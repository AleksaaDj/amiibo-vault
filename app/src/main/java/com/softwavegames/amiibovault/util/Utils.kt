package com.softwavegames.amiibovault.util

import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Utils {

    fun convertAmiiboToAmiiboWishlist(fromAmiibo: Amiibo): AmiiboWishlist {
        var amiiboWishlist: AmiiboWishlist
        with(fromAmiibo) {
            amiiboWishlist = AmiiboWishlist(
                amiiboSeries,
                character,
                gameSeries,
                head,
                image,
                name,
                release!!,
                tail,
                type
            )
        }
        return amiiboWishlist
    }

    fun convertAmiiboToAmiiboCollection(fromAmiibo: Amiibo): AmiiboCollection {
        var amiiboCollection: AmiiboCollection
        with(fromAmiibo) {
            amiiboCollection = AmiiboCollection(
                amiiboSeries,
                character,
                gameSeries,
                head,
                image,
                name,
                release!!,
                tail,
                type
            )
        }
        return amiiboCollection
    }

    fun convertAmiiboWishlistToAmiibo(fromAmiibo: AmiiboWishlist): Amiibo {
        var amiibo: Amiibo
        with(fromAmiibo) {
            amiibo = Amiibo(
                amiiboSeries,
                character,
                gameSeries,
                head,
                image,
                name,
                release,
                tail,
                type
            )
        }
        return amiibo
    }

    fun convertAmiiboCollectionToAmiibo(fromAmiibo: AmiiboCollection): Amiibo {
        var amiibo: Amiibo
        with(fromAmiibo) {
            amiibo = Amiibo(
                amiiboSeries,
                character,
                gameSeries,
                head,
                image,
                name,
                release,
                tail,
                type
            )
        }
        return amiibo
    }

    fun getDayAndMonthFromString(date: String?): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = LocalDate.parse(date, dateFormatter)
        val dayAndMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM")
        val dayAndMonth = formattedDate.format(dayAndMonthFormatter)

        return dayAndMonth
    }

    fun getYearFromString(date: String?): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = LocalDate.parse(date, dateFormatter)
        val yearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")
        val year = formattedDate.format(yearFormatter)

        return year
    }

}