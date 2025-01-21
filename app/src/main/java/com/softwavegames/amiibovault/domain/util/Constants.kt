package com.softwavegames.amiibovault.domain.util

object Constants {

    const val BASE_URL = "https://www.amiiboapi.com/api/"
    const val DB_NAME = "amiibo_db"

    const val FIREBASE_DB_AMIIBO = "Amiibo"

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