package com.softwavegames.amiibovault.domain.util

import android.content.Context
import android.provider.Settings

enum class NavigationTypeHelper {
    THREE_BUTTON,
    TWO_BUTTON,
    GESTURE;

    companion object {
        fun getMode(context: Context) =
            entries[Settings.Secure.getInt(context.contentResolver, "navigation_mode", -1)]
    }
}
