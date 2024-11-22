package com.softwavegames.amiibovault.presenter.compose.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.Release
import com.softwavegames.amiibovault.presenter.compose.common.DotsDivider
import com.softwavegames.amiibovault.domain.util.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    amiibo: Amiibo,
    onBackClick: () -> Unit,
    navigateToMore: (Amiibo) -> Unit,
    navigateToCompatibility: (Amiibo) -> Unit,
    saveToWishlist: (Amiibo) -> Unit,
    removeFromWishlist: (Amiibo) -> Unit,
    isAmiiboSavedWishlist: State<Boolean?>,
    saveToMyCollection: (Amiibo) -> Unit,
    removeFromMyCollection: (Amiibo) -> Unit,
    isAmiiboSavedMyCollection: State<Boolean?>,
    isPortrait: Boolean
) {

    val context = LocalContext.current

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = { Text(text = stringResource(R.string.amiibo)) },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.Red,
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if (isAmiiboSavedWishlist.value == true) {
                    removeFromWishlist(amiibo)
                } else {
                    saveToWishlist(amiibo)
                }
            }) {
                Icon(
                    painter = if (isAmiiboSavedWishlist.value == true) painterResource(id = R.drawable.ic_bookmark) else painterResource(
                        id = R.drawable.ic_bookmark_outlined
                    ),
                    contentDescription = null,
                    tint = Color.Red,
                )
            }
        }
    )

    if (!isPortrait) {
        Row(
            modifier = Modifier
                .padding(top = 90.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            AsyncImage(
                modifier = Modifier
                    .size(260.dp)
                    .padding(start = 75.dp, bottom = 40.dp)
                    .weight(1f),
                model = ImageRequest.Builder(context).data(amiibo.image).build(),
                contentDescription = null,
                error = painterResource(id = R.drawable.ic_image_placeholder)
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(start = 112.dp, end = 122.dp)
                    .weight(2f)
            ) {

                AmiiboDetailsInfo(amiibo = amiibo, isLandscape = true)

                HorizontalDivider(
                    modifier = Modifier
                        .height(0.5.dp)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActionButtonPart(
                    navigateToMore = { navigateToMore(amiibo) },
                    navigateToCompatibility = { navigateToCompatibility(amiibo) },
                    isAmiiboSavedMyCollection = isAmiiboSavedMyCollection,
                    saveToMyCollection = { saveToMyCollection(amiibo) },
                    removeFromMyCollection = { removeFromMyCollection(amiibo) },
                    isLandscape = true
                )
                Spacer(modifier = Modifier.height(20.dp))

                ReleaseInfo(amiibo.release, isLandscape = true)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .padding(top = 90.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(220.dp)
                    .fillMaxWidth(),
                model = ImageRequest.Builder(context).data(amiibo.image).build(),
                contentDescription = null,
                error = painterResource(id = R.drawable.ic_image_placeholder)
            )
            Column(
                modifier = Modifier
                    .padding(start = 52.dp, end = 52.dp)
            ) {

                Spacer(modifier = Modifier.height(15.dp))

                AmiiboDetailsInfo(amiibo = amiibo, isLandscape = false)

                HorizontalDivider(
                    modifier = Modifier
                        .height(0.5.dp)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActionButtonPart(
                    navigateToMore = { navigateToMore(amiibo) },
                    navigateToCompatibility = { navigateToCompatibility(amiibo) },
                    isAmiiboSavedMyCollection = isAmiiboSavedMyCollection,
                    saveToMyCollection = { saveToMyCollection(amiibo) },
                    removeFromMyCollection = { removeFromMyCollection(amiibo) },
                    isLandscape = false
                )

                Spacer(modifier = Modifier.height(10.dp))

                DotsDivider()

                ReleaseInfo(amiibo.release, isLandscape = false)

            }
        }
    }
}

@Composable
fun AmiiboDetailsInfo(amiibo: Amiibo, isLandscape: Boolean) {
    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = amiibo.name,
        textAlign = TextAlign.Center,
        fontSize = 28.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(if (isLandscape) 5.dp else 18.dp))

    InfoDetailItem(
        label = stringResource(id = R.string.character),
        infoToDisplay = amiibo.character
    )

    HorizontalDivider(
        modifier = Modifier
            .height(0.5.dp)
            .background(Color.LightGray)
    )

    Spacer(modifier = Modifier.height(if (isLandscape) 5.dp else 7.dp))

    InfoDetailItem(label = stringResource(R.string.game_series), infoToDisplay = amiibo.gameSeries)

    HorizontalDivider(
        modifier = Modifier
            .height(0.5.dp)
            .background(Color.LightGray)
    )

    Spacer(modifier = Modifier.height(if (isLandscape) 5.dp else 7.dp))

    InfoDetailItem(label = stringResource(R.string.set), infoToDisplay = amiibo.amiiboSeries)

    HorizontalDivider(
        modifier = Modifier
            .height(0.5.dp)
            .background(Color.LightGray)
    )

    Spacer(modifier = Modifier.height(if (isLandscape) 5.dp else 7.dp))

    InfoDetailItem(label = stringResource(R.string.type), infoToDisplay = amiibo.type)

    HorizontalDivider(
        modifier = Modifier
            .height(0.5.dp)
            .background(Color.LightGray)
    )

    Spacer(modifier = Modifier.height(if (isLandscape) 5.dp else 7.dp))

    InfoDetailItem(label = stringResource(R.string.serial), infoToDisplay = amiibo.head + amiibo.tail)

}

@Composable
fun InfoDetailItem(label: String, infoToDisplay: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            modifier = Modifier
                .padding(top = 2.dp),
            text = label,
            fontSize = 13.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Normal,
        )
        Text(
            modifier = Modifier
                .padding(start = 5.dp),
            text = infoToDisplay,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
        )

    }
}

