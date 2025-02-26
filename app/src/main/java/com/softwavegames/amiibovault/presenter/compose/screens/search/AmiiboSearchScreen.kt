package com.softwavegames.amiibovault.presenter.compose.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.model.AmiiboCollection
import com.softwavegames.amiibovault.model.AmiiboWishlist
import com.softwavegames.amiibovault.presenter.compose.common.ChipGroup
import com.softwavegames.amiibovault.presenter.compose.common.buttons.ScrollToTopButton
import com.softwavegames.amiibovault.presenter.compose.common.cards.FeaturedAmiiboCard
import com.softwavegames.amiibovault.presenter.compose.common.dialogs.RateDialog
import com.softwavegames.amiibovault.presenter.compose.common.items.AmiiboGridItem
import com.softwavegames.amiibovault.presenter.compose.common.items.AmiiboListItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AmiiboListScreen(
    amiiboCollection: List<AmiiboCollection>?,
    amiiboWishList: List<AmiiboWishlist>?,
    amiiboList: List<Amiibo>?,
    amiiboLatest: Amiibo?,
    navigateToDetails: (Amiibo) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onChangeListClick: () -> Unit,
    onScrollToTopClick: () -> Unit,
    isPortrait: Boolean,
    listState: LazyListState,
    gridState: LazyGridState,
    showScrollToTopList: Boolean,
    showScrollToTopGrid: Boolean,
    onFilterTypeSelected: (String) -> Unit,
    onFilterTypeRemoved: () -> Unit,
    onFilterSetSelected: (String) -> Unit,
    onFilterSetRemoved: () -> Unit,
    onSortTypeSelected: (String) -> Unit,
    onSortTypeRemoved: () -> Unit,
    onLayoutChange: (Boolean) -> Unit,
    saveToWishlist: (Amiibo) -> Unit,
    removeFromWishlist: (Amiibo) -> Unit,
    onSoundVolumeChanged: (Boolean) -> Unit,
    openRateDialog: MutableState<Boolean>,
    onDialogRateDismissed: () -> Unit,
    onRateClicked: () -> Unit,
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isList by rememberSaveable { mutableStateOf(true) }
    var isSoundOn by rememberSaveable { mutableStateOf(true) }
    var showLatestAmiibo by rememberSaveable { mutableStateOf(false) }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showProgress = rememberSaveable {
            mutableStateOf(true)
        }
        val showErrorScreen = rememberSaveable {
            mutableStateOf(false)
        }

        Row(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            SearchBar(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = if (isPortrait) 0.dp else 90.dp),
                query = searchText,
                onQueryChange = {
                    searchText = it
                    onSearchQueryChange(it)
                },
                onSearch = { isSearchActive = false },
                placeholder = {
                    Text(text = stringResource(R.string.search_amiibo))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        tint = Color.Red,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(modifier = Modifier
                        .padding(end = 5.dp),
                        onClick = {
                            isSoundOn = !isSoundOn
                            onSoundVolumeChanged(isSoundOn)
                        }
                    ) {
                        Icon(
                            painter = if (isSoundOn) painterResource(id = R.drawable.ic_volume_on) else painterResource(
                                id = R.drawable.ic_volume_off
                            ), contentDescription = null,
                            tint = Color.Red
                        )
                    }
                },
                content = {},
                active = isSearchActive,
                onActiveChange = {
                    isSearchActive = it
                },
                tonalElevation = 0.dp,
                colors = SearchBarDefaults.colors(
                    dividerColor = Color.Red,
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
        when {
            openRateDialog.value -> {
                RateDialog(
                    onDismissRequest = {
                        onDialogRateDismissed()
                    },
                    onConfirmation = {
                        onRateClicked()
                    }
                )
            }
        }
        if (isPortrait) {
            AnimatedVisibility(visible = showLatestAmiibo) {
                FeaturedAmiiboCard(
                    amiiboLatest = amiiboLatest,
                    navigateToDetails = navigateToDetails
                )
            }
            if (amiiboLatest != null) {
                LaunchedEffect(true) {
                    delay(1000)
                    showLatestAmiibo = true
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ChipGroup(
                onFilterTypeSelected = {
                    onFilterTypeSelected(it)
                    searchText = ""
                },
                onFilterTypeRemoved = {
                    searchText = ""
                    onFilterTypeRemoved()
                },
                onFilterSetSelected = {
                    searchText = ""
                    onFilterSetSelected(it)
                },
                onFilterSetRemoved = {
                    searchText = ""
                    onFilterSetRemoved()
                },
                onSortTypeRemoved = {
                    searchText = ""
                    onSortTypeRemoved()
                },
                onSortTypeSelected = {
                    searchText = ""
                    onSortTypeSelected(it)
                },
                isPortrait = isPortrait
            )

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 10.dp, end = 13.dp, start = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.Absolute.Right
            ) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            onChangeListClick()
                            isList = !isList
                            onLayoutChange(isList)
                        }
                        .size(24.dp),
                    painter = if (isList) painterResource(id = R.drawable.ic_grid) else painterResource(
                        id = R.drawable.ic_list
                    ), contentDescription = null,
                    tint = Color.Red
                )

            }
        }

        HorizontalDivider(
            modifier = Modifier
                .height(0.5.dp)
                .padding(start = if (isPortrait) 0.dp else 80.dp),
            color = Color.LightGray
        )
        if (isList) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(visible = showProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 150.dp),
                        color = Color.Red,
                    )
                    LaunchedEffect(this) {
                        delay(25000)
                        showProgress.value = false
                        showErrorScreen.value = true
                    }
                }
                androidx.compose.animation.AnimatedVisibility(visible = showErrorScreen.value) {
                    EmptyScreen(stringResource(id = R.string.error_getting_amiibo_list), isPortrait)
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(
                            start = if (isPortrait) 10.dp else 110.dp,
                            end = if (isPortrait) 10.dp else 40.dp
                        ),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    if (amiiboList != null) {
                        if (amiiboList.isNotEmpty()) {
                            items(count = amiiboList.size) { amiiboItem ->
                                val foundInCollection =
                                    amiiboCollection?.firstOrNull { it.tail == amiiboList[amiiboItem].tail } != null
                                val foundInWishlist =
                                    amiiboWishList?.firstOrNull { it.tail == amiiboList[amiiboItem].tail } != null

                                AmiiboListItem(
                                    amiibo = amiiboList[amiiboItem],
                                    onClick = { amiibo ->
                                        navigateToDetails(amiibo)
                                    },
                                    showInCollection = foundInCollection,
                                    showInWishlist = foundInWishlist,
                                    saveToWishlist = saveToWishlist,
                                    removeFromWishlist = removeFromWishlist
                                )
                                showProgress.value = false
                                showErrorScreen.value = false
                            }
                        }
                    }
                }
                this@Column.AnimatedVisibility(
                    visible = showScrollToTopList,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    ScrollToTopButton(onClick = {
                        onScrollToTopClick()
                    })
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(visible = showProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 150.dp),
                        color = Color.Red,
                    )
                    LaunchedEffect(this) {
                        delay(25000)
                        showProgress.value = false
                        showErrorScreen.value = true
                    }
                }
                androidx.compose.animation.AnimatedVisibility(visible = showErrorScreen.value) {
                    EmptyScreen(stringResource(id = R.string.error_getting_amiibo_list), isPortrait)
                }
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(if (isPortrait) 3 else 5),
                    modifier = Modifier
                        .padding(start = if (isPortrait) 24.dp else 100.dp, end = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(vertical = 20.dp),
                ) {
                    if (amiiboList != null) {
                        items(count = amiiboList.size) { amiiboItem ->
                            val foundInCollection =
                                amiiboCollection?.firstOrNull { it.tail == amiiboList[amiiboItem].tail } != null
                            AmiiboGridItem(
                                amiibo = amiiboList[amiiboItem],
                                showInCollection = foundInCollection,
                                onAmiiboClick = {
                                    navigateToDetails(it)
                                })
                        }
                    }
                }
                this@Column.AnimatedVisibility(
                    visible = showScrollToTopGrid,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    ScrollToTopButton(onClick = {
                        onScrollToTopClick()
                    })
                }
            }
        }
        if (amiiboList != null) {
            androidx.compose.animation.AnimatedVisibility(visible = amiiboList.isEmpty() && !showErrorScreen.value) {
                EmptyScreen(stringResource(id = R.string.no_amiibo_found), isPortrait)
            }
        }
    }
}

@Composable
fun EmptyScreen(textToDisplay: String, isPortrait: Boolean) {
    Column(
        modifier = Modifier
            .padding(if (isPortrait) 70.dp else 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(180.dp),
            painter = painterResource(id = R.drawable.no_results), contentDescription = null
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = textToDisplay,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}


