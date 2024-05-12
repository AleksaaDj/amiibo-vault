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
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.util.AverageColor
import com.softwavegames.amiibovault.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AmiiboSearchViewModel @Inject constructor(
    private val repository: AmiiboRepository,
    @ApplicationContext val context: Context
) :
    ViewModel() {


    private var _amiiboList = MutableLiveData<List<Amiibo>?>()
    var amiiboList: LiveData<List<Amiibo>?> = _amiiboList

    private var _amiiboLatest = MutableLiveData<Amiibo?>()
    var amiiboLatest: LiveData<Amiibo?> = _amiiboLatest

    init {
        loadAmiibos()
        getFeaturedAmiiboFromFirebase()
    }

    private fun loadAmiibos() {
        repository.getAmiiboListFromDbHome().onEach { localList ->
            if (localList.isEmpty()) {
                try {
                    val amiiboListResponse = repository.getAmiiboList()
                    when (amiiboListResponse.isSuccessful) {
                        true -> {
                            with(amiiboListResponse.body()) {
                                val amiiboList = this?.amiibo
                                amiiboList?.let { addAmiiboListToDatabase(amiiboList = it) }
                            }
                        }
                        else -> {
                            Log.e("ErrorAmiiboList", amiiboListResponse.message())
                        }
                    }
                } catch (e: UnknownHostException) {
                    Log.e("ErrorAmiiboList", "No Internet connection")
                }
            } else {
                _amiiboList.postValue(localList)
            }
        }.launchIn(viewModelScope)
    }

    fun searchAmiibo(name: String) {
        var amiiboLocalList: List<Amiibo>
        repository.searchAmiiboHome(name).onEach { localList ->
            amiiboLocalList = localList
            _amiiboList.postValue(amiiboLocalList)
        }.launchIn(viewModelScope)
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
                repository.updateFeaturedAmiibo(
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
//Filter Amiibo
/*fun getAmiiboFiltered(series: String, type: String) {
    val amiiboSeries = series.ifEmpty { null }
    val amiiboType = type.ifEmpty { null }

    if (amiiboSeries != null && amiiboType != null) {
        repository.getAmiiboListFilteredBoth(amiiboSeries, amiiboType).onEach { resultList ->
            _amiiboList.postValue(resultList)
        }.launchIn(viewModelScope)
    } else if (amiiboSeries == null && amiiboType == null) {
        loadAmiibos()
    } else {
        repository.getAmiiboListFilteredOne(amiiboSeries, amiiboType).onEach { resultList ->
            _amiiboList.postValue(resultList)
        }.launchIn(viewModelScope)
    }
}*/