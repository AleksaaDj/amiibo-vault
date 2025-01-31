package com.softwavegames.amiibovault.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.softwavegames.amiibovault.R


fun getAvatarResourceByIndex(avatarId: Int): Int {
    return when (avatarId) {
        AvatarsIndex.LINK -> R.drawable.link_avatar
        AvatarsIndex.PEACH -> R.drawable.peach_avatar
        AvatarsIndex.MARIO -> R.drawable.mario_avatar
        AvatarsIndex.ALEX -> R.drawable.alex_avatar
        AvatarsIndex.BOKOBLIN -> R.drawable.bokoblin_avatar
        AvatarsIndex.BOWSER -> R.drawable.bowser_avatar
        AvatarsIndex.INKLING -> R.drawable.inkling_avatar
        AvatarsIndex.ISABELLE -> R.drawable.isabelle_avatar
        AvatarsIndex.KIRBY -> R.drawable.kirby_avatar
        AvatarsIndex.LUCINA -> R.drawable.lucina_avatar
        AvatarsIndex.NABIRU -> R.drawable.nabiru_avatar
        AvatarsIndex.PIKACHU -> R.drawable.pikachu_avatar
        AvatarsIndex.SAMUS -> R.drawable.samus_avatar
        AvatarsIndex.YOSHI -> R.drawable.yoshi_avatar
        else -> R.drawable.ic_avatar_placeholder
    }
}

@Composable
fun getAvatarColorByIndex(backgroundColorIndex: Int): Color {
    return when (backgroundColorIndex) {
        ColorsIndex.RED_INDEX -> colorResource(id = R.color.red_avatar)
        ColorsIndex.PINK_INDEX -> colorResource(id = R.color.pink_avatar)
        ColorsIndex.BLUE_INDEX -> colorResource(id = R.color.blue_avatar)
        ColorsIndex.BLACK_INDEX -> colorResource(id = R.color.black_avatar)
        ColorsIndex.YELLOW_INDEX -> colorResource(id = R.color.yellow_avatar)
        ColorsIndex.PURPLE_INDEX -> colorResource(id = R.color.purple_avatar)
        ColorsIndex.TEAL_INDEX -> colorResource(id = R.color.teal_avatar)
        ColorsIndex.OLIVE_INDEX -> colorResource(id = R.color.olive_avatar)
        ColorsIndex.GREY_INDEX -> colorResource(id = R.color.grey_avatar)
        ColorsIndex.GREEN_INDEX -> colorResource(id = R.color.green_avatar)
        else -> Color.DarkGray
    }
}

