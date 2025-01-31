package com.softwavegames.amiibovault.presenter.compose.screens.collection

import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import com.softwavegames.amiibovault.domain.billing.PurchaseHelper
import com.softwavegames.amiibovault.domain.util.Constants
import com.softwavegames.amiibovault.presenter.compose.navhost.NavHostViewModel
import com.softwavegames.amiibovault.presenter.compose.navhost.navigateToDetails
import com.softwavegames.amiibovault.presenter.compose.navhost.navigateToSupportScreen
import com.softwavegames.amiibovault.presenter.compose.navhost.playSound

@Composable
fun CollectionScreenInit(
    viewModel: CollectionScreenViewModel,
    navController: NavController,
    soundPool: SoundPool,
    buttonSound: Int,
    iconSound: Int,
    isSoundOn: MutableState<Boolean>,
    isPortrait: Boolean,
    isDark: Boolean,
    navHostViewModel: NavHostViewModel,
    openRemoveAdsCollectionDialog: MutableState<Boolean>,
    buyEnabledAds: Boolean,
    purchaseHelper: PurchaseHelper
) {
    val isFilterTypeCollectionSelected = rememberSaveable { mutableStateOf(false) }
    val filterTypeCollection = rememberSaveable { mutableStateOf("") }
    val isFilterSetCollectionSelected = rememberSaveable { mutableStateOf(false) }
    val filterSetCollection = rememberSaveable { mutableStateOf("") }
    val isSortTypeCollectionSelected = rememberSaveable { mutableStateOf(false) }

    val isFilterTypeWishlistSelected = rememberSaveable { mutableStateOf(false) }
    val filterTypeWishlist = rememberSaveable { mutableStateOf("") }
    val isFilterSetWishlistSelected = rememberSaveable { mutableStateOf(false) }
    val filterSetWishlist = rememberSaveable { mutableStateOf("") }
    val isSortTypeWishlistSelected = rememberSaveable { mutableStateOf(false) }

    viewModel.getAmiiboFilteredFromCollection(
        filterTypeCollection.value,
        filterSetCollection.value
    )

    viewModel.getAmiiboFilteredFromWishlist(
        filterTypeWishlist.value,
        filterSetWishlist.value
    )
    MyCollectionScreen(
        amiiboListCollection = viewModel.amiiboListCollection.observeAsState().value,
        amiiboListWishlist = viewModel.amiiboListWishlist.observeAsState().value,
        navigateToDetails = { amiibo ->
            playSound(soundPool, buttonSound, isSoundOn.value)
            navigateToDetails(
                navController = navController,
                amiibo = amiibo,
            )
        },
        isPortrait = isPortrait,
        onSupportClick = {
            playSound(soundPool, iconSound, isSoundOn.value)
            navigateToSupportScreen(navController)
        },
        onSelectionChange = { index ->
            if (index == 0) {
                isFilterTypeCollectionSelected.value = false
                isFilterSetCollectionSelected.value = false
                isSortTypeCollectionSelected.value = false
                filterTypeCollection.value = ""
                filterSetCollection.value = ""
                viewModel.sortTypeCollection.value = null
                viewModel.getAmiiboFilteredFromCollection(
                    "",
                    ""
                )
            } else {
                isFilterTypeWishlistSelected.value = false
                isFilterSetWishlistSelected.value = false
                isSortTypeWishlistSelected.value = false
                filterTypeWishlist.value = ""
                filterSetWishlist.value = ""
                viewModel.sortTypeWishList.value = null
                viewModel.getAmiiboFilteredFromWishlist(
                    "",
                    ""
                )
            }
            playSound(soundPool, iconSound, isSoundOn.value)
        },
        isDarkMode = isDark,
        onThemeModeClicked = {
            playSound(soundPool, iconSound, isSoundOn.value)
            navHostViewModel.setThemeMode(!isDark)
        },
        onPurchaseClicked = {
            playSound(soundPool, iconSound, isSoundOn.value)
            openRemoveAdsCollectionDialog.value = true
        },
        buyEnabled = buyEnabledAds,
        onRemoveAdsClicked = {
            playSound(soundPool, buttonSound, isSoundOn.value)
            purchaseHelper.makeNoAdsPurchase()
            openRemoveAdsCollectionDialog.value = false
        },
        onDialogAdsDismissed = {
            playSound(soundPool, buttonSound, isSoundOn.value)
            openRemoveAdsCollectionDialog.value = false
        },
        openRemoveAdsDialog = openRemoveAdsCollectionDialog,

        onFilterTypeCollectionSelected = { typeFilter ->
            playSound(soundPool, iconSound, isSoundOn.value)
            viewModel.getAmiiboFilteredFromCollection(
                typeFilter,
                filterSetCollection.value
            )
            filterTypeCollection.value = typeFilter
            isFilterTypeCollectionSelected.value = true
        },
        onFilterTypeCollectionRemoved = {
            playSound(soundPool, iconSound, isSoundOn.value)
            isFilterTypeCollectionSelected.value = false
            filterTypeCollection.value = ""
            viewModel.getAmiiboFilteredFromCollection(
                filterTypeCollection.value,
                filterSetCollection.value
            )
        },
        onFilterSetCollectionSelected = { setFilter ->
            playSound(soundPool, iconSound, isSoundOn.value)
            viewModel.getAmiiboFilteredFromCollection(
                filterTypeCollection.value,
                setFilter
            )
            filterSetCollection.value = setFilter
            isFilterSetCollectionSelected.value = true
        },
        onFilterSetCollectionRemoved = {
            playSound(soundPool, iconSound, isSoundOn.value)
            isFilterSetCollectionSelected.value = false
            filterSetCollection.value = ""
            viewModel.getAmiiboFilteredFromCollection(
                filterTypeCollection.value,
                filterSetCollection.value
            )
        },
        onSortTypeCollectionSelected = { sortTypeSelected ->
            playSound(soundPool, iconSound, isSoundOn.value)
            viewModel.sortAmiiboCollectionList(
                sortTypeSelected,
                viewModel.amiiboListCollection.value
            )
            isSortTypeCollectionSelected.value = true
            viewModel.sortTypeCollection.value = sortTypeSelected
        },
        onSortTypeCollectionRemoved = {
            playSound(soundPool, iconSound, isSoundOn.value)
            isSortTypeCollectionSelected.value = false
            viewModel.sortTypeCollection.value = Constants.SORT_TYPE_NAME_ASC
            viewModel.sortAmiiboCollectionList(
                viewModel.sortTypeCollection.value.toString(),
                viewModel.amiiboListCollection.value
            )
        },
        onFilterTypeWishlistSelected = { typeFilter ->
            playSound(soundPool, iconSound, isSoundOn.value)
            viewModel.getAmiiboFilteredFromWishlist(
                typeFilter,
                filterSetWishlist.value
            )
            filterTypeWishlist.value = typeFilter
            isFilterTypeWishlistSelected.value = true
        },
        onFilterTypeWishlistRemoved = {
            playSound(soundPool, iconSound, isSoundOn.value)
            isFilterTypeWishlistSelected.value = false
            filterTypeWishlist.value = ""
            viewModel.getAmiiboFilteredFromWishlist(
                filterTypeWishlist.value,
                filterSetWishlist.value
            )
        },
        onFilterSetWishlistSelected = { setFilter ->
            playSound(soundPool, iconSound, isSoundOn.value)
            viewModel.getAmiiboFilteredFromWishlist(
                filterTypeWishlist.value,
                setFilter
            )
            filterSetWishlist.value = setFilter
            isFilterSetWishlistSelected.value = true
        },
        onFilterSetWishlistRemoved = {
            playSound(soundPool, iconSound, isSoundOn.value)
            isFilterSetWishlistSelected.value = false
            filterSetWishlist.value = ""
            viewModel.getAmiiboFilteredFromWishlist(
                filterTypeWishlist.value,
                filterSetWishlist.value
            )
        },
        onSortTypeWishlistSelected = { sortTypeSelected ->
            playSound(soundPool, iconSound, isSoundOn.value)
            viewModel.sortAmiiboWishlist(
                sortTypeSelected,
                viewModel.amiiboListWishlist.value
            )
            isSortTypeWishlistSelected.value = true
            viewModel.sortTypeWishList.value = sortTypeSelected
        },
        onSortTypeWishlistRemoved = {
            playSound(soundPool, iconSound, isSoundOn.value)
            isSortTypeWishlistSelected.value = false
            viewModel.sortTypeWishList.value = Constants.SORT_TYPE_NAME_ASC
            viewModel.sortAmiiboWishlist(
                viewModel.sortTypeWishList.value.toString(),
                viewModel.amiiboListWishlist.value
            )
        }
    )
}