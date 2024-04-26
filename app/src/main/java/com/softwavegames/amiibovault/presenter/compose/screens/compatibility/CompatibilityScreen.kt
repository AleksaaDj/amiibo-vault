package com.softwavegames.amiibovault.presenter.compose.screens.compatibility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.AmiiboGames
import com.softwavegames.amiibovault.model.Games3DS
import com.softwavegames.amiibovault.model.GamesSwitch
import com.softwavegames.amiibovault.model.GamesWiiU
import com.softwavegames.amiibovault.presenter.compose.common.TextSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    amiiboGames: AmiiboGames?,
    onBackClick: () -> Unit,
) {

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = { Text(text = stringResource(R.string.console_usage)) },
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
    val items = remember {
        listOf("Switch", "3DS", "WiiU")
    }

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Column {
        TextSwitch(
            modifier = Modifier
                .padding(top = 70.dp, start = 10.dp, end = 10.dp),
            selectedIndex = selectedIndex,
            items = items,
            onSelectionChange = {
                selectedIndex = it
                selectedTab = selectedIndex
            }
        )

        if (amiiboGames != null) {
            when (selectedTab) {
                0 -> SwitchGamesList(consoleGamesList = amiiboGames.gamesSwitch)
                1 -> DsGamesList(consoleGamesList = amiiboGames.games3DS)
                2 -> WiiGamesList(consoleGamesList = amiiboGames.gamesWiiU)
            }
        }
    }
}

@Composable
fun SwitchGamesList(consoleGamesList: List<GamesSwitch>) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(50.dp)
    )
    {
        Card(
            modifier = Modifier
                .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            shape = RoundedCornerShape(25.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            )

        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.switch_console),
                contentDescription = null
            )
        }
    }
    if (consoleGamesList.isNotEmpty()) {
        AnimatedVisibility(visibleState = state) {
            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.run { spacedBy(4.dp) },
                contentPadding = PaddingValues(10.dp),

                ) {
                items(count = consoleGamesList.size) {
                    ConsoleGameItem(
                        title = consoleGamesList[it].gameName,
                        usage = consoleGamesList[it].amiiboUsage[0].Usage
                    )
                }
            }
        }
    } else {
        NoCompatibleGamesScreen(console = "Nintendo Switch")
    }
}

@Composable
fun DsGamesList(consoleGamesList: List<Games3DS>) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(start = 115.dp, end = 115.dp, top = 22.dp, bottom = 24.dp)
    )
    {
        Card(
            modifier = Modifier, shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            )

        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ds_console),
                contentDescription = null
            )
        }
    }
    if (consoleGamesList.isNotEmpty()) {
        AnimatedVisibility(visibleState = state) {
            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.run { spacedBy(4.dp) },
                contentPadding = PaddingValues(10.dp),

                ) {
                items(count = consoleGamesList.size) {
                    ConsoleGameItem(
                        title = consoleGamesList[it].gameName,
                        usage = consoleGamesList[it].amiiboUsage[0].Usage
                    )
                }
            }
        }
    } else {
        NoCompatibleGamesScreen(console = "Nintendo 3DS")
    }
}

@Composable
fun WiiGamesList(consoleGamesList: List<GamesWiiU>) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(start = 50.dp, end = 50.dp, top = 35.dp, bottom = 38.dp)
    )
    {
        Card(
            modifier = Modifier
                .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            shape = RoundedCornerShape(43.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            )

        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.wii_console),
                contentDescription = null
            )
        }
    }
    if (consoleGamesList.isNotEmpty()) {
        AnimatedVisibility(visibleState = state) {
            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.run { spacedBy(4.dp) },
                contentPadding = PaddingValues(10.dp),

                ) {
                items(count = consoleGamesList.size) {
                    ConsoleGameItem(
                        title = consoleGamesList[it].gameName,
                        usage = consoleGamesList[it].amiiboUsage[0].Usage
                    )
                }
            }
        }
    } else {
        NoCompatibleGamesScreen(console = "Nintendo Wii U")
    }
}


@Composable
fun ConsoleGameItem(title: String, usage: String) {

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            text = title
        )
        Text(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            text = usage
        )
    }
}

@Composable
fun NoCompatibleGamesScreen(console: String) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            text = stringResource(R.string.no_compatible_games_for, console),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

