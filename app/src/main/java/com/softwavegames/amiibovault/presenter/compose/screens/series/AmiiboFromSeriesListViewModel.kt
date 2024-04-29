package com.softwavegames.amiibovault.presenter.compose.screens.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.model.Amiibo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AmiiboFromSeriesListViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {

    private var _amiiboList = MutableLiveData<List<Amiibo>?>()
    var amiiboList: LiveData<List<Amiibo>?> = _amiiboList

    fun loadAmiibos(gameSeries: String) {
        repository.getAmiiboFromSeriesList(gameSeries).onEach {
            _amiiboList.value = it
        }.launchIn(viewModelScope)
    }
}