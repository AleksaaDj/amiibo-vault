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
    private lateinit var productDetails: ProductDetails
    private lateinit var purchase: Purchase

    private val productID = Constants.BILLING_PRODUCT_ID

    private val _productName = MutableStateFlow("Searching...")
    // For later use
    //val productName = _productName.asStateFlow()

    private val _buyEnabled = MutableStateFlow(true)
    val buyEnabled = _buyEnabled.asStateFlow()

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
                    queryProduct(productID)
                } else {
                    _statusText.value = Constants.BILLING_STATUS_CLIENT_FAILED
                }
            }

            override fun onBillingServiceDisconnected() {
                _statusText.value = Constants.BILLING_STATUS_CLIENT_LOST
            }
        })
    }

    fun queryProduct(productId: String) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
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
                productDetails = productDetailsList[0]
                _productName.value = "Product: " + productDetails.name
            } else {
                _statusText.value = Constants.BILLING_STATUS_NO_PRODUCTS
                _buyEnabled.value = false
            }
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _buyEnabled.value = purchaseList.isEmpty()
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
                        _buyEnabled.value = false
                        _statusText.value = Constants.BILLING_STATUS_PURCHASE_COMPLETE
                    }

                }
            }
        }
    }

    fun makePurchase() {
        if (::productDetails.isInitialized) {
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
        } else {
            _statusText.value = Constants.BILLING_STATUS_PRODUCT_DETAILS_UNKNOWN
        }
    }
}