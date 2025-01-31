package com.softwavegames.amiibovault.presenter.compose.screens.collection

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.Utils
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import com.softwavegames.amiibovault.presenter.compose.common.items.AmiiboGridItem
import com.softwavegames.amiibovault.presenter.compose.common.ChipGroup
import com.softwavegames.amiibovault.presenter.compose.common.cards.GlowingCard
import com.softwavegames.amiibovault.presenter.compose.common.dialogs.RemoveAdsDialog
import com.softwavegames.amiibovault.presenter.compose.common.TextSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCollectionScreen(
    amiiboListCollection: List<AmiiboCollection>?,
    amiiboListWishlist: List<AmiiboWishlist>?,
    navigateToDetails: (Amiibo) -> Unit,
    isPortrait: Boolean,
    onSupportClick: () -> Unit,
    onSelectionChange: (Int) -> Unit,
    isDarkMode: Boolean,
    onThemeModeClicked: () -> Unit,
    onPurchaseClicked: () -> Unit,
    buyEnabled: Boolean,
    openRemoveAdsDialog: MutableState<Boolean>,
    onRemoveAdsClicked: () -> Unit,
    onDialogAdsDismissed: () -> Unit,
    onFilterTypeCollectionSelected: (String) -> Unit,
    onFilterTypeCollectionRemoved: () -> Unit,
    onFilterSetCollectionSelected: (String) -> Unit,
    onFilterSetCollectionRemoved: () -> Unit,
    onSortTypeCollectionSelected: (String) -> Unit,
    onSortTypeCollectionRemoved: () -> Unit,
    onFilterTypeWishlistSelected: (String) -> Unit,
    onFilterTypeWishlistRemoved: () -> Unit,
    onFilterSetWishlistSelected: (String) -> Unit,
    onFilterSetWishlistRemoved: () -> Unit,
    onSortTypeWishlistSelected: (String) -> Unit,
    onSortTypeWishlistRemoved: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    TopAppBar(
        modifier = Modifier
            .padding(start = if (isPortrait) 0.dp else 80.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = { Text(text = stringResource(R.string.collections)) },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (buyEnabled) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                onPurchaseClicked()
                            }
                            .padding(end = 15.dp, top = 3.dp)
                            .size(24.dp),
                        painter = painterResource(
                            id = R.drawable.ic_remove_ads
                        ), contentDescription = null,
                        tint = Color.Red
                    )
                }
                Icon(
                    modifier = Modifier
                        .padding(end = 15.dp, top = 3.dp)
                        .size(24.dp)
                        .clickable {
                            onThemeModeClicked()
                        },
                    painter = if (isDarkMode) painterResource(id = R.drawable.ic_light_mode) else painterResource(
                        id = R.drawable.ic_dark_mode
                    ),
                    tint = Color.Red,
                    contentDescription = stringResource(
                        R.string.donate
                    )
                )
                Row(
                    modifier = Modifier
                        .clickable {
                            onSupportClick()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 13.dp, top = 3.dp)
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.ic_help),
                        tint = Color.Red,
                        contentDescription = stringResource(
                            R.string.support
                        ),
                    )
                }
            }
        }
    )
    when {
        openRemoveAdsDialog.value -> {
            RemoveAdsDialog(
                onDismissRequest = { onDialogAdsDismissed() },
                onConfirmation = {
                    onRemoveAdsClicked()
                },
            )
        }
    }
    Column(
        modifier = Modifier
            .padding(top = if (isPortrait) 110.dp else 90.dp)
    ) {
        if (isPortrait) {
            DbStatisticInfo(selectedTab, amiiboListCollection, amiiboListWishlist)
        }

        val context = LocalContext.current
        val items = rememberSaveable {
            listOf(context.getString(R.string.my_collection), context.getString(R.string.wishlist))
        }

        var selectedIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        Column {
            TextSwitch(
                modifier = Modifier
                    .padding(
                        top = if (isPortrait) 5.dp else 0.dp,
                        start = if (isPortrait) 10.dp else 110.dp,
                        end = if (isPortrait) 10.dp else 50.dp
                    ),
                selectedIndex = selectedIndex,
                items = items,
                onSelectionChange = {
                    onSelectionChange(selectedIndex)
                    selectedIndex = it
                    selectedTab = selectedIndex
                }
            )

            when (selectedTab) {
                0 -> amiiboListCollection?.let {
                    MyCollection(
                        isPortrait = isPortrait, amiiboList = it,
                        navigateToDetails = { amiibo ->
                            navigateToDetails(amiibo)
                        },
                        onFilterTypeCollectionSelected = { type ->
                            onFilterTypeCollectionSelected(type)
                        },
                        onFilterTypeCollectionRemoved = { onFilterTypeCollectionRemoved() },
                        onFilterSetCollectionSelected = { series ->
                            onFilterSetCollectionSelected(series)
                        },
                        onFilterSetCollectionRemoved = { onFilterSetCollectionRemoved() },
                        onSortTypeCollectionSelected = { sortType ->
                            onSortTypeCollectionSelected(sortType)
                        },
                        onSortTypeCollectionRemoved = { onSortTypeCollectionRemoved() }
                    )
                }

                1 -> amiiboListWishlist?.let {
                    Wishlist(isPortrait = isPortrait,
                        amiiboList = it,
                        navigateToDetails = { amiibo ->
                            navigateToDetails(amiibo)
                        },
                        onFilterTypeWishlistSelected = { type ->
                            onFilterTypeWishlistSelected(type)
                        },
                        onFilterTypeWishlistRemoved = { onFilterTypeWishlistRemoved() },
                        onFilterSetWishlistSelected = { series ->
                            onFilterSetWishlistSelected(series)
                        },
                        onFilterSetWishlistRemoved = { onFilterSetWishlistRemoved() },
                        onSortTypeWishlistSelected = { sortType ->
                            onSortTypeWishlistSelected(sortType)
                        },
                        onSortTypeWishlistRemoved = { onSortTypeWishlistRemoved() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyCollection(
    isPortrait: Boolean,
    amiiboList: List<AmiiboCollection>,
    navigateToDetails: (amiibo: Amiibo) -> Unit,
    onFilterTypeCollectionSelected: (String) -> Unit,
    onFilterTypeCollectionRemoved: () -> Unit,
    onFilterSetCollectionSelected: (String) -> Unit,
    onFilterSetCollectionRemoved: () -> Unit,
    onSortTypeCollectionSelected: (String) -> Unit,
    onSortTypeCollectionRemoved: () -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ChipGroup(
            onFilterTypeSelected = {
                onFilterTypeCollectionSelected(it)
            },
            onFilterTypeRemoved = {
                onFilterTypeCollectionRemoved()
            },
            onFilterSetSelected = {
                onFilterSetCollectionSelected(it)
            },
            onFilterSetRemoved = {
                onFilterSetCollectionRemoved()
            },
            onSortTypeRemoved = {
                onSortTypeCollectionRemoved()
            },
            onSortTypeSelected = {
                onSortTypeCollectionSelected(it)
            },
            isPortrait = isPortrait
        )
    }
    HorizontalDivider(
        modifier = Modifier
            .height(0.5.dp)
            .padding(start = if (isPortrait) 5.dp else 90.dp, bottom = 5.dp),
        color = Color.LightGray
    )
    if (amiiboList.isNotEmpty()) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isPortrait) 3 else 5),
            modifier = Modifier
                .padding(
                    start = if (isPortrait) 24.dp else 110.dp,
                    end = if (isPortrait) 20.dp else 45.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            items(count = amiiboList.size) {
                val amiiboConverted = Utils().convertAmiiboCollectionToAmiibo(amiiboList[it])
                AmiiboGridItem(
                    amiibo = amiiboConverted,
                    showInCollection = true,
                    onAmiiboClick = { amiibo ->
                        navigateToDetails(amiibo)
                    })
            }
        }
    } else {
        EmptyCollection(Constants.CollectionLists.MY_COLLECTION_LIST.name, isPortrait)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Wishlist(
    isPortrait: Boolean,
    amiiboList: List<AmiiboWishlist>,
    navigateToDetails: (amiibo: Amiibo) -> Unit,
    onFilterTypeWishlistSelected: (String) -> Unit,
    onFilterTypeWishlistRemoved: () -> Unit,
    onFilterSetWishlistSelected: (String) -> Unit,
    onFilterSetWishlistRemoved: () -> Unit,
    onSortTypeWishlistSelected: (String) -> Unit,
    onSortTypeWishlistRemoved: () -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ChipGroup(
            onFilterTypeSelected = {
                onFilterTypeWishlistSelected(it)
            },
            onFilterTypeRemoved = {
                onFilterTypeWishlistRemoved()
            },
            onFilterSetSelected = {
                onFilterSetWishlistSelected(it)
            },
            onFilterSetRemoved = {
                onFilterSetWishlistRemoved()
            },
            onSortTypeRemoved = {
                onSortTypeWishlistRemoved()
            },
            onSortTypeSelected = {
                onSortTypeWishlistSelected(it)
            },
            isPortrait = isPortrait
        )
    }
    HorizontalDivider(
        modifier = Modifier
            .height(0.5.dp)
            .padding(start = if (isPortrait) 5.dp else 90.dp, bottom = 5.dp),
        color = Color.LightGray
    )
    if (amiiboList.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isPortrait) 3 else 5),
            modifier = Modifier
                .padding(
                    start = if (isPortrait) 24.dp else 110.dp,
                    end = if (isPortrait) 20.dp else 45.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            items(count = amiiboList.size) {
                val amiiboConverted = Utils().convertAmiiboWishlistToAmiibo(amiiboList[it])
                AmiiboGridItem(
                    amiibo = amiiboConverted,
                    showInCollection = true,
                    onAmiiboClick = { amiibo ->
                        navigateToDetails(amiibo)
                    })
            }
        }
    } else {
        EmptyCollection(Constants.CollectionLists.WISHLIST_LIST.name, isPortrait)
    }
}

@Composable
fun DbStatisticInfo(
    selectedTab: Int,
    amiiboListCollection: List<AmiiboCollection>?,
    amiiboListWishlist: List<AmiiboWishlist>?
) {
    var numberOfAmiibo by rememberSaveable { mutableIntStateOf(0) }

    val amiiboCounter by animateIntAsState(
        targetValue = numberOfAmiibo,
        animationSpec = tween(
            durationMillis = 1200,
            easing = LinearEasing
        ), label = ""
    )

    if (amiiboListCollection?.size != null && amiiboListWishlist?.size != null) {
        numberOfAmiibo =
            if (selectedTab == 0) amiiboListCollection.size else amiiboListWishlist.size

    }
    val current =
        if (selectedTab == 0) amiiboListCollection?.size?.toFloat() else amiiboListWishlist?.size?.toFloat()
    val amiiboWorldwide = Constants.AMIIBO_WORLDWIDE_SIZE
    val percent = (current?.times(100.0f) ?: 0f) / amiiboWorldwide

    GlowingCard(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp),
        containerColor = MaterialTheme.colorScheme.tertiary,
        glowingColor = MaterialTheme.colorScheme.tertiary,
        cornersRadius = 10.dp
    ) {
        Column {
            val text =
                if (selectedTab == 0) stringResource(R.string.collection) else stringResource(
                    R.string.wishlist
                )
            Text(
                modifier = Modifier
                    .padding(start = 40.dp, top = 20.dp, bottom = 5.dp),
                text = amiiboCounter.toString() + stringResource(R.string.amiibo_in) + text,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )

            AnimatedLinearProgressIndicator(indicatorProgress = percent / 100)

            Text(
                modifier = Modifier
                    .padding(start = 40.dp, bottom = 20.dp),
                text = amiiboWorldwide.toInt().toString() + stringResource(R.string.worldwide),
                fontSize = 13.sp,
                color = Color.Black
            )
        }
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
        progress = { progressAnimation },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, end = 40.dp, bottom = 5.dp),
        color = Color.Red,
    )
    progress = indicatorProgress

}

@Composable
fun EmptyCollection(listType: String, isPortrait: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isPortrait) 0.dp else 90.dp,
                top = if (isPortrait) 160.dp else 60.dp
            )
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
            modifier = Modifier.padding(
                top = 10.dp,
                start = if (isPortrait) 20.dp else 70.dp,
                end = if (isPortrait) 20.dp else 70.dp
            )
        )
    }
}