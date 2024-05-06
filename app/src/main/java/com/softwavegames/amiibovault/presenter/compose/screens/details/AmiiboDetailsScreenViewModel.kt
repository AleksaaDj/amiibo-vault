package com.softwavegames.amiibovault.presenter.compose.screens.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwavegames.amiibovault.util.Utils
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.model.Amiibo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AmiiboDetailsScreenViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {

    private var _amiiboSavedMyCollection = MutableLiveData<Boolean>()
    var amiiboSavedMyCollection: LiveData<Boolean?> = _amiiboSavedMyCollection

    private var _amiiboSavedWishlist = MutableLiveData<Boolean>()
    var amiiboSavedWishlist: LiveData<Boolean?> = _amiiboSavedWishlist

    suspend fun upsertAmiiboToMyCollection(amiibo: Amiibo) {
        val amiiboCollection = Utils().convertAmiiboToAmiiboCollection(amiibo)
        repository.upsertAmiiboDbMyCollection(amiibo = amiiboCollection)
    }

    suspend fun deleteAmiiboFromMyCollection(amiibo: Amiibo) {
        val amiiboCollection = Utils().convertAmiiboToAmiiboCollection(amiibo)
        repository.deleteAmiiboFromDbMyCollection(amiibo = amiiboCollection)
    }

    fun checkIsAmiiboSavedInMyCollection(tail: String) {
        repository.getAmiiboFromDbMyCollection(tail).onEach {
            _amiiboSavedMyCollection.value = it.isNotEmpty()
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

    fun checkIsAmiiboSavedInWishList(tail: String) {
        repository.getAmiiboFromDbWishlist(tail).onEach {
            _amiiboSavedWishlist.value = it.isNotEmpty()
        }.launchIn(viewModelScope)
    }
}