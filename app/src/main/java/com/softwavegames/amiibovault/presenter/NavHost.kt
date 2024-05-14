package com.softwavegames.amiibovault.presenter

import android.content.Context
import android.media.SoundPool
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.softwavegames.amiibovault.AppNavigation
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.presenter.compose.screens.collection.CollectionScreenViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.collection.MyCollectionScreen
import com.softwavegames.amiibovault.presenter.compose.screens.compatibility.CompatibilityConsolesViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.compatibility.CompatibilityScreen
import com.softwavegames.amiibovault.presenter.compose.screens.details.AmiiboDetailsScreenViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.details.DetailsScreen
import com.softwavegames.amiibovault.presenter.compose.screens.nfcreader.NfcScannerScreen
import com.softwavegames.amiibovault.presenter.compose.screens.search.AmiiboListScreen
import com.softwavegames.amiibovault.presenter.compose.screens.search.AmiiboSearchViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.series.AmiiboFromSeriesListViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.series.AmiiboGridScreen
import com.softwavegames.amiibovault.presenter.compose.screens.support.SupportScreen
import com.softwavegames.amiibovault.util.Constants
import kotlinx.coroutines.launch


@Composable
fun BottomNavigationBar(
    context: Context,
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    navigationSelectedItem: MutableState<Int>,
    isPortrait: Boolean
) {
    val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .build()
    val buttonSound = soundPool.load(context, R.raw.button_click, 1)
    val iconSound = soundPool.load(context, R.raw.icon_click, 1)
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val showScrollToTopButtonList by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 10
        }
    }
    val showScrollToTopButtonGrid by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 25
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (bottomBarState.value) {
                if (isPortrait) {
                    NavigationBar(
                        modifier = Modifier
                            .height(70.dp),
                        contentColor = Color.White,
                        containerColor = Color.Black
                    ) {
                        BottomNavigationItem().bottomNavigationItems()
                            .forEachIndexed { index, navigationItem ->
                                NavigationBarItem(
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.DarkGray
                                    ),
                                    selected = index == navigationSelectedItem.value,
                                    icon = {
                                        Icon(
                                            navigationItem.icon,
                                            contentDescription = navigationItem.label,
                                            tint = Color.White
                                        )
                                    },
                                    onClick = {
                                        navigationSelectedItem.value = index
                                        navController.navigate(navigationItem.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                    }
                }
            }
        },

        floatingActionButton = {
            if (bottomBarState.value) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            soundPool.play(buttonSound, 1F, 1F, 1, 0, 1F)
                            navigateToNfcScanner(navController)
                        },
                        shape = CircleShape,
                        containerColor = if (isPortrait) Color.DarkGray else Color.Black,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                            .offset(y = if (isPortrait) 70.dp else 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_nfc),
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = if (isPortrait) FabPosition.Center else FabPosition.Start,
    ) { paddingValues ->

        if (!isPortrait && bottomBarState.value) {
            NavigationRail(
                contentColor = Color.White,
                containerColor = Color.Black
            ) {
                BottomNavigationItem().bottomNavigationItems()
                    .forEachIndexed { index, navigationItem ->
                        NavigationRailItem(
                            colors = NavigationRailItemDefaults.colors(
                                indicatorColor = Color.DarkGray
                            ),
                            selected = index == navigationSelectedItem.value,
                            icon = {
                                Icon(
                                    navigationItem.icon,
                                    contentDescription = navigationItem.label,
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                navigationSelectedItem.value = index
                                navController.navigate(navigationItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
            }
        }
        NavHost(
            navController = navController,
            startDestination = AppNavigation.BottomNavScreens.AmiiboList.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(AppNavigation.BottomNavScreens.AmiiboList.route) {
                val viewModel: AmiiboSearchViewModel = hiltViewModel()
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    Constants.PARSED_AMIIBO,
                    null
                )
                AmiiboListScreen(
                    amiiboList = viewModel.amiiboList.observeAsState().value,
                    amiiboLatest = viewModel.amiiboLatest.observeAsState().value,
                    navigateToDetails = { amiibo ->
                        soundPool.play(buttonSound, 1F, 1F, 1, 0, 1F)
                        navigateToDetails(
                            navController = navController,
                            amiibo = amiibo,
                        )
                    },
                    onSearchQueryChange = { query, isList ->
                        viewModel.searchAmiibo(query)
                        if (query.isNotEmpty()) {
                            coroutineScope.launch {
                                scrollToTop(isList, listState, gridState)
                            }
                        }
                    },
                    onChangeListClick = {
                        soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                    },
                    onScrollToTopClick = { isList ->
                        soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                        coroutineScope.launch {
                            scrollToTop(isList, listState, gridState)
                        }
                    },
                    isPortrait = isPortrait,
                    listState = listState,
                    gridState = gridState,
                    showScrollToTopList = showScrollToTopButtonList,
                    showScrollToTopGrid = showScrollToTopButtonGrid,
                )
            }
            composable(AppNavigation.NavigationItem.DetailsScreen.route) {
                navController.previousBackStackEntry?.savedStateHandle?.get<Amiibo?>(Constants.PARSED_AMIIBO)
                    ?.let {
                        val viewModel: AmiiboDetailsScreenViewModel = hiltViewModel()
                        LaunchedEffect(true) {
                            viewModel.checkIsAmiiboSavedInMyCollection(it.tail)
                            viewModel.checkIsAmiiboSavedInWishList(it.tail)
                        }
                        DetailsScreen(
                            amiibo = it,
                            onBackClick = {
                                soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                                navController.navigateUp()
                            },
                            navigateToMore = { amiibo ->
                                soundPool.play(buttonSound, 1F, 1F, 1, 0, 1F)
                                navigateToMore(navController = navController, amiibo = amiibo)
                            },
                            navigateToCompatibility = { amiibo ->
                                soundPool.play(buttonSound, 1F, 1F, 1, 0, 1F)
                                navigateToCompatibility(
                                    navController = navController,
                                    amiibo = amiibo
                                )
                            },
                            saveToMyCollection = { amiibo ->
                                soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                                viewModel.viewModelScope.launch {
                                    viewModel.upsertAmiiboToMyCollection(amiibo)
                                }
                            },
                            removeFromMyCollection = { amiibo ->
                                soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                                viewModel.viewModelScope.launch {
                                    viewModel.deleteAmiiboFromMyCollection(amiibo)
                                }
                            },
                            isAmiiboSavedMyCollection = viewModel.amiiboSavedMyCollection.observeAsState(),
                            saveToWishlist = { amiibo ->
                                soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                                viewModel.viewModelScope.launch {
                                    viewModel.upsertAmiiboToWishList(amiibo)
                                }
                            },
                            removeFromWishlist = { amiibo ->
                                soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                                viewModel.viewModelScope.launch {
                                    viewModel.deleteAmiiboFromWishList(amiibo)
                                }
                            },
                            isAmiiboSavedWishlist = viewModel.amiiboSavedWishlist.observeAsState(),
                            isPortrait = isPortrait
                        )
                    }
            }
            composable(AppNavigation.NavigationItem.AmiiboSeriesScreen.route) {
                navController.previousBackStackEntry?.savedStateHandle?.get<String?>(Constants.SERIES)
                    ?.let {
                        val viewModel: AmiiboFromSeriesListViewModel = hiltViewModel()
                        LaunchedEffect(true) {
                            viewModel.loadAmiibos(it)
                        }
                        AmiiboGridScreen(
                            amiiboList = viewModel.amiiboList.observeAsState().value,
                            navigateToDetails = { amiibo ->
                                soundPool.play(buttonSound, 1F, 1F, 1, 0, 1F)
                                navigateToDetails(
                                    navController = navController,
                                    amiibo = amiibo
                                )
                            },
                            onBackClick = {
                                soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                                navController.navigateUp()
                            },
                            isPortrait = isPortrait
                        )
                    }
            }

            composable(AppNavigation.NavigationItem.AmiiboCompatibilityScreen.route) {
                navController.previousBackStackEntry?.savedStateHandle?.get<Amiibo?>(Constants.PARSED_AMIIBO)
                    ?.let {
                        val viewModel: CompatibilityConsolesViewModel = hiltViewModel()
                        LaunchedEffect(true) {
                            viewModel.loadAmiiboConsoleInfo(it.tail)
                        }
                        CompatibilityScreen(
                            amiiboGames = viewModel.amiiboConsolesList.observeAsState().value,
                            onBackClick = {
                                soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                                navController.navigateUp()
                            },
                            isPortrait = isPortrait,
                            amiiboName = it.name
                        )
                    }
            }
            composable(AppNavigation.BottomNavScreens.AmiiboMyCollection.route) {
                val viewModel: CollectionScreenViewModel = hiltViewModel()
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    Constants.PARSED_AMIIBO,
                    null
                )
                MyCollectionScreen(
                    amiiboListCollection = viewModel.amiiboListCollection.observeAsState().value,
                    amiiboListWishlist = viewModel.amiiboListWishlist.observeAsState().value,
                    navigateToDetails = { amiibo ->
                        soundPool.play(buttonSound, 1F, 1F, 1, 0, 1F)
                        navigateToDetails(navController = navController, amiibo = amiibo)
                    },
                    isPortrait = isPortrait,
                    onSupportClick = {
                        navigateToSupportScreen(navController)
                    }
                )
            }
            composable(AppNavigation.BottomNavScreens.NfcScanner.route) {
                NfcScannerScreen(
                    onBackClick = {
                        soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                        navController.navigateUp()
                    },
                    isPortrait = isPortrait
                )
            }
            composable(AppNavigation.NavigationItem.SupportScreen.route) {
                SupportScreen(
                    onBackClick = {
                        soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = ""
) {

    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Outlined.Search,
                route = AppNavigation.BottomNavScreens.AmiiboList.route
            ),
            BottomNavigationItem(
                label = "Collection",
                icon = Icons.Filled.FavoriteBorder,
                route = AppNavigation.BottomNavScreens.AmiiboMyCollection.route
            ),
        )
    }
}

private fun navigateToDetails(
    navController: NavController, amiibo: Amiibo
) {
    navController.currentBackStackEntry?.savedStateHandle?.set(Constants.PARSED_AMIIBO, amiibo)
    navController.navigate(
        route = AppNavigation.NavigationItem.DetailsScreen.route
    )
}

private fun navigateToMore(navController: NavController, amiibo: Amiibo) {
    navController.currentBackStackEntry?.savedStateHandle?.set(Constants.SERIES, amiibo.gameSeries)
    navController.navigate(
        route = AppNavigation.NavigationItem.AmiiboSeriesScreen.route
    )
}

private fun navigateToCompatibility(navController: NavController, amiibo: Amiibo) {
    navController.currentBackStackEntry?.savedStateHandle?.set(Constants.PARSED_AMIIBO, amiibo)
    navController.navigate(
        route = AppNavigation.NavigationItem.AmiiboCompatibilityScreen.route
    )
}

private fun navigateToNfcScanner(navController: NavController) {
    navController.navigate(
        route = AppNavigation.BottomNavScreens.NfcScanner.route
    )
}

private fun navigateToSupportScreen(navController: NavController) {
    navController.navigate(
        route = AppNavigation.NavigationItem.SupportScreen.route
    )
}

private suspend fun scrollToTop(
    isList: Boolean,
    listState: LazyListState,
    gridState: LazyGridState
) {
    if (isList) {
        listState.scrollToItem(0)
    } else {
        gridState.scrollToItem(0)
    }
}