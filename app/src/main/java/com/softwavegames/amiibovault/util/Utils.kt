package com.softwavegames.amiibovault.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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

    fun urlToBitmap(
        scope: CoroutineScope,
        imageURL: String,
        context: Context,
        onSuccess: (bitmap: Bitmap) -> Unit,
        onError: (error: Throwable) -> Unit
    ) {
        var bitmap: Bitmap? = null
        val loadBitmap = scope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageURL)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                bitmap = (result.drawable as BitmapDrawable).bitmap
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
            }
        }
        loadBitmap.invokeOnCompletion { throwable ->
            bitmap?.let {
                onSuccess(it)
            } ?: throwable?.let {
                onError(it)
            } ?: onError(Throwable("Undefined Error"))
        }
    }
}