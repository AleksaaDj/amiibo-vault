package com.softwavegames.amiibovault.domain.util

import android.content.SharedPreferences

object SharedPrefUtils {

    fun getInt(mSharedPref: SharedPreferences, key: String?, defValue: Int): Int {
        return mSharedPref.getInt(key, defValue)
    }

    fun putInt(mSharedPref: SharedPreferences, key: String?, value: Int) {
        val prefsEditor = mSharedPref.edit()
        prefsEditor.putInt(key, value)
        prefsEditor.apply()
    }

    fun getBoolean(mSharedPref: SharedPreferences, key: String?, defValue: Boolean): Boolean {
        return mSharedPref.getBoolean(key, defValue)
    }

    fun putBoolean(mSharedPref: SharedPreferences, key: String?, value: Boolean) {
        val prefsEditor = mSharedPref.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun putSet(mSharedPref: SharedPreferences, key: String?, value: Set<String>) {
        val prefsEditor = mSharedPref.edit()
        prefsEditor.putStringSet(key, value)
        prefsEditor.apply()
    }

    fun getSet(mSharedPref: SharedPreferences, key: String?, defValue: Set<String>): Set<String>? {
        return mSharedPref.getStringSet(key, defValue)
    }

}