package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo

@Composable
fun AmiiboListItem(
    modifier: Modifier = Modifier,
    amiibo: Amiibo,
    onClick: (Amiibo) -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
            .clickable { onClick(amiibo) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(
            topStart = 5.dp,
            bottomStart = 5.dp,
            bottomEnd = 5.dp,
            topEnd = 5.dp
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable { onClick(amiibo) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                shape = RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp, bottomEnd = 300.dp),
            ) {
                ListItemDetails(modifier = modifier, amiibo = amiibo)
            }
        }
    }
}

@Composable
private fun ListItemDetails(modifier: Modifier, amiibo: Amiibo) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(90.dp)
                    .padding(5.dp),
                model = ImageRequest.Builder(context).data(amiibo.image).build(),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                error = painterResource(R.drawable.ic_image_placeholder)
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 6.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = modifier.padding(top = 5.dp),
                text = amiibo.name,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = modifier.padding(bottom = 8.dp),
                text = amiibo.gameSeries,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary,
            )


            amiibo.release?.jp?.let {
                Text(
                    text = it,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}
