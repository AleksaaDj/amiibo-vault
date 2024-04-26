package com.softwavegames.amiibovault.presenter.compose.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.presenter.compose.common.AmiiboGridItem
import com.softwavegames.amiibovault.presenter.compose.common.AmiiboListItem
import com.softwavegames.amiibovault.presenter.compose.common.LatestAmiiboCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmiiboListScreen(
    amiiboList: List<Amiibo>?,
    amiiboLatest: Amiibo?,
    navigateToDetails: (Amiibo) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onChangeListClick: () -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isList by rememberSaveable { mutableStateOf(true) }
    val sortedList: List<Amiibo>? = amiiboList?.sortedBy { it.name }

    Row(
        modifier = Modifier
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        SearchBar(
            modifier = Modifier
                .weight(3f),
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
        modifier = Modifier
            .padding(top = 70.dp)
    ) {

        LatestAmiiboCard(
            amiiboLatest = amiiboLatest,
            navigateToDetails = navigateToDetails
        )
        Spacer(modifier = Modifier.height(5.dp))

        Divider(
            modifier = Modifier.height(0.5.dp),
            color = Color.LightGray
        )

        if (isList) {
            val showProgress = rememberSaveable {
                mutableStateOf(true)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (showProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 100.dp),
                        color = Color.Red,
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                ) {
                    if (sortedList != null) {
                        items(count = sortedList.size) {
                            AmiiboListItem(amiibo = sortedList[it], onClick = { amiibo ->
                                navigateToDetails(amiibo)
                            })
                            showProgress.value = false
                        }
                    }
                }

            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(start = 24.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
            ) {
                if (sortedList != null) {
                    items(count = sortedList.size) {
                        AmiiboGridItem(amiibo = sortedList[it]) { amiibo ->
                            navigateToDetails(amiibo)
                        }
                    }
                }
            }
        }
    }
}
