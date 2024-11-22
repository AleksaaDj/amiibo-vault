package com.softwavegames.amiibovault.presenter.compose.navhost

import android.app.Activity
import android.content.Context
import android.media.SoundPool
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.softwavegames.amiibovault.domain.ads.showInterstitial
import com.softwavegames.amiibovault.domain.billing.PurchaseHelper
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
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.NavigationTypeHelper
import kotlinx.coroutines.launch


@Composable
fun BottomNavigationBar(
    context: Context,
    activity: Activity,
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    navigationSelectedItem: MutableState<Int>,
    isPortrait: Boolean,
    purchaseHelper: PurchaseHelper,
    navigationMode: NavigationTypeHelper
) {

    val navHostViewModel: NavHostViewModel = hiltViewModel()
    val statusText by purchaseHelper.statusText.collectAsState("")
    val buyEnabled by purchaseHelper.buyEnabled.collectAsState(false)
    val openAlertDialog = remember { mutableStateOf(false) }
    val openedTimes = rememberSaveable { mutableIntStateOf(navHostViewModel.getAppOpenedTimes()) }

    if (buyEnabled) {
        navHostViewModel.setAppOpenedTimes(openedTimes.intValue + 1)
        if (openedTimes.intValue == Constants.OPENED_TIMES_TARGET) {
            openAlertDialog.value = true
            navHostViewModel.setAppOpenedTimes(0)
            openedTimes.intValue = 0
        }
    }

    if (statusText == Constants.BILLING_STATUS_PURCHASE_COMPLETE) {
        Toast.makeText(context, stringResource(R.string.remove_ads_purchased), Toast.LENGTH_LONG)
            .show()
    }

    val interstitialClickTimes = rememberSaveable { mutableIntStateOf(1) }
    val isSoundOn = rememberSaveable { mutableStateOf(true) }
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
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (bottomBarState.value) {
                if (isPortrait) {
                    NavigationBar(
                        modifier = Modifier
                            .height(
                                when (navigationMode) {
                                    NavigationTypeHelper.GESTURE -> 70.dp
                                    NavigationTypeHelper.TWO_BUTTON -> 80.dp
                                    NavigationTypeHelper.THREE_BUTTON -> 100.dp
                                }
                            ),
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
                                        playSound(soundPool, iconSound, isSoundOn.value)
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
                            playSound(soundPool, buttonSound, isSoundOn.value)
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
                                playSound(soundPool, iconSound, isSoundOn.value)
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
                val isList = rememberSaveable { mutableStateOf(true) }
                val isFilterTypeSelected = rememberSaveable { mutableStateOf(false) }
                val filterType = rememberSaveable { mutableStateOf("") }
                val isFilterSetSelected = rememberSaveable { mutableStateOf(false) }
                val filterSet = rememberSaveable { mutableStateOf("") }
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    Constants.PARSED_AMIIBO,
                    null
                )
                AmiiboListScreen(
                    amiiboCollection = viewModel.amiiboListCollection.observeAsState().value,
                    amiiboWishList = viewModel.amiiboListWishlist.observeAsState().value,
                    amiiboList = viewModel.amiiboList.observeAsState().value,
                    amiiboLatest = viewModel.amiiboLatest.observeAsState().value,
                    navigateToDetails = { amiibo ->
                        playSound(soundPool, buttonSound, isSoundOn.value)
                        navigateToDetails(
                            navController = navController,
                            amiibo = amiibo,
                            interstitialClickTimes = interstitialClickTimes.intValue,
                            activity = activity,
                            addInterstitialClickTime = {
                                interstitialClickTimes.intValue += 1
                            },
                            buyEnabled = buyEnabled
                        )
                    },
                    onLayoutChange = {
                        isList.value = it
                    },
                    onSearchQueryChange = { query ->
                        if (isFilterTypeSelected.value && filterType.value.isNotEmpty() || isFilterSetSelected.value && filterSet.value.isNotEmpty()) {
                            viewModel.searchAmiiboFiltered(
                                query,
                                filterType.value,
                                filterSet.value
                            )
                        } else {
                            viewModel.searchAmiibo(query)
                        }
                        if (query.isNotEmpty()) {
                            coroutineScope.launch {
                                scrollToTop(isList.value, listState, gridState)
                            }
                        }
                    },
                    onChangeListClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                    },
                    onScrollToTopClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        coroutineScope.launch {
                            scrollToTop(isList.value, listState, gridState)
                        }
                    },
                    isPortrait = isPortrait,
                    listState = listState,
                    gridState = gridState,
                    showScrollToTopList = showScrollToTopButtonList,
                    showScrollToTopGrid = showScrollToTopButtonGrid,
                    onFilterTypeSelected = { typeFilter ->
                        playSound(soundPool, iconSound, isSoundOn.value)
                        viewModel.searchAmiiboFiltered("", typeFilter, filterSet.value)
                        filterType.value = typeFilter
                        isFilterTypeSelected.value = true
                    },
                    onFilterTypeRemoved = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        isFilterTypeSelected.value = false
                        filterType.value = ""
                        viewModel.searchAmiiboFiltered("", filterType.value, filterSet.value)
                    },
                    onFilterSetSelected = { setFilter ->
                        playSound(soundPool, iconSound, isSoundOn.value)
                        viewModel.searchAmiiboFiltered("", filterType.value, setFilter)
                        filterSet.value = setFilter
                        isFilterSetSelected.value = true
                    },
                    onFilterSetRemoved = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        isFilterSetSelected.value = false
                        filterSet.value = ""
                        viewModel.searchAmiiboFiltered("", filterType.value, filterSet.value)
                    },
                    saveToWishlist = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        viewModel.viewModelScope.launch {
                            viewModel.upsertAmiiboToWishList(it)
                        }
                    },
                    removeFromWishlist = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        viewModel.viewModelScope.launch {
                            viewModel.deleteAmiiboFromWishList(it)
                        }
                    },
                    onSoundVolumeChanged = {
                        isSoundOn.value = it
                    },
                    openRemoveAdsDialog = openAlertDialog,
                    onRemoveAdsClicked = {
                        purchaseHelper.makePurchase()
                        openAlertDialog.value = false
                    },
                    onDialogDismissed = {
                        openAlertDialog.value = false
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
                                playSound(soundPool, iconSound, isSoundOn.value)
                                navController.navigateUp()
                            },
                            navigateToMore = { amiibo ->
                                playSound(soundPool, buttonSound, isSoundOn.value)
                                navigateToMore(navController = navController, amiibo = amiibo)
                            },
                            navigateToCompatibility = { amiibo ->
                                playSound(soundPool, buttonSound, isSoundOn.value)
                                navigateToCompatibility(
                                    navController = navController,
                                    amiibo = amiibo,
                                    interstitialClickTimes = interstitialClickTimes.intValue,
                                    activity = activity,
                                    addInterstitialClickTime = {
                                        interstitialClickTimes.intValue += 1
                                    },
                                    buyEnabled = buyEnabled
                                )
                            },
                            saveToMyCollection = { amiibo ->
                                playSound(soundPool, iconSound, isSoundOn.value)
                                viewModel.viewModelScope.launch {
                                    viewModel.upsertAmiiboToMyCollection(amiibo)
                                }
                            },
                            removeFromMyCollection = { amiibo ->
                                playSound(soundPool, iconSound, isSoundOn.value)
                                viewModel.viewModelScope.launch {
                                    viewModel.deleteAmiiboFromMyCollection(amiibo)
                                }
                            },
                            isAmiiboSavedMyCollection = viewModel.amiiboSavedMyCollection.observeAsState(),
                            saveToWishlist = { amiibo ->
                                playSound(soundPool, iconSound, isSoundOn.value)
                                viewModel.viewModelScope.launch {
                                    viewModel.upsertAmiiboToWishList(amiibo)
                                }
                            },
                            removeFromWishlist = { amiibo ->
                                playSound(soundPool, iconSound, isSoundOn.value)
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
                val viewModel: AmiiboFromSeriesListViewModel = hiltViewModel()
                var series by rememberSaveable {
                    mutableStateOf("")
                }
                navController.previousBackStackEntry?.savedStateHandle?.get<String?>(Constants.SERIES)
                    ?.let {
                        series = it
                    }
                if (series.isNotEmpty()) {
                    viewModel.loadAmiibos(series)
                    AmiiboGridScreen(
                        amiiboList = viewModel.amiiboList.observeAsState().value,
                        navigateToDetails = { amiibo ->
                            playSound(soundPool, buttonSound, isSoundOn.value)
                            navigateToDetails(
                                navController = navController,
                                amiibo = amiibo,
                                interstitialClickTimes = interstitialClickTimes.intValue,
                                activity = activity,
                                addInterstitialClickTime = {
                                    interstitialClickTimes.intValue += 1
                                },
                                buyEnabled = buyEnabled
                            )
                        },
                        onBackClick = {
                            playSound(soundPool, iconSound, isSoundOn.value)
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
                                playSound(soundPool, iconSound, isSoundOn.value)
                                navController.navigateUp()
                            },
                            isPortrait = isPortrait,
                            amiiboName = it.name,
                            onCardClick = {
                                playSound(soundPool, iconSound, isSoundOn.value)
                            },
                            onSelectionChange = {
                                playSound(soundPool, iconSound, isSoundOn.value)
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
                        playSound(soundPool, buttonSound, isSoundOn.value)
                        navigateToDetails(
                            navController = navController,
                            amiibo = amiibo,
                            activity = activity,
                            buyEnabled = buyEnabled,
                            interstitialClickTimes = interstitialClickTimes.intValue,
                            addInterstitialClickTime = {
                                interstitialClickTimes.intValue += 1
                            })
                    },
                    isPortrait = isPortrait,
                    onSupportClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        navigateToSupportScreen(navController)
                    },
                    onSelectionChange = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                    }
                )
            }
            composable(AppNavigation.BottomNavScreens.NfcScanner.route) {
                NfcScannerScreen(
                    onBackClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        navController.navigateUp()
                    },
                    isPortrait = isPortrait
                )
            }
            composable(AppNavigation.NavigationItem.SupportScreen.route) {
                SupportScreen(
                    buyEnabled = buyEnabled,
                    onBackClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        navController.navigateUp()
                    },
                    onPurchaseClicked = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        purchaseHelper.makePurchase()
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
    navController: NavController,
    amiibo: Amiibo,
    interstitialClickTimes: Int,
    activity: Activity,
    addInterstitialClickTime: () -> Unit,
    buyEnabled: Boolean,
) {
    if (isShowAd(interstitialClickTimes, buyEnabled)) {
        showInterstitial(activity = activity) {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                Constants.PARSED_AMIIBO,
                amiibo
            )
            navController.navigate(
                route = AppNavigation.NavigationItem.DetailsScreen.route
            )
            addInterstitialClickTime()
        }
    } else {
        navController.currentBackStackEntry?.savedStateHandle?.set(Constants.PARSED_AMIIBO, amiibo)
        navController.navigate(
            route = AppNavigation.NavigationItem.DetailsScreen.route
        )
        addInterstitialClickTime()
    }
}

