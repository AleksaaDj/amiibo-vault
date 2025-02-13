package com.softwavegames.amiibovault.presenter.compose.screens.collection

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.domain.util.CompositeImageHelper
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionScreenViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {

    private var _amiiboListCollection = MutableLiveData<List<AmiiboCollection>?>()
    var amiiboListCollection: LiveData<List<AmiiboCollection>?> = _amiiboListCollection

    private var _amiiboListWishlist = MutableLiveData<List<AmiiboWishlist>?>()
    var amiiboListWishlist: LiveData<List<AmiiboWishlist>?> = _amiiboListWishlist

    var sortTypeCollection = MutableLiveData<String?>()
    var sortTypeWishList = MutableLiveData<String?>()

    private var _numberOfAmiiboWorldWide = MutableLiveData<Int>()
    var numberOfAmiiboWorldWide: LiveData<Int> = _numberOfAmiiboWorldWide


    fun getAmiiboFilteredFromCollection(type: String, series: String) {
        var amiiboLocalList: List<AmiiboCollection>
        var amiiboFilteredList: List<AmiiboCollection>
        repository.getAmiiboListFromDbMyCollection().onEach { localList ->
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
            _amiiboListCollection.postValue(amiiboFilteredList)
            sortTypeCollection.value?.let { sortAmiiboCollectionList(it, amiiboFilteredList) }
        }.launchIn(viewModelScope)
    }

    fun sortAmiiboCollectionList(sortType: String, amiiboList: List<AmiiboCollection>?) {
        val amiiboLocalList = amiiboList ?: emptyList()
        val amiiboSortedList: List<AmiiboCollection> = when (sortType) {
            Constants.SORT_TYPE_NAME_ASC -> {
                amiiboLocalList.sortedBy { it.name }
            }

            Constants.SORT_TYPE_NAME_DSC -> {
                amiiboLocalList.sortedByDescending { it.name }
            }

            Constants.SORT_TYPE_RELEASE_ASC -> {
                amiiboLocalList.sortedWith(compareBy<AmiiboCollection> { it.release?.jp == "null" }.thenBy { it.release?.jp })
            }

            Constants.SORT_TYPE_RELEASE_DSC -> {
                amiiboLocalList.sortedWith(compareBy<AmiiboCollection> { it.release?.jp == "null" }.thenByDescending { it.release?.jp })
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
        _amiiboListCollection.postValue(amiiboSortedList)
    }


    fun getAmiiboFilteredFromWishlist(type: String, series: String) {
        var amiiboLocalList: List<AmiiboWishlist>
        var amiiboFilteredList: List<AmiiboWishlist>
        repository.getAmiiboListFromDdWishlist().onEach { localList ->
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
            _amiiboListWishlist.postValue(amiiboFilteredList)
            sortTypeWishList.value?.let { sortAmiiboWishlist(it, amiiboFilteredList) }
        }.launchIn(viewModelScope)
    }

    fun sortAmiiboWishlist(sortType: String, amiiboList: List<AmiiboWishlist>?) {
        val amiiboLocalList = amiiboList ?: emptyList()
        val amiiboSortedList: List<AmiiboWishlist> = when (sortType) {
            Constants.SORT_TYPE_NAME_ASC -> {
                amiiboLocalList.sortedBy { it.name }
            }

            Constants.SORT_TYPE_NAME_DSC -> {
                amiiboLocalList.sortedByDescending { it.name }
            }

            Constants.SORT_TYPE_RELEASE_ASC -> {
                amiiboLocalList.sortedWith(compareBy<AmiiboWishlist> { it.release.jp == "null" }.thenBy { it.release.jp })
            }

            Constants.SORT_TYPE_RELEASE_DSC -> {
                amiiboLocalList.sortedWith(compareBy<AmiiboWishlist> { it.release.jp == "null" }.thenByDescending { it.release.jp })
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
        _amiiboListWishlist.postValue(amiiboSortedList)
    }

    fun getAmiiboNumberWorldwide(type: String, series: String) {
        var amiiboLocalList: List<Amiibo>
        var amiiboFilteredList: List<Amiibo>
        repository.getAmiiboListFromDbHome().onEach { localList ->
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
            _numberOfAmiiboWorldWide.postValue(amiiboFilteredList.size)
        }.launchIn(viewModelScope)
    }

    fun createAndDownloadCompositeImage(context: Context) {
        viewModelScope.launch {
            amiiboListCollection.value?.let {
                CompositeImageHelper().createAndSaveCompositeImage(
                    it,
                    context
                )
            }
        }
    }
}