package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo

@Composable
fun AmiiboGridItem(
    amiibo: Amiibo,
    onAmiiboClick: (Amiibo) -> Unit,
) {
    val context = LocalContext.current

    Column {
        AsyncImage(
            modifier = Modifier
                .padding(top = 3.dp)
                .size(114.dp)
                .clickable {
                    onAmiiboClick(amiibo)
                },
            model = ImageRequest.Builder(context).data(amiibo.image).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Inside,
            error = painterResource(R.drawable.ic_image_placeholder)
        )

        Text(
            modifier = Modifier
                .padding(top = 5.dp)
                .width(105.dp),
            text = amiibo.character,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}