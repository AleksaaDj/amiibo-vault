package com.softwavegames.amiibovault.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.softwavegames.amiibovault.R

@Composable
fun getAvatarColorsList(): List<Color> {
    return listOf(
        colorResource(id = R.color.red_avatar),
        colorResource(id = R.color.pink_avatar),
        colorResource(id = R.color.blue_avatar),
        colorResource(id = R.color.black_avatar),
        colorResource(id = R.color.yellow_avatar),
        colorResource(id = R.color.purple_avatar),
        colorResource(id = R.color.teal_avatar),
        colorResource(id = R.color.olive_avatar),
        colorResource(id = R.color.grey_avatar),
        colorResource(id = R.color.green_avatar),
    )
}

@Composable
fun getAvatarImagesList(): List<Painter> {
    return listOf(
        painterResource(R.drawable.link_avatar),
        painterResource(R.drawable.peach_avatar),
        painterResource(R.drawable.mario_avatar),
        painterResource(R.drawable.alex_avatar),
        painterResource(R.drawable.bokoblin_avatar),
        painterResource(R.drawable.bowser_avatar),
        painterResource(R.drawable.inkling_avatar),
        painterResource(R.drawable.isabelle_avatar),
        painterResource(R.drawable.kirby_avatar),
        painterResource(R.drawable.lucina_avatar),
        painterResource(R.drawable.nabiru_avatar),
        painterResource(R.drawable.pikachu_avatar),
        painterResource(R.drawable.samus_avatar),
        painterResource(R.drawable.yoshi_avatar),
    )
}