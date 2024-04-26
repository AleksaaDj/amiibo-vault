package com.softwavegames.amiibovault

import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboWishlist

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
}