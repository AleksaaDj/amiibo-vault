package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
fun LatestAmiiboCard(
    amiiboLatest: Amiibo?,
    navigateToDetails: (Amiibo) -> Unit
) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(start = 20.dp, end = 20.dp, top = 5.dp)
        ) {
            if (amiiboLatest != null) {
                Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .height(100.dp)
                        .offset(y = 40.dp)
                        .fillMaxWidth()
                        .clickable {
                            navigateToDetails(amiiboLatest)
                        },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 20.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.latest_amiibo),
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = amiiboLatest.name,
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
            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = 220.dp),
                model = ImageRequest.Builder(context).data(amiiboLatest.image).build(),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        }
    }
}
