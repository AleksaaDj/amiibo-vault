package com.softwavegames.amiibovault.presenter.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.domain.util.AverageColor.getAverageColor

@Composable
fun AmiiboListItem(
    modifier: Modifier = Modifier,
    amiibo: Amiibo,
    onClick: (Amiibo) -> Unit,
    showInCollection: Boolean,
    showInWishlist: Boolean,
    saveToWishlist: (Amiibo) -> Unit,
    removeFromWishlist: (Amiibo) -> Unit,
) {
    val density = LocalDensity.current
    var height by remember { mutableIntStateOf(0) }
    val heightDp = remember(height) { with(density) { height.toDp() } }
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(amiibo.image)
            .size(Size.ORIGINAL)
            .build()
    ).state

    val defaultColor = MaterialTheme.colorScheme.background
    var dominantColor by remember {
        mutableStateOf(defaultColor)
    }
    if (imageState is AsyncImagePainter.State.Success) {
        dominantColor = getAverageColor(
            imageBitmap = imageState.result.drawable.toBitmap().asImageBitmap()
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
            .clickable { onClick(amiibo) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(
            topStart = 7.dp,
            bottomStart = 7.dp,
            bottomEnd = 7.dp,
            topEnd = 7.dp
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .onSizeChanged {
                        height = it.height
                    }
                    .clickable { onClick(amiibo) }
                    .background(dominantColor),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                shape = RoundedCornerShape(
                    bottomEnd = 300.dp
                ),
            ) {
                ListItemDetails(
                    modifier = modifier,
                    amiibo = amiibo,
                    imageState.painter,
                    dominantColor,
                    heightDp,
                    showInCollection,
                    showInWishlist,
                    saveToWishlist,
                    removeFromWishlist
                )
            }
        }
    }
}

@Composable
private fun ListItemDetails(
    modifier: Modifier,
    amiibo: Amiibo,
    imagePainter: Painter?,
    dominantColor: Color,
    height: Dp,
    showInCollection: Boolean,
    showInWishlist: Boolean,
    saveToWishlist: (Amiibo) -> Unit,
    removeFromWishlist: (Amiibo) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(height)
                    .background(dominantColor),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier
                        .size(85.dp)
                        .padding(5.dp),
                    painter = imagePainter ?: painterResource(id = R.drawable.ic_image_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                        if (!showInCollection) setToSaturation(0.1f)
                    })

                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    modifier = modifier.padding(top = 5.dp, end = 30.dp),
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
                        text = if (it == "null") "" else it,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            onClick = {
                if (showInWishlist) {
                    removeFromWishlist(amiibo)
                } else {
                    saveToWishlist(amiibo)
                }
            }) {
            Icon(
                painter = if (showInWishlist) painterResource(id = R.drawable.ic_bookmark) else painterResource(
                    id = R.drawable.ic_bookmark_outlined
                ),
                contentDescription = null,
                tint = dominantColor,
            )
        }
    }
}
