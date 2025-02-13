package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.softwavegames.amiibovault.R

@Composable
fun LogoAnim(onAnimationFinished: () -> Unit) {
    val alphaValue = remember { Animatable(0f) }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.opening),
            contentDescription = "Boats",
            alpha = alphaValue.value,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        LaunchedEffect(key1 = this) {
            alphaValue.animateTo(
                1f,
                animationSpec = tween(1000),
            )
            alphaValue.animateTo(
                0f,
                animationSpec = tween(1100),
            )
            onAnimationFinished()
        }
    }
}