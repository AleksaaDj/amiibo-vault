package com.softwavegames.amiibovault

class AppNavigation {
    enum class Screen {
        AmiiboDetails,
        AmiiboGridScreen,
        AmiiboCompatibilityScreen,
        SupportScreen,
        CreatePostScreen
    }

    enum class BottomScreen {
        AmiiboList,
        NfcScanner,
        AmiiboCollection,
        CollectionPosts
    }


    sealed class NavigationItem(val route: String) {
        data object DetailsScreen : NavigationItem(Screen.AmiiboDetails.name)
        data object AmiiboSeriesScreen : NavigationItem(Screen.AmiiboGridScreen.name)
        data object AmiiboCompatibilityScreen : NavigationItem(Screen.AmiiboCompatibilityScreen.name)
        data object SupportScreen : NavigationItem(Screen.SupportScreen.name)
        data object CreatePostScreen : NavigationItem(Screen.CreatePostScreen.name)
    }

    sealed class BottomNavScreens(val route : String) {
        data object AmiiboList : BottomNavScreens(BottomScreen.AmiiboList.name)
        data object NfcScanner  : BottomNavScreens(BottomScreen.NfcScanner.name)
        data object CollectionPosts  : BottomNavScreens(BottomScreen.CollectionPosts.name)
        data object AmiiboMyCollection  : BottomNavScreens(BottomScreen.AmiiboCollection.name)
    }
}