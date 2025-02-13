package com.softwavegames.amiibovault.presenter.compose.common.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo

@Composable
fun FeaturedAmiiboCard(
    amiiboLatest: Amiibo?,
    navigateToDetails: (Amiibo) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        if (amiiboLatest != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AmiiboFeaturedDetails(
                    amiiboLatest = amiiboLatest,
                    navigateToDetails,
                )
            }
            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(),
                model = ImageRequest.Builder(context).data(amiiboLatest.image).build(),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        }
    }
}

@Composable
private fun AmiiboFeaturedDetails(
    amiiboLatest: Amiibo,
    navigateToDetails: (Amiibo) -> Unit,
) {

    val amiiboAverageColor =
        if (amiiboLatest.color == 0) MaterialTheme.colorScheme.tertiary else Color(amiiboLatest.color)

    GlowingCard(
        modifier = Modifier
            .wrapContentHeight()
            .offset(y = 40.dp)
            .fillMaxWidth()
            .clickable {
                navigateToDetails(amiiboLatest)
            },
        glowingColor = amiiboAverageColor,
        containerColor = amiiboAverageColor,
        cornersRadius = 10.dp
    ) {

        Column(
            modifier = Modifier
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.featured_amiibo),
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = amiiboLatest.character,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = amiiboLatest.gameSeries,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        }
    }
}


