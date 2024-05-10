package com.softwavegames.amiibovault.presenter.compose.common

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.util.Utils
import com.softwavegames.amiibovault.util.getAverageColor

@Composable
fun LatestAmiiboCard(
    amiiboLatest: Amiibo?,
    navigateToDetails: (Amiibo) -> Unit
) {
    val bitmap = remember {
        MutableLiveData<Bitmap>()
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        Utils().urlToBitmap(
            scope = scope,
            imageURL = amiiboLatest?.image.toString(),
            context = context,
            onSuccess = {
                bitmap.postValue(it)
            },
            onError = {
                Log.e("latest_amiibo", it.message.toString())
            }
        )
    }
    bitmap.observeAsState().value?.let { generatedBitmap ->
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
                    AmiiboLatestDetails(
                        amiiboLatest = amiiboLatest,
                        navigateToDetails,
                        generatedBitmap
                    )
                }
                Image(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterEnd)
                        .fillMaxWidth(),
                    bitmap = generatedBitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Inside
                )
            }
        }
    }
}

@Composable
private fun AmiiboLatestDetails(
    amiiboLatest: Amiibo,
    navigateToDetails: (Amiibo) -> Unit,
    bitmap: Bitmap
) {
    // Crop image to focus on central part of the amiibo for average color
    val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 100, bitmap.width - 100, bitmap.height - 100)

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .offset(y = 40.dp)
            .fillMaxWidth()
            .clickable {
                navigateToDetails(amiiboLatest)
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 20.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = getAverageColor(
                imageBitmap = croppedBitmap.asImageBitmap()
            ),
        ),
        shape = RoundedCornerShape(10.dp)
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


