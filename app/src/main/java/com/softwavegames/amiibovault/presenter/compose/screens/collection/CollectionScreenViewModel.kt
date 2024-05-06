package com.softwavegames.amiibovault.presenter.compose.screens.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CollectionScreenViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {

    private var _amiiboListCollection = MutableLiveData<List<AmiiboCollection>?>()
    var amiiboListCollection: LiveData<List<AmiiboCollection>?> = _amiiboListCollection

    private var _amiiboListWishlist = MutableLiveData<List<AmiiboWishlist>?>()
    var amiiboListWishlist: LiveData<List<AmiiboWishlist>?> = _amiiboListWishlist

    init {
        getAmiiboCollection()
        getAmiiboWishlist()
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
}