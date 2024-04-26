package com.softwavegames.amiibovault.presenter.compose.screens.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.softwavegames.amiibovault.Constants
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.model.Amiibo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AmiiboSearchViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {


    private var _amiiboList = MutableLiveData<List<Amiibo>?>()
    var amiiboList: LiveData<List<Amiibo>?> = _amiiboList

    private var _amiiboLatest = MutableLiveData<Amiibo?>()
    var amiiboLatest: LiveData<Amiibo?> = _amiiboLatest

    init {
        loadAmiibos("")
        setupFirebase()
    }

    fun loadAmiibos(name: String) {
        viewModelScope.launch {
            val amiiboListResponse = repository.getAmiiboList(name)
            when (amiiboListResponse.isSuccessful) {
                true -> {
                    with(amiiboListResponse.body()) {
                        val amiiboList = this?.amiibo
                        _amiiboList.postValue(amiiboList)
                    }
                }

                else -> {
                    Log.e("ErrorAmiiboList", amiiboListResponse.message())
                }
            }
        }
    }

    private fun setupFirebase() {
        val database: DatabaseReference = Firebase.database.reference

        database.child(Constants.FIREBASE_DB_AMIIBO).get().addOnSuccessListener { dataSnapshot ->
            val amiibo = dataSnapshot.getValue(Amiibo::class.java)
            _amiiboLatest.value = amiibo
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }
}