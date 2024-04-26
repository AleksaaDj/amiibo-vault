package com.softwavegames.amiibovault.presenter.compose.screens.compatibility

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwavegames.amiibovault.data.repository.AmiiboRepository
import com.softwavegames.amiibovault.model.AmiiboGames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompatibilityConsolesViewModel @Inject constructor(private val repository: AmiiboRepository) :
    ViewModel() {

    private var _amiiboConsolesList = MutableLiveData<AmiiboGames?>()
    var amiiboConsolesList: LiveData<AmiiboGames?> = _amiiboConsolesList

    fun loadAmiiboConsoleInfo(amiiboTail: String) {
        viewModelScope.launch {
            val amiiboConsoles = repository.getCompatibilityConsoles(amiiboTail)

            when (amiiboConsoles.isSuccessful) {
                true -> {
                    with(amiiboConsoles.body()) {
                        val amiiboList = this?.amiibo
                        _amiiboConsolesList.postValue(amiiboList?.get(0))
                    }
                }
                else -> {
                    Log.e("ErrorAmiiboConsoles", amiiboConsoles.message())
                }
            }
        }
    }
}