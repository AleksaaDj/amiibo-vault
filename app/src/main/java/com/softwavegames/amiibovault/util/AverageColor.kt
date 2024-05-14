package com.softwavegames.amiibovault.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils


object AverageColor {

    fun getAverageColor(imageBitmap: ImageBitmap): Color {
        val averageColor: Color
        // Create resized Bitmap
        val compatibleBitmap = Bitmap.createScaledBitmap(
            imageBitmap.asAndroidBitmap()
                .copy(Bitmap.Config.ARGB_8888, false), 50, 50, false
        )

        // Retrieve the pixels from the compatible Bitmap
        val pixels = IntArray(compatibleBitmap.width * compatibleBitmap.height)
        compatibleBitmap.getPixels(
            pixels, 0, compatibleBitmap.width, 0, 0,
            compatibleBitmap.width, compatibleBitmap.height
        )

        var redSum = 0
        var greenSum = 0
        var blueSum = 0

        // Calculate the sum of RGB values
        for (pixel in pixels) {
            val red = android.graphics.Color.red(pixel)
            val green = android.graphics.Color.green(pixel)
            val blue = android.graphics.Color.blue(pixel)

            redSum += red
            greenSum += green
            blueSum += blue
        }

        // Calculate the average RGB values
        val pixelCount = pixels.size
        val averageRed = redSum / pixelCount
        val averageGreen = greenSum / pixelCount
        val averageBlue = blueSum / pixelCount

        // Set the average color as the result
        averageColor = Color(averageRed, averageGreen, averageBlue)

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(averageColor.toArgb(), hsl)

        // Create a new color with the modified lightness component
        val lighterColor = ColorUtils.HSLToColor(
            floatArrayOf(
                hsl[0] + 0.2f,
                hsl[1] + 0.3f,
                hsl[2] + 0.2f
            )
        )
        return Color(lighterColor)
    }
}