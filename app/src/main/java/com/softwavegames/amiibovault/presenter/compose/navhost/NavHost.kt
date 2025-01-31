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
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.softwavegames.amiibovault.AppNavigation
import com.softwavegames.amiibovault.R
import com.softwavegames.amiibovault.domain.ads.showInterstitial
import com.softwavegames.amiibovault.domain.billing.PurchaseHelper
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.domain.util.ThemeState
import com.softwavegames.amiibovault.model.Amiibo
import com.softwavegames.amiibovault.presenter.compose.screens.collection.CollectionScreenInit
import com.softwavegames.amiibovault.presenter.compose.screens.collection.CollectionScreenViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.compatibility.CompatibilityConsolesViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.compatibility.CompatibilityScreen
import com.softwavegames.amiibovault.presenter.compose.screens.createpost.CreatePostScreen
import com.softwavegames.amiibovault.presenter.compose.screens.createpost.CreatePostViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.details.AmiiboDetailsScreenViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.details.DetailsScreen
import com.softwavegames.amiibovault.presenter.compose.screens.main.CollectionPostsViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.main.MainScannerPostsScreen
import com.softwavegames.amiibovault.presenter.compose.screens.search.AmiiboListScreen
import com.softwavegames.amiibovault.presenter.compose.screens.search.AmiiboSearchViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.series.AmiiboFromSeriesListViewModel
import com.softwavegames.amiibovault.presenter.compose.screens.series.AmiiboGridScreen
import com.softwavegames.amiibovault.presenter.compose.screens.support.SupportScreen
import kotlinx.coroutines.launch


