package com.softwavegames.amiibovault.domain.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.AmiiboCollection
import java.io.OutputStream
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

class CompositeImageHelper {

    private val targetWidth = 3500
    private val targetHeight = 2960
    private val padding = 20 // Padding between images
    private val topBottomPadding = 380 // Extra padding at top and bottom


    suspend fun createAndSaveCompositeImage(
        amiiboList: List<AmiiboCollection>,
        context: Context
    ): Boolean {
        val imageLoader = ImageLoader(context)

        val bitmaps = amiiboList.mapNotNull { amiibo ->
            val request = ImageRequest.Builder(context)
                .data(amiibo.image)
                .allowHardware(false) // Important for returning software bitmaps
                .build()

            val result = imageLoader.execute(request)
            (result as? SuccessResult)?.drawable?.toBitmap()
        }

        if (bitmaps.isEmpty()) return false

        val compositeBitmap = createAdaptiveGridBitmap(bitmaps, context)
        return saveBitmapToFile(compositeBitmap, context)
    }

    private fun createAdaptiveGridBitmap(bitmaps: List<Bitmap>, context: Context): Bitmap {
        val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        val vectorDrawable = VectorDrawableCompat.create(
            context.resources,
            R.drawable.collection_generated_background,
            null
        )
        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, targetWidth, targetHeight)
            vectorDrawable.draw(canvas)
        } else {
            val backgroundPaint = Paint().apply { color = Color.White.toArgb() }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), backgroundPaint)
        }

        val columns = calculateOptimalColumns(bitmaps.size)
        val rows = ceil(bitmaps.size.toDouble() / columns).toInt()

        val maxCellWidth = (targetWidth - (columns + 1) * padding) / columns
        val maxCellHeight = (targetHeight - 2 * topBottomPadding - (rows + 1) * padding) / rows
        val cellSize = min(maxCellWidth, maxCellHeight)

        val totalGridWidth = columns * cellSize + (columns - 1) * padding
        val totalGridHeight = rows * cellSize + (rows - 1) * padding

        val horizontalOffset = (targetWidth - totalGridWidth) / 2
        val verticalOffset = (targetHeight - totalGridHeight) / 2

        bitmaps.forEachIndexed { index, bitmap ->
            val row = index / columns
            val col = index % columns

            val left = horizontalOffset + col * (cellSize + padding)
            val top = verticalOffset + row * (cellSize + padding)

            drawScaledImage(canvas, bitmap, Rect(left, top, left + cellSize, top + cellSize))
        }

        bitmaps.forEach { it.recycle() }
        return outputBitmap
    }

    private fun saveBitmapToFile(bitmap: Bitmap, context: Context): Boolean {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/amiibo Vault")
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "amiibo_collection${UUID.randomUUID()}")

        val uri: Uri? =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, values, null, null)
        }
        return true
    }

    private fun calculateOptimalColumns(totalImages: Int): Int {
        val estimatedCellWidth = when (totalImages) {
            in 0..20 -> 600
            in 21..50 -> 350
            in 51..100 -> 250
            in 101..300 -> 150
            in 301..500 -> 130
            in 501..600 -> 100
            in 601..800 -> 80
            else -> 60
        }
        val maxColumnsBasedOnWidth = (targetWidth + padding) / (estimatedCellWidth + padding)
        return minOf(totalImages, maxColumnsBasedOnWidth)
    }

    private fun drawScaledImage(canvas: Canvas, bitmap: Bitmap, targetRect: Rect) {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height
        val targetRatio = targetRect.width().toFloat() / targetRect.height()

        val (width, height) = if (aspectRatio > targetRatio) {
            targetRect.width() to (targetRect.width() / aspectRatio).roundToInt()
        } else {
            (targetRect.height() * aspectRatio).roundToInt() to targetRect.height()
        }

        val left = targetRect.left + (targetRect.width() - width) / 2
        val top = targetRect.top + (targetRect.height() - height) / 2

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)
        scaledBitmap.recycle()
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}