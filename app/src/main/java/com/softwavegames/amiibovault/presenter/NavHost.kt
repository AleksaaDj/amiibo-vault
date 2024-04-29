package com.softwavegames.amiibovault.presenter

import android.content.Context
import android.media.SoundPool
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
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
import com.softwavegames.amiibovault.util.Constants
import kotlinx.coroutines.launch


@Composable
fun BottomNavigationBar(
    context: Context,
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    navigationSelectedItem: MutableState<Int>,
) {
    val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .build()
    val buttonSound = soundPool.load(context, R.raw.button_click, 1)
    val iconSound = soundPool.load(context, R.raw.icon_click, 1)


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (bottomBarState.value) {
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
                        backgroundColor = Color.DarkGray,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                            .offset(y = 70.dp)
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
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->


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
                    onSearchQueryChange = {
                        viewModel.searchAmiibo(it)
                    },
                    onChangeListClick = {
                        soundPool.play(iconSound, 1F, 1F, 1, 0, 1F)
                    }
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
                        )
                    }
            }
            composable(AppNavigation.NavigationItem.AmiiboGridScreen.route) {
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
                            }
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
                            }
                        )
                    }
            }
            composable(AppNavigation.BottomNavScreens.AmiiboMyCollection.route) {
                val viewModel: CollectionScreenViewModel = hiltViewModel()
                MyCollectionScreen(
                    amiiboListCollection = viewModel.amiiboListCollection.observeAsState().value,
                    amiiboListWishlist = viewModel.amiiboListWishlist.observeAsState().value,
                    navigateToDetails = { amiibo ->
                        soundPool.play(buttonSound, 1F, 1F, 1, 0, 1F)
                        navigateToDetails(navController = navController, amiibo = amiibo)
                    }
                )
            }
            composable(AppNavigation.BottomNavScreens.NfcScanner.route) {
                NfcScannerScreen(
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
        route = AppNavigation.NavigationItem.AmiiboGridScreen.route
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