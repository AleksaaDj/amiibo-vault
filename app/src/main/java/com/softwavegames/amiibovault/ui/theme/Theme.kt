package com.softwavegames.amiibovault.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColorScheme(
    background = LightBlack,
    primary = Color.LightGray,
    secondary = Color.LightGray,
    tertiary = Green40,
    primaryContainer = Color.Black,
    secondaryContainer = Color.DarkGray,
    onSecondaryContainer = Color.LightGray,
    onPrimary = Color.White,
    onBackground = Color.White,
    surface = Color.DarkGray,
    inverseSurface = DarkRed
)

private val LightColorScheme = lightColorScheme(
    background = LightWhite,
    primary = Color.DarkGray,
    secondary = Color.White,
    tertiary = Green,
    primaryContainer = Color.White,
    secondaryContainer = LightGray,
    onSecondaryContainer = Color.Gray,
    onPrimary = Black,
    onBackground = Color.Gray,
    surface = Color.Black,
    inverseSurface = LightRed

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AmiiboMvvmComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {

        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}