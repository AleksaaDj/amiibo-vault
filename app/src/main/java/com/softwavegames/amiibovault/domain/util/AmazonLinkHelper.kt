package com.softwavegames.amiibovault.domain.util

import android.net.Uri
import androidx.core.net.toUri
import com.softwavegames.amiibovault.model.Amiibo

class AmazonLinkHelper {

    private val amiiboTag = "amiibovault-20"

    fun createAmazonLink(amiibo: Amiibo): Uri {
        val amiiboName = amiibo.name.replace("&", " ")
        val amiiboNameFiltered = amiiboName.replace(" ", "+")
        val amiiboType = amiibo.type
        val amiiboSeries = amiibo.gameSeries.replace(" ", "+")

        return "https://www.amazon.com/s?k=$amiiboNameFiltered+Amiibo+$amiiboType+$amiiboSeries&tag=$amiiboTag".toUri()
    }
}