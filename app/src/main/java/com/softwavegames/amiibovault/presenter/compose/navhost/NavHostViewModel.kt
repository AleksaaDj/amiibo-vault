package com.softwavegames.amiibovault.presenter.compose.navhost

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.SharedPrefUtils
import com.softwavegames.amiibovault.domain.util.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavHostViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {

    fun setAppOpenedAdsTimes(number: Int) {
        SharedPrefUtils.putInt(sharedPref, Constants.SHARED_PREFERENCES_OPENED_ADS_TIMES, number)
    }

    fun getAppOpenedAdsTimes(): Int {
        return SharedPrefUtils.getInt(
            sharedPref,
            Constants.SHARED_PREFERENCES_OPENED_ADS_TIMES,
            1
        )
    }

    fun setAppOpenedRateTimes(number: Int) {
        SharedPrefUtils.putInt(sharedPref, Constants.SHARED_PREFERENCES_OPENED_RATE_TIMES, number)
    }

    fun getAppOpenedRateTimes(): Int {
        return SharedPrefUtils.getInt(
            sharedPref,
            Constants.SHARED_PREFERENCES_OPENED_RATE_TIMES,
            1
        )
    }

    fun setRateClicked() {
        SharedPrefUtils.putBoolean(sharedPref, Constants.SHARED_PREFERENCES_RATE_CLICKED, true)
    }

    fun isRateClicked(): Boolean {
        return SharedPrefUtils.getBoolean(
            sharedPref,
            Constants.SHARED_PREFERENCES_RATE_CLICKED,
            false
        )
    }

    fun setThemeMode(isDarkMode: Boolean) {
        val theme = when (isDarkMode) {
            true -> AppCompatDelegate.MODE_NIGHT_NO
            false -> AppCompatDelegate.MODE_NIGHT_YES
        }
        AppCompatDelegate.setDefaultNightMode(theme)
        ThemeState.darkModeState.value = isDarkMode
        SharedPrefUtils.putBoolean(sharedPref, Constants.SHARED_PREFERENCES_IS_DARK_MODE, isDarkMode)
    }
}