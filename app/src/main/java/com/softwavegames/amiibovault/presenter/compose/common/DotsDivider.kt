package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DotsDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(Color.Gray),
        )
        Spacer(modifier = Modifier.width(15.dp))

        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(Color.Gray),
        )
        Spacer(modifier = Modifier.width(15.dp))

        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(Color.Gray),
        )
    }
}