@Composable
fun BottomNavigationBar(
    context: Context,
    activity: Activity,
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    navigationSelectedItem: MutableState<Int>,
    isPortrait: Boolean,
    purchaseHelper: PurchaseHelper
) {

    val navHostViewModel: NavHostViewModel = hiltViewModel()
    val statusText by purchaseHelper.statusText.collectAsState("")
    val buyEnabledAds by purchaseHelper.buyEnabledNoAds.collectAsState(false)
    val buyEnabledScan by purchaseHelper.buyEnabledScan.collectAsState(false)
    val openRemoveAdsDialog = remember { mutableStateOf(false) }
    val openRemoveAdsCollectionDialog = remember { mutableStateOf(false) }
    val openInDevelopmentDialog = remember { mutableStateOf(false) }
    val openRateDialog = remember { mutableStateOf(false) }
    val isDark = ThemeState.darkModeState.value
    val reviewManager = remember { ReviewManagerFactory.create(context) }
    var reviewInfo: ReviewInfo? by rememberSaveable { mutableStateOf(null) }
    reviewManager.requestReviewFlow().addOnCompleteListener {
        if (it.isSuccessful) {
            reviewInfo = it.result
        }
    }

    val openedAdTimes =
        rememberSaveable { mutableIntStateOf(navHostViewModel.getAppOpenedAdsTimes()) }
    val openedRateTimes =
        rememberSaveable { mutableIntStateOf(navHostViewModel.getAppOpenedRateTimes()) }

    if (buyEnabledAds) {
        navHostViewModel.setAppOpenedAdsTimes(openedAdTimes.intValue + 1)
        if (isShowRemoveAdDialog(openedAdTimes.intValue)) {
            openRemoveAdsDialog.value = true
            navHostViewModel.setAppOpenedAdsTimes(0)
            openedAdTimes.intValue = 0
        }
    }
    if (!navHostViewModel.isRateClicked()) {
        navHostViewModel.setAppOpenedRateTimes(openedRateTimes.intValue + 1)
        if (isShowRateDialog(openedRateTimes.intValue)) {
            openRateDialog.value = true
            navHostViewModel.setAppOpenedRateTimes(0)
            openedRateTimes.intValue = 0
        }
    }

    if (navHostViewModel.getCollectionPostsOpenedTimes() == 0) {
        openInDevelopmentDialog.value = true
    } else {
        navHostViewModel.setCollectionPostsOpenedTimes(1)
    }

    if (statusText == Constants.BILLING_STATUS_PURCHASE_COMPLETE) {
        Toast.makeText(context, stringResource(R.string.product_purchased), Toast.LENGTH_LONG)
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
    val listStatePosts = rememberLazyListState()
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
                                90.dp
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
                val isSortTypeSelected = rememberSaveable { mutableStateOf(false) }

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
                            amiibo = amiibo
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
                    onSortTypeSelected = { sortTypeSelected ->
                        playSound(soundPool, iconSound, isSoundOn.value)
                        viewModel.sortAmiiboList(sortTypeSelected, viewModel.amiiboList.value)
                        isSortTypeSelected.value = true
                        viewModel.sortType.value = sortTypeSelected
                    },
                    onSortTypeRemoved = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        isSortTypeSelected.value = false
                        viewModel.sortType.value = Constants.SORT_TYPE_NAME_ASC
                        viewModel.sortAmiiboList(
                            viewModel.sortType.value.toString(),
                            viewModel.amiiboList.value
                        )
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
                    openRateDialog = openRateDialog,
                    onDialogRateDismissed = { openRateDialog.value = false },
                    onRateClicked = {
                        openRateDialog.value = false
                        reviewInfo?.let {
                            reviewManager.launchReviewFlow(activity, it)
                            navHostViewModel.setRateClicked()
                        }
                    },
                    onRemoveAdsClicked = {
                        playSound(soundPool, buttonSound, isSoundOn.value)
                        purchaseHelper.makeNoAdsPurchase()
                        openRemoveAdsDialog.value = false
                    },
                    onDialogAdsDismissed = {
                        playSound(soundPool, buttonSound, isSoundOn.value)
                        openRemoveAdsDialog.value = false
                    },
                    openRemoveAdsDialog = openRemoveAdsDialog,
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
                            isPortrait = isPortrait,
                            showAd = {
                                if (isShowAd(interstitialClickTimes.intValue, buyEnabledAds)) {
                                    showInterstitial(activity = activity) {
                                    }
                                }
                                interstitialClickTimes.intValue += 1
                            },
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
                                amiibo = amiibo
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
                            },
                            showBannerAd = buyEnabledAds,
                            showAd = {
                                if (isShowAd(interstitialClickTimes.intValue, buyEnabledAds)) {
                                    showInterstitial(activity = activity) {
                                    }
                                }
                                interstitialClickTimes.intValue += 1
                            },
                        )
                    }
            }

            composable(AppNavigation.BottomNavScreens.AmiiboMyCollection.route) {
                val viewModel: CollectionScreenViewModel = hiltViewModel()
                CollectionScreenInit(
                    viewModel = viewModel,
                    navController = navController,
                    soundPool = soundPool,
                    buttonSound = buttonSound,
                    iconSound = iconSound,
                    isSoundOn = isSoundOn,
                    isPortrait = isPortrait,
                    isDark = isDark,
                    navHostViewModel = navHostViewModel,
                    openRemoveAdsCollectionDialog = openRemoveAdsCollectionDialog,
                    buyEnabledAds = buyEnabledAds,
                    purchaseHelper = purchaseHelper,
                )
            }

            composable(AppNavigation.BottomNavScreens.NfcScanner.route) {
                val viewModel: CollectionPostsViewModel = hiltViewModel()
                MainScannerPostsScreen(
                    onBackClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        navController.navigateUp()
                    },
                    isPortrait = isPortrait,
                    showBannerAd = buyEnabledAds,
                    buyEnabledScan = buyEnabledScan,
                    onPurchaseScanClicked = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        purchaseHelper.makeScanPurchase()
                    },
                    onSelectionChange = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                    },
                    collectionPostList = viewModel.collectionPost.observeAsState().value,
                    onCreatePostClicked = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        navigateToCreatePostScreen(navController)
                    },
                    onLikeDislikeClicked = { postId ->
                        playSound(soundPool, iconSound, isSoundOn.value)
                        viewModel.likeDislikePost(postId)
                    },
                    likedPostsIds = viewModel.likedPostsIds.observeAsState().value,
                    listState = listStatePosts,
                    onConfirmationClicked = {
                        playSound(soundPool, buttonSound, isSoundOn.value)
                        navHostViewModel.setCollectionPostsOpenedTimes(1)
                        openInDevelopmentDialog.value = false
                    },
                    openInDevelopmentDialog = openInDevelopmentDialog
                )
            }

            composable(AppNavigation.NavigationItem.SupportScreen.route) {
                SupportScreen(
                    buyEnabled = buyEnabledAds,
                    onBackClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        navController.navigateUp()
                    },
                    onPurchaseClicked = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        purchaseHelper.makeNoAdsPurchase()
                    },
                    onRateReviewClicked = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        reviewInfo?.let {
                            reviewManager.launchReviewFlow(activity, it)
                            navHostViewModel.setRateClicked()
                        }
                    }
                )
            }
            composable(AppNavigation.NavigationItem.CreatePostScreen.route) {
                val viewModel: CreatePostViewModel = hiltViewModel()
                CreatePostScreen(
                    onBackClick = {
                        playSound(soundPool, iconSound, isSoundOn.value)
                        navController.navigateUp()
                    },
                    isPortrait = isPortrait,
                    showBannerAd = buyEnabledAds,
                    onCreatePostClicked = { collectionPost, bitmap ->
                        playSound(soundPool, iconSound, isSoundOn.value)
                        viewModel.uploadImage(collectionPost, bitmap)
                    },
                    postPublished = viewModel.postPublished.observeAsState().value,
                    onPostPublished = {
                        coroutineScope.launch {
                            listStatePosts.scrollToItem(0)
                        }
                        navController.navigateUp()
                    },
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

fun navigateToDetails(
    navController: NavController,
    amiibo: Amiibo,
) {
    navController.currentBackStackEntry?.savedStateHandle?.set(
        Constants.PARSED_AMIIBO,
        amiibo
    )
    navController.navigate(
        route = AppNavigation.NavigationItem.DetailsScreen.route
    )
}

fun navigateToMore(navController: NavController, amiibo: Amiibo) {
    navController.currentBackStackEntry?.savedStateHandle?.set(Constants.SERIES, amiibo.gameSeries)
    navController.navigate(
        route = AppNavigation.NavigationItem.AmiiboSeriesScreen.route
    )
}

fun navigateToCompatibility(
    navController: NavController, amiibo: Amiibo,
) {
    navController.currentBackStackEntry?.savedStateHandle?.set(
        Constants.PARSED_AMIIBO,
        amiibo
    )
    navController.navigate(
        route = AppNavigation.NavigationItem.AmiiboCompatibilityScreen.route
    )
}

fun navigateToNfcScanner(navController: NavController) {
    navController.navigate(
        route = AppNavigation.BottomNavScreens.NfcScanner.route
    )
}

fun navigateToSupportScreen(navController: NavController) {
    navController.navigate(
        route = AppNavigation.NavigationItem.SupportScreen.route
    )
}

fun navigateToCreatePostScreen(navController: NavController) {
    navController.navigate(
        route = AppNavigation.NavigationItem.CreatePostScreen.route
    )
}

fun playSound(soundPool: SoundPool, sound: Int, isSoundOn: Boolean) {
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

private fun isShowRemoveAdDialog(openedTimes: Int): Boolean {
    return openedTimes == Constants.OPENED_TIMES_TARGET_ADS_DIALOG
}

private fun isShowRateDialog(openedTimes: Int): Boolean {
    return openedTimes == Constants.OPENED_TIMES_TARGET_RATE_DIALOG
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