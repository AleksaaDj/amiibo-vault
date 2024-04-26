package com.softwavegames.amiibovault.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Amiibo(
    val amiiboSeries: String = "",
    val character: String = "",
    val gameSeries: String = "",
    val head: String = "",
    val image: String = "",
    val name: String = "",
    val release: Release? = null,
    @PrimaryKey val tail: String = "",
    val type: String = "",
): Parcelable

@Parcelize
@Entity
data class AmiiboWishlist(
    val amiiboSeries: String,
    val character: String,
    val gameSeries: String,
    val head: String,
    val image: String,
    val name: String,
    val release: Release,
    @PrimaryKey val tail: String,
    val type: String,
): Parcelable