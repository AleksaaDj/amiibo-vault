package com.softwavegames.amiibovault

class AppNavigation {
    enum class Screen {
        AmiiboDetails,
        AmiiboGridScreen,
        AmiiboCompatibilityScreen,
        SupportScreen
    }

    enum class BottomScreen {
        AmiiboList,
        AmiiboCollection,
        NfcScanner
    }


    sealed class NavigationItem(val route: String) {
        data object DetailsScreen : NavigationItem(Screen.AmiiboDetails.name)
        data object AmiiboSeriesScreen : NavigationItem(Screen.AmiiboGridScreen.name)
        data object AmiiboCompatibilityScreen : NavigationItem(Screen.AmiiboCompatibilityScreen.name)
        data object SupportScreen : NavigationItem(Screen.SupportScreen.name)

    }

    sealed class BottomNavScreens(val route : String) {
        data object AmiiboList : BottomNavScreens(BottomScreen.AmiiboList.name)
        data object AmiiboMyCollection  : BottomNavScreens(BottomScreen.AmiiboCollection.name)
        data object NfcScanner  : BottomNavScreens(BottomScreen.NfcScanner.name)
    }
}