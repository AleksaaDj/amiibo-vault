package com.softwavegames.amiibovault.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Release(
    val au: String? = "",
    val eu: String? = "",
    val jp: String? = "",
    val na: String? = ""
): Parcelable