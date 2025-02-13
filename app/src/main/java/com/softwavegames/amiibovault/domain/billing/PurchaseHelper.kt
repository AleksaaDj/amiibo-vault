package com.softwavegames.amiibovault.domain.billing

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.common.collect.ImmutableList
import com.softwavegames.amiibovault.domain.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class PurchaseHelper(private val activity: Activity) {
    private lateinit var billingClient: BillingClient
    private lateinit var productNoAdsDetails: ProductDetails
    private lateinit var productScanDetails: ProductDetails
    private lateinit var purchase: Purchase

    private val productNoAdsID = Constants.BILLING_PRODUCT_NO_ADS_ID
    private val productScanID = Constants.BILLING_PRODUCT_SCAN_ID

    private val _buyEnabledNoAds = MutableStateFlow(true)
    val buyEnabledNoAds = _buyEnabledNoAds.asStateFlow()

    private val _buyEnabledScan = MutableStateFlow(true)
    val buyEnabledScan = _buyEnabledScan.asStateFlow()

    private val _statusText = MutableStateFlow(Constants.BILLING_STATUS_INITIALIZING)
    val statusText = _statusText.asStateFlow()

    init {
        billingSetup()
    }

    private fun billingSetup() {
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                if (billingResult.responseCode ==
                    BillingClient.BillingResponseCode.OK
                    && purchases != null
                ) {
                    for (purchase in purchases) {
                        completePurchase(purchase)
                    }
                } else if (billingResult.responseCode ==
                    BillingClient.BillingResponseCode.USER_CANCELED
                ) {
                    _statusText.value = Constants.BILLING_STATUS_PURCHASE_CANCELED
                } else {
                    _statusText.value = Constants.BILLING_STATUS_PURCHASE_ERROR
                }
            }
        billingClient = BillingClient.newBuilder(activity)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .setListener(purchasesUpdatedListener)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(
                billingResult: BillingResult
            ) {
                if (billingResult.responseCode ==
                    BillingClient.BillingResponseCode.OK
                ) {
                    _statusText.value = Constants.BILLING_STATUS_CLIENT_CONNECTED
                    queryProduct()
                } else {
                    _statusText.value = Constants.BILLING_STATUS_CLIENT_FAILED
                }
            }

            override fun onBillingServiceDisconnected() {
                _statusText.value = Constants.BILLING_STATUS_CLIENT_LOST
            }
        })
    }

    fun queryProduct() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productNoAdsID)
                        .setProductType(
                            BillingClient.ProductType.INAPP
                        )
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productScanID)
                        .setProductType(
                            BillingClient.ProductType.INAPP
                        )
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { _, productDetailsList ->
            if (productDetailsList.isNotEmpty()) {
                for (productDetails in productDetailsList) {
                    when (productDetails.productId) {
                        Constants.BILLING_PRODUCT_NO_ADS_ID -> {
                            productNoAdsDetails = productDetails
                        }

                        Constants.BILLING_PRODUCT_SCAN_ID -> {
                            productScanDetails = productDetails
                        }
                    }
                }

            } else {
                _statusText.value = Constants.BILLING_STATUS_NO_PRODUCTS
                _buyEnabledNoAds.value = false
                _buyEnabledScan.value = false
            }
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (product in purchaseList) {
                    if (product.products[0].toString() == Constants.BILLING_PRODUCT_NO_ADS_ID) {
                        _buyEnabledNoAds.value = false
                    }
                    if (product.products[0].toString() == Constants.BILLING_PRODUCT_SCAN_ID) {
                        _buyEnabledScan.value = false
                    }
                }
            }
        }
    }

    private fun completePurchase(item: Purchase) {
        purchase = item
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                CoroutineScope(Dispatchers.IO).launch {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                        when (purchase.products[0]) {
                            Constants.BILLING_PRODUCT_NO_ADS_ID -> {
                                _buyEnabledNoAds.value = false
                            }
                            Constants.BILLING_PRODUCT_SCAN_ID -> {
                                _buyEnabledScan.value = false
                            }
                        }
                        _statusText.value = Constants.BILLING_STATUS_PURCHASE_COMPLETE
                    }
                }
            }
        }
    }

    fun makeNoAdsPurchase() {
        if (::productNoAdsDetails.isInitialized) {
            openBillingFlow(productNoAdsDetails)
        } else {
            _statusText.value = Constants.BILLING_STATUS_PRODUCT_DETAILS_UNKNOWN
        }
    }

    fun makeScanPurchase() {
        if (::productScanDetails.isInitialized) {
            openBillingFlow(productScanDetails)
        } else {
            _statusText.value = Constants.BILLING_STATUS_PRODUCT_DETAILS_UNKNOWN
        }
    }

    private fun openBillingFlow(productDetails: ProductDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                ImmutableList.of(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }
}