private fun navigateToMore(navController: NavController, amiibo: Amiibo) {
    navController.currentBackStackEntry?.savedStateHandle?.set(Constants.SERIES, amiibo.gameSeries)
    navController.navigate(
        route = AppNavigation.NavigationItem.AmiiboSeriesScreen.route
    )
}

private fun navigateToCompatibility(
    navController: NavController, amiibo: Amiibo, interstitialClickTimes: Int,
    activity: Activity,
    addInterstitialClickTime: () -> Unit,
    buyEnabled: Boolean,
) {
    if (isShowAd(interstitialClickTimes, buyEnabled)) {
        showInterstitial(activity = activity) {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                Constants.PARSED_AMIIBO,
                amiibo
            )
            navController.navigate(
                route = AppNavigation.NavigationItem.AmiiboCompatibilityScreen.route
            )
            addInterstitialClickTime()
        }
    } else {
        navController.currentBackStackEntry?.savedStateHandle?.set(Constants.PARSED_AMIIBO, amiibo)
        navController.navigate(
            route = AppNavigation.NavigationItem.AmiiboCompatibilityScreen.route
        )
        addInterstitialClickTime()
    }
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

private fun playSound(soundPool: SoundPool, sound: Int, isSoundOn: Boolean) {
    if (isSoundOn) {
        soundPool.play(sound, 1F, 1F, 1, 0, 1F)
    }
}

private fun isShowAd(
    interstitialClickTimes: Int,
    buyEnabled: Boolean,
): Boolean {
    return buyEnabled && interstitialClickTimes % 4 == 0
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