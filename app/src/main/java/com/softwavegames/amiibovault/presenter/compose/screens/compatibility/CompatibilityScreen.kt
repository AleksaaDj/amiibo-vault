package com.softwavegames.amiibovault.presenter.compose.screens.compatibility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.ads.FullWidthBannerAd
import com.softwavegames.amiibovault.model.AmiiboGames
import com.softwavegames.amiibovault.model.Games3DS
import com.softwavegames.amiibovault.model.GamesSwitch
import com.softwavegames.amiibovault.model.GamesWiiU
import com.softwavegames.amiibovault.presenter.compose.common.TextSwitch
import com.softwavegames.amiibovault.domain.util.Console
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    amiiboGames: AmiiboGames?,
    onBackClick: () -> Unit,
    isPortrait: Boolean,
    amiiboName: String,
    onCardClick: () -> Unit,
    onSelectionChange: () -> Unit,
    showBannerAd: Boolean
) {

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showErrorScreen by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = Color.Red,
        ),
        title = { Text(text = amiiboName) },
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
        listOf(Console.SWITCH, Console.DS, Console.WII)
    }

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextSwitch(
            modifier = Modifier
                .padding(top = if (isPortrait) 100.dp else 90.dp, start = 10.dp, end = 10.dp),
            selectedIndex = selectedIndex,
            items = items,
            onSelectionChange = {
                onSelectionChange()
                selectedIndex = it
                selectedTab = selectedIndex
            }
        )
        if (amiiboGames != null) {
            when (selectedTab) {
                0 -> SwitchGamesList(
                    consoleGamesList = amiiboGames.gamesSwitch,
                    isPortrait = isPortrait,
                    onCardClick = onCardClick,
                    showBannerAd = showBannerAd
                )

                1 -> DsGamesList(
                    consoleGamesList = amiiboGames.games3DS, isPortrait = isPortrait,
                    onCardClick = onCardClick,
                    showBannerAd = showBannerAd
                )

                2 -> WiiGamesList(
                    consoleGamesList = amiiboGames.gamesWiiU, isPortrait = isPortrait,
                    onCardClick = onCardClick,
                    showBannerAd = showBannerAd
                )
            }
        } else {
            AnimatedVisibility(visible = !showErrorScreen) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 150.dp),
                    color = Color.Red,
                )
                LaunchedEffect(this) {
                    delay(4500)
                    showErrorScreen = true
                }
            }
        }
        AnimatedVisibility(visible = showErrorScreen) {
            EmptyScreen()
        }
    }
}


@Composable
fun SwitchGamesList(
    consoleGamesList: List<GamesSwitch>,
    isPortrait: Boolean,
    onCardClick: () -> Unit,
    showBannerAd: Boolean
) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    if (isPortrait) {
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
    }
    if (consoleGamesList.isNotEmpty()) {
        if(showBannerAd) {
            BannerAd()
        }
        AnimatedVisibility(visibleState = state) {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        start = if (isPortrait) 0.dp else 50.dp,
                        end = if (isPortrait) 0.dp else 50.dp
                    ),
                verticalArrangement = Arrangement.run { spacedBy(10.dp) },
                contentPadding = PaddingValues(15.dp),

                ) {
                items(count = consoleGamesList.size) {
                    ConsoleGameItem(
                        title = consoleGamesList[it].gameName,
                        usage = consoleGamesList[it].amiiboUsage[0].Usage,
                        onCardClick = onCardClick
                    )
                }
            }
        }
    } else {
        NoCompatibleGamesScreen(console = Console.SWITCH)
    }
}

@Composable
fun DsGamesList(consoleGamesList: List<Games3DS>, isPortrait: Boolean, onCardClick: () -> Unit, showBannerAd: Boolean) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    if (isPortrait) {
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
    }
    if (consoleGamesList.isNotEmpty()) {
        if(showBannerAd) {
            BannerAd()
        }
        AnimatedVisibility(visibleState = state) {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        start = if (isPortrait) 0.dp else 50.dp,
                        end = if (isPortrait) 0.dp else 50.dp
                    ),
                verticalArrangement = Arrangement.run { spacedBy(10.dp) },
                contentPadding = PaddingValues(15.dp),

                ) {
                items(count = consoleGamesList.size) {
                    ConsoleGameItem(
                        title = consoleGamesList[it].gameName,
                        usage = consoleGamesList[it].amiiboUsage[0].Usage,
                        onCardClick = onCardClick
                    )
                }
            }
        }
    } else {
        NoCompatibleGamesScreen(console = Console.DS)
    }
}

@Composable
fun WiiGamesList(consoleGamesList: List<GamesWiiU>, isPortrait: Boolean, onCardClick: () -> Unit, showBannerAd: Boolean) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    if (isPortrait) {
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
    }
    if (consoleGamesList.isNotEmpty()) {
        if(showBannerAd) {
            BannerAd()
        }
        AnimatedVisibility(visibleState = state) {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        start = if (isPortrait) 0.dp else 50.dp,
                        end = if (isPortrait) 0.dp else 50.dp
                    ),
                verticalArrangement = Arrangement.run { spacedBy(10.dp) },
                contentPadding = PaddingValues(15.dp),

                ) {
                items(count = consoleGamesList.size) {
                    ConsoleGameItem(
                        title = consoleGamesList[it].gameName,
                        usage = consoleGamesList[it].amiiboUsage[0].Usage,
                        onCardClick = onCardClick
                    )
                }
            }
        }
    } else {
        NoCompatibleGamesScreen(console = Console.WII)
    }
}


@Composable
fun ConsoleGameItem(title: String, usage: String, onCardClick: () -> Unit) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),

        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {
            onCardClick()
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = title
                )
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = {
                        onCardClick()
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
            }
            if (expandedState) {
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = usage
                )
            }
        }
    }
}

@Composable
fun NoCompatibleGamesScreen(console: String) {
    Column(
        modifier = Modifier
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(150.dp),
            painter = painterResource(id = R.drawable.no_results), contentDescription = null
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.no_compatible_games_for, console),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyScreen() {
    Column(
        modifier = Modifier
            .padding(10.dp),
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
            text = stringResource(R.string.error_getting_compatible_games),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun BannerAd() {
    Card(
        modifier = Modifier.height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        FullWidthBannerAd(modifier = Modifier.fillMaxWidth())
    }
}

