package com.softwavegames.amiibovault.presenter.compose.screens.search

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.domain.util.AverageColor
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.Utils
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class AmiiboSearchViewModel @Inject constructor(
    private val repository: AmiiboRepository,
    @ApplicationContext val context: Context
) :
    ViewModel() {

    var sortType = MutableLiveData<String?>()

    private var _amiiboList = MutableLiveData<List<Amiibo>?>()
    var amiiboList: LiveData<List<Amiibo>?> = _amiiboList

    private var _amiiboLatest = MutableLiveData<Amiibo?>()
    var amiiboLatest: LiveData<Amiibo?> = _amiiboLatest

    private var _amiiboListCollection = MutableLiveData<List<AmiiboCollection>?>()
    var amiiboListCollection: LiveData<List<AmiiboCollection>?> = _amiiboListCollection

    private var _amiiboListWishlist = MutableLiveData<List<AmiiboWishlist>?>()
    var amiiboListWishlist: LiveData<List<AmiiboWishlist>?> = _amiiboListWishlist


    init {
        loadAmiibos()
        getAmiiboCollection()
        getAmiiboWishlist()
        getFeaturedAmiiboFromFirebase()
    }

    private fun loadAmiibos() {
        repository.getAmiiboListFromDbHome().onEach { localList ->
            if (localList.isEmpty()) {
                val amiiboList = getRemoteAmiiboList()
                addAmiiboListToDatabase(amiiboList = amiiboList)
            } else {
                val amiiboList = getRemoteAmiiboList()
                if (amiiboList.size > localList.size) {
                    addAmiiboListToDatabase(amiiboList = amiiboList)
                } else {
                    _amiiboList.postValue(localList)
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun getRemoteAmiiboList(): List<Amiibo> {
        var amiiboListRemote = emptyList<Amiibo>()
        try {
            val amiiboListResponse = repository.getAmiiboList()
            when (amiiboListResponse.isSuccessful) {
                true -> {
                    with(amiiboListResponse.body()) {
                        if (this?.amiibo != null) {
                            amiiboListRemote = this.amiibo
                        }
                    }
                }

                else -> {
                    Log.e("ErrorAmiiboList", amiiboListResponse.message())
                }
            }
        } catch (e: IOException) {
            Log.e("ErrorAmiiboList", "No Internet connection")
        }
        return amiiboListRemote
    }

    fun searchAmiibo(name: String) {
        var amiiboLocalList: List<Amiibo>
        repository.searchAmiiboHome(name).onEach { localList ->
            amiiboLocalList = localList
            _amiiboList.postValue(amiiboLocalList)
            sortType.value?.let { sortAmiiboList(it, amiiboLocalList) }
        }.launchIn(viewModelScope)
    }

    fun searchAmiiboFiltered(name: String, type: String, series: String) {
        var amiiboLocalList: List<Amiibo>
        var amiiboFilteredList: List<Amiibo>
        repository.searchAmiiboHome(name).onEach { localList ->
            amiiboLocalList = localList
            amiiboFilteredList = if (type.isNotEmpty() && series.isNotEmpty()) {
                amiiboLocalList.filter {
                    it.type.contains(type) && it.amiiboSeries.contains(series)
                }
            } else if (type.isNotEmpty() && series.isEmpty()) {
                amiiboLocalList.sortedBy { it.name }
                amiiboLocalList.filter {
                    it.type.contains(type)
                }
            } else if (type.isEmpty() && series.isNotEmpty()) {
                amiiboLocalList.filter {
                    it.amiiboSeries.contains(series)
                }
            } else {
                amiiboLocalList
            }
            _amiiboList.postValue(amiiboFilteredList)
            sortType.value?.let { sortAmiiboList(it,amiiboFilteredList) }
        }.launchIn(viewModelScope)
    }

    fun sortAmiiboList(sortType: String, amiiboList: List<Amiibo>?) {
        val amiiboLocalList = amiiboList ?: emptyList()
        val amiiboSortedList: List<Amiibo> = when (sortType) {
            Constants.SORT_TYPE_NAME_ASC -> {
                amiiboLocalList.sortedBy { it.name }
            }

            Constants.SORT_TYPE_NAME_DSC -> {
                amiiboLocalList.sortedByDescending { it.name }
            }

            Constants.SORT_TYPE_RELEASE_ASC -> {
                amiiboLocalList.sortedWith(compareBy<Amiibo> { it.release?.jp == "null" }.thenBy { it.release?.jp })
            }

            Constants.SORT_TYPE_RELEASE_DSC -> {
                amiiboLocalList.sortedWith(compareBy<Amiibo> { it.release?.jp == "null" }.thenByDescending { it.release?.jp })
            }

            Constants.SORT_TYPE_SET_ASC -> {
                amiiboLocalList.sortedBy { it.gameSeries }
            }

            Constants.SORT_TYPE_SET_DSC -> {
                amiiboLocalList.sortedByDescending { it.gameSeries }
            }

            else -> {
                amiiboLocalList
            }
        }
        Log.d("Sort", "sort")
        _amiiboList.postValue(amiiboSortedList)
    }

    private suspend fun addAmiiboListToDatabase(amiiboList: List<Amiibo>) {
        repository.upsertAmiiboDbHomeList(amiiboList)
    }

    private fun getFeaturedAmiiboFromFirebase() {
        var currentAmiibo: Amiibo? = null
        repository.getCurrentFeaturedAmiibo().onEach {
            if (it.isNotEmpty()) {
                currentAmiibo = it[0]
                _amiiboLatest.postValue(currentAmiibo)
            }
        }.launchIn(viewModelScope)

        val database: DatabaseReference = Firebase.database.reference
        database.child(Constants.FIREBASE_DB_AMIIBO).get()
            .addOnSuccessListener { dataSnapshot ->
                val amiibo = dataSnapshot.getValue(Amiibo::class.java)
                if (amiibo != null) {
                    if (amiibo.tail != currentAmiibo?.tail) {
                        updateFeaturedAmiiboLocal(currentAmiibo, amiibo)
                    }
                }
                loadAmiibos()
            }.addOnFailureListener { e ->
                Log.e("firebase", "Error getting data", e)
            }
    }

    private fun updateFeaturedAmiiboLocal(currentAmiibo: Amiibo?, newAmiibo: Amiibo) {
        urlToBitmap(
            imageURL = newAmiibo.image,
            onSuccess = {
                val croppedBitmap = Bitmap.createBitmap(it, 0, 100, it.width - 100, it.height - 100)
                val color = AverageColor.getAverageColor(croppedBitmap.asImageBitmap())
                if (currentAmiibo == null) {
                    repository.getAmiiboSpecific(newAmiibo.tail).onEach { newAmiibo ->
                        if (newAmiibo.isNotEmpty()) {
                            _amiiboLatest.postValue(
                                newAmiibo[0].copy(
                                    color = color.toArgb(),
                                )
                            )
                        }
                    }.launchIn(viewModelScope)
                }
                repository.setFeaturedAmiibo(
                    featured = true,
                    color = color.toArgb(),
                    tail = newAmiibo.tail
                )
                if (currentAmiibo != null) {
                    repository.removeCurrentFeaturedAmiibo(currentAmiibo.tail)
                }
            },
            onError = {
                Log.e("latest_amiibo", it.message.toString())
            }
        )
    }

    private fun getAmiiboCollection() {
        repository.getAmiiboListFromDbMyCollection().onEach {
            _amiiboListCollection.value = it
        }.launchIn(viewModelScope)
    }

    private fun getAmiiboWishlist() {
        repository.getAmiiboListFromDdWishlist().onEach {
            _amiiboListWishlist.value = it
        }.launchIn(viewModelScope)
    }

    suspend fun upsertAmiiboToWishList(amiibo: Amiibo) {
        val amiiboWishlist = Utils().convertAmiiboToAmiiboWishlist(amiibo)
        repository.upsertAmiiboDbWishlist(amiibo = amiiboWishlist)
    }

    suspend fun deleteAmiiboFromWishList(amiibo: Amiibo) {
        val amiiboWishlist = Utils().convertAmiiboToAmiiboWishlist(amiibo)
        repository.deleteAmiiboFromDbWishlist(amiibo = amiiboWishlist)
    }

    private fun urlToBitmap(
        imageURL: String,
        onSuccess: (bitmap: Bitmap) -> Unit,
        onError: (error: Throwable) -> Unit
    ) {
        var bitmap: Bitmap? = null
        val loadBitmap = viewModelScope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageURL)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                bitmap = (result.drawable as BitmapDrawable).bitmap
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
            }
        }
        loadBitmap.invokeOnCompletion { throwable ->
            bitmap?.let {
                onSuccess(it)
            } ?: throwable?.let {
                onError(it)
            }
        }
    }
}