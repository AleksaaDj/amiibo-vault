package com.softwavegames.amiibovault.domain.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject

class FirebaseEventsLogs @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {
    companion object {

        // Search Screen
        const val AMIIBO_SEARCH_SCREEN_OPENED = "search_screen_opened"
        const val AMIIBO_LIST = "list_clicked"
        const val AMIIBO_GRID = "grid_clicked"
        const val AMIIBO_FILTER_TYPE_SEARCH = "filter_type_search_selected"
        const val AMIIBO_FILTER_SET_SEARCH = "filter_set_search_selected"
        const val AMIIBO_SORT_SEARCH = "sort_search_selected"
        const val AMIIBO_SOUND = "sound_clicked"

        // Collection Screen
        const val AMIIBO_DETAILS_OPENED = "details_opened"
        const val AMIIBO_ADD_COLLECTION = "add_collection_clicked"
        const val AMIIBO_ADD_WISHLIST = "add_wishlist_clicked"
        const val AMIIBO_AMAZON = "amazon_clicked"
        const val AMIIBO_MORE = "more_clicked"
        const val AMIIBO_USAGE = "usage_clicked"

        // Scanner Screen
        const val AMIIBO_SCANNER_SCREEN_OPENED = "scanner_screen_opened"

        // Community Screen
        const val AMIIBO_COMMUNITY_SCREEN_OPENED = "community_screen_opened"
        const val AMIIBO_LIKED = "liked_clicked"
        const val AMIIBO_CREATE_POST = "create_post_clicked"
        const val AMIIBO_CREATE_AVATAR = "create_avatar_clicked"

        // Collections Screen
        const val AMIIBO_COLLECTIONS_SCREEN_OPENED = "collections_screen_opened"
        const val AMIIBO_REMOVE_ADS = "remove_ads_clicked"
        const val AMIIBO_THEME = "theme_clicked"
        const val AMIIBO_IMAGE_DOWNLOAD = "image_download_clicked"
        const val AMIIBO_IMAGE_DOWNLOAD_CONFIRMED = "image_download_confirmed"
        const val AMIIBO_FILTER_TYPE_COLLECTION = "filter_type_collection_selected"
        const val AMIIBO_FILTER_SET_COLLECTION = "filter_set_collection_selected"
        const val AMIIBO_SORT_COLLECTION = "sort_collection_selected"
        const val AMIIBO_FILTER_TYPE_WISHLIST = "filter_type_wishlist_selected"
        const val AMIIBO_FILTER_SET_WISHLIST = "filter_set_wishlist_selected"
        const val AMIIBO_SORT_WISHLIST = "sort_wishlist_selected"

        // Support Screen
        const val AMIIBO_SUPPORT_SCREEN_OPENED = "support_screen_opened"
        const val AMIIBO_RATE = "rate_clicked"
        const val AMIIBO_KOFI = "kofi_clicked"
    }

    fun logEvent(eventId: String, id: String, name: String) {
        firebaseAnalytics.logEvent(eventId) {
            param(FirebaseAnalytics.Param.ITEM_ID, id)
            param(FirebaseAnalytics.Param.ITEM_NAME, name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        }
    }
}