@Composable
fun ActionButtonPart(
    navigateToMore: () -> Unit,
    navigateToCompatibility: () -> Unit,
    isAmiiboSavedMyCollection: State<Boolean?>,
    saveToMyCollection: () -> Unit,
    removeFromMyCollection: () -> Unit,
    isLandscape: Boolean
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isLandscape) 0.dp else 8.dp, start = 30.dp, end = 30.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        onClick = { navigateToMore() },
    )
    {
        Text(text = stringResource(R.string.more_from_series))
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 50.dp,
                end = 50.dp,
                top = if (isLandscape) 0.dp else 6.dp,
                bottom = 10.dp
            )
            .clickable {
                navigateToCompatibility()
            },
        text = stringResource(R.string.compatibility_and_usage),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp, end = 45.dp)
            .clickable {
                if (isAmiiboSavedMyCollection.value == true) {
                    removeFromMyCollection()
                } else {
                    saveToMyCollection()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(20.dp),
                painter = if (isAmiiboSavedMyCollection.value == false) painterResource(id = R.drawable.ic_add) else painterResource(
                    id = R.drawable.ic_remove
                ),
                tint = Color.Red,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 2.dp, end = 5.dp),
                text = if (isAmiiboSavedMyCollection.value == false) stringResource(R.string.add_to_my_collection) else stringResource(
                    R.string.remove_from_collection
                ),
                textAlign = TextAlign.End,
                color = Color.Red,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ReleaseInfo(release: Release?, isLandscape: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (isLandscape) 0.dp else 35.dp,
                start = 25.dp,
                end = 25.dp,
                bottom = 50.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (!release?.au.equals("null") && release?.au != null) {
            ReleaseCard(release.au, R.drawable.au_flag)
        }
        if (!release?.eu.equals("null") && release?.eu != null) {
            ReleaseCard(release.eu, R.drawable.eu_flag)
        }
        if (!release?.jp.equals("null") && release?.jp != null) {
            ReleaseCard(release.jp, R.drawable.jp_flag)
        }
        if (!release?.na.equals("null") && release?.na != null) {
            ReleaseCard(release.na, R.drawable.us_flag)
        }

    }
}

@Composable
fun ReleaseCard(date: String, flag: Int) {
    val dayAndMonth = Utils().getDayAndMonthFromString(date)
    val year = Utils().getYearFromString(date)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-7).dp)
    ) {
        Card(
            modifier = Modifier,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            shape = CircleShape
        ) {
            Image(
                painter = painterResource(flag),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
        }
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = dayAndMonth, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.wrapContentSize(unbounded = true),
            text = year, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
        )
    }
}



