package com.softwavegames.amiibovault.presenter.compose.screens.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AmiiboSearchViewModel @Inject constructor(private val repository: AmiiboRepository) : ViewModel() {

    private var _amiiboList = MutableLiveData<List<Amiibo>?>()
    var amiiboList: LiveData<List<Amiibo>?> = _amiiboList

    private var _amiiboLatest = MutableLiveData<Amiibo?>()
    var amiiboLatest: LiveData<Amiibo?> = _amiiboLatest

    init {
        loadAmiibos()
        setupFirebase()
    }
    private fun loadAmiibos() {
        repository.selectAmiiboDbList().onEach { localList ->
            if (localList.isEmpty()) {
                try {
                    val amiiboListResponse = repository.getAmiiboList("")
                    when (amiiboListResponse.isSuccessful) {
                        true -> {
                            with(amiiboListResponse.body()) {
                                val amiiboList = this?.amiibo
                                amiiboList?.let { addAmiiboListToDatabase(amiiboList = it) }
                                _amiiboList.postValue(amiiboList)
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
        repository.searchAmiiboList(name).onEach { localList ->
            amiiboLocalList = localList
            _amiiboList.postValue(amiiboLocalList)
        }.launchIn(viewModelScope)
    }

    private suspend fun addAmiiboListToDatabase(amiiboList: List<Amiibo>) {
        repository.upsertAmiiboList(amiiboList)
    }

    private fun setupFirebase() {
        val database: DatabaseReference = Firebase.database.reference

        database.child(Constants.FIREBASE_DB_AMIIBO).get().addOnSuccessListener { dataSnapshot ->
            val amiibo = dataSnapshot.getValue(Amiibo::class.java)
            _amiiboLatest.value = amiibo
            loadAmiibos()
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }
}