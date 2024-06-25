package com.softwavegames.amiibovault.presenter.compose.screens.series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.presenter.compose.common.AmiiboGridItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmiiboGridScreen(
    amiiboList: List<Amiibo>?,
    navigateToDetails: (Amiibo) -> Unit,
    onBackClick: () -> Unit,
    isPortrait: Boolean
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = { amiiboList?.get(0)?.gameSeries?.let { Text(text = it) } },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.Red,
                )
            }
        },
    )

    val showProgress = remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp, start = 5.dp, end = 0.dp, bottom = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showProgress.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 150.dp),
                color = Color.Red,
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isPortrait) 3 else 5),
            modifier = Modifier
                .padding(
                    start = if (isPortrait) 24.dp else 45.dp,
                    end = if (isPortrait) 20.dp else 40.dp
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
        ) {
            if (amiiboList != null) {
                items(count = amiiboList.size) {
                    AmiiboGridItem(
                        amiibo = amiiboList[it],
                        showInCollection = true,
                        onAmiiboClick = { amiibo ->
                            navigateToDetails(amiibo)
                        })
                    showProgress.value = false
                }
            }
        }
    }
}