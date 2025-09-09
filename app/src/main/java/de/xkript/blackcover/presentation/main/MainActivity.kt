package de.xkript.blackcover.presentation.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.xkript.blackcover.R
import de.xkript.blackcover.core.ui.theme.BlackCoverTheme
import de.xkript.blackcover.core.util.Constant
import de.xkript.blackcover.core.util.GooglePlayHelper
import de.xkript.blackcover.core.util.dataStores.DataStoreBlackCover
import de.xkript.blackcover.core.util.myExtension.isNetworkAvailable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var dataStoreBlackCover: DataStoreBlackCover
    
    // Local
    private val viewModel: MainViewModel by viewModels()
    private val googlePlayHelper = GooglePlayHelper(this)
    private var isPurchaseFlowLaunched = false
    private var isPremiumCheckedBefore = false
    
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Nothing.
        }
        else {
            getNotificationPermission()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get notification permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getNotificationPermission()
        }
        setContent {
            BlackCoverTheme {
                
                val uiState by viewModel.uiState.collectAsState()
                MainScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }
        }
        observeToEvents()
    }
    
    override fun onResume() {
        super.onResume()
        val chackedOnFirstLaunch = dataStoreBlackCover.getBoolean(Constant.DS_BLACK_COVER_PREMIUM_CHECKED_ON_FIRST_LAUNCH, false)
        val isSubUser = dataStoreBlackCover.getBoolean(Constant.DS_BLACK_COVER_IS_SUB_USER, false)
        val isLifetimeUser = dataStoreBlackCover.getBoolean(Constant.DS_BLACK_COVER_IS_LIFETIME_USER, false)
        if (isNetworkAvailable()) {
            if (chackedOnFirstLaunch.not()) {
                checkUserIsSubscriber()
            }
            if (isSubUser && isPremiumCheckedBefore.not()) {
                isPremiumCheckedBefore = true
                checkUserIsSubscriber()
            }
            if (isLifetimeUser && isPremiumCheckedBefore.not()) {
                isPremiumCheckedBefore = true
                checkUserIsSubscriber()
            }
            if (isPurchaseFlowLaunched) {
                isPurchaseFlowLaunched = false
                checkUserIsSubscriber()
            }
        }
        else {
            // Nothing
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        googlePlayHelper.disconnect()
    }
    
    // Other
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getNotificationPermission() {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    
    private fun checkUserIsSubscriber() {
        googlePlayHelper.getPurchasedInappList(
            onPurchasedInappList = { inappList ->
                if (inappList.isNotEmpty()) { // User has lifetime
                    dataStoreBlackCover.setValue(Constant.DS_BLACK_COVER_PREMIUM_CHECKED_ON_FIRST_LAUNCH, true)
                    dataStoreBlackCover.setValue(Constant.DS_BLACK_COVER_IS_LIFETIME_USER, true)
                    viewModel.changeIsSubscriber(true)
                }
                else { // User has not lifetime, check subs
                    dataStoreBlackCover.setValue(Constant.DS_BLACK_COVER_IS_LIFETIME_USER, false)
                    googlePlayHelper.getPurchasedSubList(
                        onPurchasedSubList = { subsList ->
                            if (subsList.isNotEmpty()) {
                                dataStoreBlackCover.setValue(Constant.DS_BLACK_COVER_PREMIUM_CHECKED_ON_FIRST_LAUNCH, true)
                                dataStoreBlackCover.setValue(Constant.DS_BLACK_COVER_IS_SUB_USER, true)
                                viewModel.changeIsSubscriber(true)
                            }
                            else { // Is regular user
                                dataStoreBlackCover.setValue(Constant.DS_BLACK_COVER_IS_SUB_USER, false)
                            }
                        },
                        onFailure = {
                            showErrorToast()
                        }
                    )
                }
            },
            onFailure = {
                showErrorToast()
            }
        )
    }
    
    private fun showErrorToast() {
        Toast.makeText(this, getString(R.string.unknown_error_please_contact_support), Toast.LENGTH_SHORT).show()
    }
    
    private fun observeToEvents() = lifecycleScope.launchWhenStarted {
        viewModel.event.collect {
            when (it) {
                is ViewModelEvent.OnBuyClick -> {
                    when (it.productId) {
                        Constant.PRODUCT_ID_YEARLY_SUB -> {
                            isPurchaseFlowLaunched = true
                            googlePlayHelper.purchasedSubscription(
                                productId = Constant.PRODUCT_ID_YEARLY_SUB,
                                onFailure = {
                                    showErrorToast()
                                }
                            )
                        }
                        Constant.PRODUCT_ID_MONTHLY_SUG -> {
                            isPurchaseFlowLaunched = true
                            googlePlayHelper.purchasedSubscription(
                                productId = Constant.PRODUCT_ID_MONTHLY_SUG,
                                onFailure = {
                                    showErrorToast()
                                }
                            )
                        }
                        Constant.PRODUCT_ID_LIFETIME -> {
                            isPurchaseFlowLaunched = true
                            googlePlayHelper.purchasedInappProduct(
                                productId = Constant.PRODUCT_ID_LIFETIME,
                                onFailure = {
                                    showErrorToast()
                                }
                            )
                        }
                    }
                    
                }
            }
        }
    }
    
}
