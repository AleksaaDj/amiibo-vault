package com.softwavegames.amiibovault.presenter.compose.screens.collection

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.softwavegames.amiibovault.Constants
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.Utils
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboWishlist
import com.softwavegames.amiibovault.presenter.compose.common.AmiiboGridItem
import com.softwavegames.amiibovault.presenter.compose.common.TextSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCollectionScreen(
    modifier: Modifier = Modifier,
    amiiboListCollection: List<Amiibo>?,
    amiiboListWishlist: List<AmiiboWishlist>?,
    navigateToDetails: (Amiibo) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    var numberOfAmiibo by rememberSaveable { mutableIntStateOf(0) }

    val amiiboCounter by animateIntAsState(
        targetValue = numberOfAmiibo,
        animationSpec = tween(
            durationMillis = 1200,
            easing = LinearEasing
        ), label = ""
    )


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = { Text(text = stringResource(R.string.collections)) },
    )

    Column(
        modifier = Modifier
            .padding(top = 70.dp)
    ) {
            if(amiiboListCollection?.size != null && amiiboListWishlist?.size != null) {
                numberOfAmiibo =
                    if (selectedTab == 0) amiiboListCollection.size else amiiboListWishlist.size

        }
        val current =
            if (selectedTab == 0) amiiboListCollection?.size?.toFloat() else amiiboListWishlist?.size?.toFloat()
        val amiiboWorldwide = 849f
        val percent = (current?.times(100.0f) ?: 0f) / amiiboWorldwide
        Card(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
            ),
            shape = RoundedCornerShape(10.dp),
        ) {
            val text =
                if (selectedTab == 0) stringResource(R.string.collection) else stringResource(
                    R.string.wishlist
                )
            Text(
                modifier = modifier
                    .padding(start = 40.dp, top = 20.dp, bottom = 5.dp),
                text = amiiboCounter.toString() + stringResource(R.string.amiibo_in) + text,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )

            AnimatedLinearProgressIndicator(indicatorProgress = percent / 100)

            Text(
                modifier = modifier
                    .padding(start = 40.dp, bottom = 20.dp),
                text = amiiboWorldwide.toInt().toString() + stringResource(R.string.worldwide),
                fontSize = 13.sp,
                color = Color.Black
            )
        }

        val items = rememberSaveable {
            listOf("my collection", "wishlist")
        }

        var selectedIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        Column {
            TextSwitch(
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                selectedIndex = selectedIndex,
                items = items,
                onSelectionChange = {
                    selectedIndex = it
                    selectedTab = selectedIndex
                }
            )

            when (selectedTab) {
                0 -> amiiboListCollection?.let {
                    MyCollection(amiiboList = it) { amiibo ->
                        navigateToDetails(amiibo)
                    }
                }

                1 -> amiiboListWishlist?.let {
                    Wishlist(amiiboList = it) { amiibo ->
                        navigateToDetails(amiibo)
                    }
                }
            }
        }
    }
}

@Composable
fun MyCollection(amiiboList: List<Amiibo>, navigateToDetails: (amiibo: Amiibo) -> Unit) {
    if (amiiboList.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(start = 24.dp, end = 20.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(count = amiiboList.size) {
                AmiiboGridItem(amiibo = amiiboList[it]) { amiibo ->
                    navigateToDetails(amiibo)
                }
            }
        }
    } else {
        EmptyCollection(Constants.CollectionLists.MY_COLLECTION_LIST.name)
    }
}

@Composable
fun Wishlist(amiiboList: List<AmiiboWishlist>, navigateToDetails: (amiibo: Amiibo) -> Unit) {
    if (amiiboList.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(start = 24.dp, end = 20.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(count = amiiboList.size) {
                val amiiboConverted = Utils().convertAmiiboWishlistToAmiibo(amiiboList[it])
                AmiiboGridItem(amiibo = amiiboConverted) { amiibo ->
                    navigateToDetails(amiibo)
                }
            }
        }
    } else {
        EmptyCollection(Constants.CollectionLists.WISHLIST_LIST.name)
    }
}

@Composable
fun EmptyCollection(listType: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 160.dp)
    ) {
        val isWishlist = listType == Constants.CollectionLists.WISHLIST_LIST.name
        if (isWishlist) {
            Icon(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(45.dp),
                painter = painterResource(id = R.drawable.ic_bookmark_outlined),
                contentDescription = null,
                tint = Color.Red
            )
        }
        val text =
            if (isWishlist) stringResource(R.string.wishlist_empty)
            else stringResource(R.string.collection_empty)
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp, start = 20.dp, end = 20.dp)
        )
    }
}

@Composable
fun AnimatedLinearProgressIndicator(
    indicatorProgress: Float,
) {
    var progress by rememberSaveable { mutableFloatStateOf(0F) }
    val progressAnimDuration = 1_200
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
        label = "",
    )
    LinearProgressIndicator(
        progress = progressAnimation,
        color = Color.Red,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, end = 40.dp, bottom = 5.dp)
    )
    progress = indicatorProgress

}