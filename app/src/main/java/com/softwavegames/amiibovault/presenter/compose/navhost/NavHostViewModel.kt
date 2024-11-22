package com.softwavegames.amiibovault.presenter.compose.navhost

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavHostViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {


    fun setAppOpenedTimes(number: Int) {
        SharedPrefUtils.putInt(sharedPref, Constants.SHARED_PREFERENCES_OPENED_TIMES, number)
    }

    fun getAppOpenedTimes(): Int {
        return SharedPrefUtils.getInt(
            sharedPref,
            Constants.SHARED_PREFERENCES_OPENED_TIMES,
            0
        )
    }
}