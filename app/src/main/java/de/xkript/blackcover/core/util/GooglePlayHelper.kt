package de.xkript.blackcover.core.util

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import de.xkript.blackcover.core.BlackCoverApp
import de.xkript.blackcover.core.util.myExtension.log

@Suppress("unused")
class GooglePlayHelper(
    private val activity: Activity
) {
    
    // Local
    private var isConnected = false
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        billingResultLogger("purchasesUpdatedListener", billingResult)
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    log("Product purchased successfully!")
                }
            }
        }
        else {
            // Handle any other error codes.
        }
    }
    
    @Suppress("DEPRECATION") // .enablePendingPurchases()
    private val billingClient = BillingClient.newBuilder(BlackCoverApp.getInstance())
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()
    
    init {
        log("GooglePlayHelper created !!")
    }
    
    // Connection
    private fun connect(
        connectionSucceed: () -> Unit,
        connectionFailed: () -> Unit,
    ) {
        if (isConnected.not()) {
            billingClient.startConnection(object : BillingClientStateListener {
                
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    billingResultLogger("Connect", billingResult)
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        isConnected = true
                        connectionSucceed()
                    }
                }
                
                override fun onBillingServiceDisconnected() {
                    log("Connect : result-> Failed")
                    connectionFailed()
                }
            })
        }
        else {
            connectionSucceed()
        }
    }
    
    fun disconnect() {
        log("disconnect :")
        if (isConnected) {
            isConnected = false
            billingClient.endConnection()
        }
    }
    
    // Get products
    private fun getInappProductDetails(
        productId: String,
        querySucceed: (productDetailsList: MutableList<ProductDetails>) -> Unit,
        queryFailed: (responseCode: Int, message: String?) -> Unit,
    ) {
        // Tools
        val queryParams = QueryProductDetailsParams
            .newBuilder()
            .setProductList(
                mutableListOf(
                    QueryProductDetailsParams
                        .Product
                        .newBuilder()
                        .setProductId(productId)
                        .setProductType(ProductType.INAPP)
                        .build()
                )
            )
            .build()
        
        // Get productDetails
        billingClient.queryProductDetailsAsync(queryParams) { billingResult, productDetailsList ->
            billingResultLogger("getInappProductDetails", billingResult)
            if (billingResult.responseCode == BillingResponseCode.OK) {
                querySucceed(productDetailsList)
            }
            else {
                queryFailed(billingResult.responseCode, billingResult.debugMessage)
            }
        }
    }
    
    private fun getSubscriptionProductDetails(
        productId: String,
        querySucceed: (productDetailsList: MutableList<ProductDetails>) -> Unit,
        queryFailed: (responseCode: Int, message: String?) -> Unit,
    ) {
        // Tools
        val queryParams = QueryProductDetailsParams
            .newBuilder()
            .setProductList(
                mutableListOf(
                    QueryProductDetailsParams
                        .Product
                        .newBuilder()
                        .setProductId(productId)
                        .setProductType(ProductType.SUBS)
                        .build()
                )
            )
            .build()
        
        // Get productDetails
        billingClient.queryProductDetailsAsync(queryParams) { billingResult, productDetailsList ->
            billingResultLogger("getInappProductDetails", billingResult)
            if (billingResult.responseCode == BillingResponseCode.OK) {
                querySucceed(productDetailsList)
            }
            else {
                queryFailed(billingResult.responseCode, billingResult.debugMessage)
            }
        }
    }
    
    // Purchased Inapp
    fun purchasedInappProduct(
        productId: String,
        onFailure: () -> Unit,
    ) {
        connect(
            connectionSucceed = {
                getInappProductDetails(
                    productId = productId,
                    querySucceed = {
                        if (it.size > 0)
                            purchasedInappProduct(it[0])
                        else
                            onFailure()
                    },
                    queryFailed = { _, _ ->
                        onFailure()
                    }
                )
            },
            connectionFailed = {
                onFailure()
            }
        )
    }
    
    private fun purchasedInappProduct(productDetails: ProductDetails) {
        // Tools
        val productDetailsParamsList = listOf(
            BillingFlowParams
                .ProductDetailsParams
                .newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams
            .newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        // Launch the billing flow
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        billingResultLogger("getInappInfo", billingResult)
    }
    
    // Purchased Subscription
    fun purchasedSubscription(
        productId: String,
        onFailure: () -> Unit,
    ) {
        connect(
            connectionSucceed = {
                getSubscriptionProductDetails(
                    productId = productId,
                    querySucceed = {
                        if (it.size > 0)
                            purchasedSubscription(it[0])
                        else
                            onFailure()
                    },
                    queryFailed = { _, _ ->
                        onFailure()
                    }
                )
            },
            connectionFailed = {
                onFailure()
            }
        )
    }
    
    private fun purchasedSubscription(productDetails: ProductDetails) {
        // Tools
        val productDetailsParamsList = listOf(
            BillingFlowParams
                .ProductDetailsParams
                .newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(productDetails.subscriptionOfferDetails.toString())
                .build()
        )
        val billingFlowParams = BillingFlowParams
            .newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        // Launch the billing flow
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        billingResultLogger("buySubscription", billingResult)
    }
    
    // Consume
    private fun consume() {}
    
    // Get purchased products
    fun getPurchasedSubList(
        onPurchasedSubList: (List<Purchase>) -> Unit,
        onFailure: () -> Unit,
    ) {
        connect(
            connectionSucceed = {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build()
                
                billingClient.queryPurchasesAsync(params) { billingResult, purchasedSubList ->
                    billingResultLogger("getUserCurrentSubsInfo", billingResult)
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        onPurchasedSubList(purchasedSubList)
                    }
                    else {
                        onFailure()
                    }
                }
            },
            connectionFailed = {
                onFailure()
            }
        )
    }
    
    fun getPurchasedInappList(
        onPurchasedInappList: (List<Purchase>) -> Unit,
        onFailure: () -> Unit,
    ) {
        connect(
            connectionSucceed = {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.INAPP)
                    .build()
                
                billingClient.queryPurchasesAsync(params) { billingResult, purchasedInappList ->
                    billingResultLogger("getPurchasedInappList", billingResult)
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        onPurchasedInappList(purchasedInappList)
                    }
                    else {
                        onFailure()
                    }
                }
            },
            connectionFailed = {
                onFailure()
            }
        )
    }
    
    // Other
    @Suppress("DEPRECATION") // SERVICE_TIMEOUT
    private fun getBillingResultName(responseCode: Int): String {
        return when (responseCode) {
            BillingResponseCode.SERVICE_TIMEOUT -> "SERVICE_TIMEOUT"
            BillingResponseCode.FEATURE_NOT_SUPPORTED -> "FEATURE_NOT_SUPPORTED"
            BillingResponseCode.SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
            BillingResponseCode.OK -> "OK"
            BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
            BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
            BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
            BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
            BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
            BillingResponseCode.ERROR -> "ERROR"
            BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
            BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
            BillingResponseCode.NETWORK_ERROR -> "NETWORK_ERROR"
            else -> ""
        }
    }
    
    private fun billingResultLogger(target: String, billingResult: BillingResult) = log("$target : result -> ${getBillingResultName(billingResult.responseCode)} message -> ${billingResult.debugMessage}")
    
}

