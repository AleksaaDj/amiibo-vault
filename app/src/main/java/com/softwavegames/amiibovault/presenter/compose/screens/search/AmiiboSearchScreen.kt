package com.softwavegames.amiibovault.presenter.compose.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.softwavegames.amiibovault.presenter.compose.common.AmiiboGridItem
import com.softwavegames.amiibovault.presenter.compose.common.AmiiboListItem
import com.softwavegames.amiibovault.presenter.compose.common.LatestAmiiboCard
import com.softwavegames.amiibovault.presenter.compose.common.ScrollToTopButton
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmiiboListScreen(
    amiiboList: List<Amiibo>?,
    amiiboLatest: Amiibo?,
    navigateToDetails: (Amiibo) -> Unit,
    onSearchQueryChange: (String, Boolean) -> Unit,
    onChangeListClick: () -> Unit,
    onScrollToTopClick: (Boolean) -> Unit,
    isPortrait: Boolean,
    listState: LazyListState,
    gridState: LazyGridState,
    showScrollToTopList: Boolean,
    showScrollToTopGrid: Boolean,
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isList by rememberSaveable { mutableStateOf(true) }
    var showLatestAmiibo by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        SearchBar(
            modifier = Modifier
                .weight(3f)
                .padding(start = if (isPortrait) 10.dp else 90.dp),
            query = searchText,
            onQueryChange = {
                searchText = it
                onSearchQueryChange(it, isList)
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
            trailingIcon = {},
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
        IconButton(modifier = Modifier
            .padding(end = 10.dp, top = 5.dp),
            onClick = {
                onChangeListClick()
                isList = !isList
            }
        ) {
            Icon(
                painter = if (isList) painterResource(id = R.drawable.ic_grid) else painterResource(
                    id = R.drawable.ic_list
                ), contentDescription = null,
                tint = Color.Red
            )
        }
    }


    Column(
        modifier = Modifier.padding(top = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showProgress = rememberSaveable {
            mutableStateOf(true)
        }
        val showErrorScreen = rememberSaveable {
            mutableStateOf(false)
        }

        if (isPortrait) {
            AnimatedVisibility(visible = showLatestAmiibo) {
                LatestAmiiboCard(
                    amiiboLatest = amiiboLatest,
                    navigateToDetails = navigateToDetails
                )
            }
            if(amiiboLatest != null) {
                LaunchedEffect(true) {
                    delay(1000)
                    showLatestAmiibo = true
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
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
                        delay(4500)
                        showProgress.value = false
                        showErrorScreen.value = true
                    }
                }
                androidx.compose.animation.AnimatedVisibility(visible = showErrorScreen.value) {
                    EmptyScreen(stringResource(id = R.string.error_getting_amiibo_list))
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(
                            start = if (isPortrait) 10.dp else 110.dp,
                            end = if (isPortrait) 10.dp else 40.dp
                        ),
                    contentPadding = PaddingValues(vertical = 10.dp),
                ) {
                    if (amiiboList != null) {
                        if (amiiboList.isNotEmpty()) {
                            items(count = amiiboList.size) {
                                AmiiboListItem(amiibo = amiiboList[it], onClick = { amiibo ->
                                    navigateToDetails(amiibo)
                                })
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
                        onScrollToTopClick(isList)
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
                        delay(4500)
                        showProgress.value = false
                        showErrorScreen.value = true
                    }
                }
                androidx.compose.animation.AnimatedVisibility(visible = showErrorScreen.value) {
                    EmptyScreen(stringResource(id = R.string.error_getting_amiibo_list))
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
                        items(count = amiiboList.size) {
                            AmiiboGridItem(amiibo = amiiboList[it]) { amiibo ->
                                navigateToDetails(amiibo)
                            }
                        }
                    }
                }
                this@Column.AnimatedVisibility(
                    visible = showScrollToTopGrid,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    ScrollToTopButton(onClick = {
                        onScrollToTopClick(isList)
                    })
                }
            }
        }
        if (amiiboList != null) {
            androidx.compose.animation.AnimatedVisibility(visible = amiiboList.isEmpty()) {
                EmptyScreen(stringResource(id = R.string.no_amiibo_found))
            }
        }
    }
}

@Composable
fun EmptyScreen(textToDisplay: String) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            text = textToDisplay,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}


