package com.softwavegames.amiibovault.domain.util

object Constants {

    const val BASE_URL = "https://www.amiiboapi.com/api/"
    const val DB_NAME = "amiibo_db"

    const val IMGUR_BASE_URL = "https://api.imgur.com/3/image"
    const val IMGUR_CLIENT_ID = "67857bbca14679e"

    const val FIREBASE_DB_AMIIBO = "Amiibo"
    const val FIREBASE_DB_POSTS = "SharedContent"
    const val FIREBASE_DB_POSTS_POST_ID = "postId"
    const val FIREBASE_DB_POSTS_LIKES = "likes"

    const val PARSED_AMIIBO = "amiibo"

    const val SERIES = "series"

    const val DONATE_URL = "https://ko-fi.com/softwavegames"

    const val AMIIBO_WORLDWIDE_SIZE = 855f

    const val BILLING_PRODUCT_NO_ADS_ID = "no_ads"
    const val BILLING_PRODUCT_SCAN_ID = "amiibo_scan"

    const val SHARED_PREFERENCES_NAME = "amiibo_preferences"
    const val SHARED_PREFERENCES_OPENED_ADS_TIMES = "app_opened_times"
    const val SHARED_PREFERENCES_OPENED_RATE_TIMES = "app_opened_rate_times"
    const val SHARED_PREFERENCES_RATE_CLICKED = "rate_clicked"
    const val SHARED_PREFERENCES_IS_DARK_MODE = "is_dark_mode"
    const val SHARED_PREFERENCES_LIKES_LIST = "likes_list"
    const val SHARED_PREFERENCES_OPENED_COLLECTION_POSTS = "collection_posts_opened_times"

    const val BILLING_STATUS_INITIALIZING = "Initializing..."
    const val BILLING_STATUS_CLIENT_CONNECTED = "Billing Client Connected"
    const val BILLING_STATUS_CLIENT_FAILED = "Billing Client Connection Failure"
    const val BILLING_STATUS_CLIENT_LOST = "Billing Client Connection Lost"
    const val BILLING_STATUS_NO_PRODUCTS = "No Matching Products Found"
    const val BILLING_STATUS_PRODUCT_DETAILS_UNKNOWN = "Product details unknown"
    const val BILLING_STATUS_PURCHASE_CANCELED = "Purchase Canceled"
    const val BILLING_STATUS_PURCHASE_ERROR = "Purchase Error"
    const val BILLING_STATUS_PURCHASE_COMPLETE = "Purchase Completed"

    const val OPENED_TIMES_TARGET_ADS_DIALOG = 2
    const val OPENED_TIMES_TARGET_RATE_DIALOG = 3

    const val SORT_TYPE_NAME_ASC = "Name (A-Z)"
    const val SORT_TYPE_NAME_DSC = "Name (Z-A)"
    const val SORT_TYPE_RELEASE_ASC = "Release (Asc)"
    const val SORT_TYPE_RELEASE_DSC = "Release (Dsc)"
    const val SORT_TYPE_SET_ASC = "Set (A-Z)"
    const val SORT_TYPE_SET_DSC = "Set (Z-A)"


    enum class CollectionLists {
        MY_COLLECTION_LIST,
        WISHLIST_LIST,
    }
}

object Console {
    const val SWITCH = "Switch"
    const val DS = "3DS"
    const val WII = "WiiU"
}

object ColorsIndex {
    const val RED_INDEX = 1
    const val PINK_INDEX = 2
    const val BLUE_INDEX = 3
    const val BLACK_INDEX = 4
    const val YELLOW_INDEX = 5
    const val PURPLE_INDEX = 6
    const val TEAL_INDEX = 7
    const val OLIVE_INDEX = 8
    const val GREY_INDEX = 9
    const val GREEN_INDEX = 10

}

object AvatarsIndex {
    const val LINK = 1
    const val PEACH = 2
    const val MARIO = 3
    const val ALEX = 4
    const val BOKOBLIN = 5
    const val BOWSER = 6
    const val INKLING = 7
    const val ISABELLE = 8
    const val KIRBY = 9
    const val LUCINA = 10
    const val NABIRU = 11
    const val PIKACHU = 12
    const val SAMUS = 13
    const val YOSHI = 14
}