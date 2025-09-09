package de.xkript.blackcover.presentation.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.xkript.blackcover.BuildConfig
import de.xkript.blackcover.R
import de.xkript.blackcover.core.BlackCoverApp
import de.xkript.blackcover.core.util.Constant
import de.xkript.blackcover.core.util.dataStores.DataStoreBlackCover
import de.xkript.blackcover.core.util.myExtension.isDevFlaver
import de.xkript.blackcover.core.util.myExtension.isServiceRunning
import de.xkript.blackcover.core.util.service.FloatingFabService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: BlackCoverApp,
    private val dataStoreBlackCover: DataStoreBlackCover,
) : ViewModel() {
    
    // State
    private val _uiState = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()
    private var _event = Channel<ViewModelEvent>()
    val event = _event.receiveAsFlow()
    
    // Local
    
    init {
        initTools()
        checkFabService()
        showTimeDate()
    }
    
    fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.OnPreViewClick           -> {
                if (BuildConfig.DEBUG || isDevFlaver()) {
                    _uiState.update { current ->
                        current.copy(
                            isSubscriber = current.isSubscriber.not()
                        )
                    }
                }
            }
            is MainEvent.OnTimeFontItemClick   -> {
                _uiState.update { current ->
                    current.copy(
                        selectedFontIndex = event.index
                    )
                }
            }
            is MainEvent.OnFabItemClick        -> {
                if (_uiState.value.isSubscriber.not() && (event.index >= 3)) { // Show emblem icon only for regular user and only for premium one
                    showPremiumSheet()
                }
                else {
                    _uiState.update { current ->
                        current.copy(
                            selectedFabIndex = event.index
                        )
                    }
                }
            }
            is MainEvent.OnTabCounterItemClick -> {
                if (_uiState.value.isSubscriber || (event.index == 3 || event.index == 4).not()) {
                    _uiState.update { current ->
                        current.copy(
                            selectedTapCounterIndex = event.index
                        )
                    }
                }
                else {
                    showPremiumSheet()
                }
            }
            is MainEvent.OnAlwaysOnDisplay     -> {
                _uiState.update { current ->
                    current.copy(
                        isSkipUnlockScreenChecked = current.isSkipUnlockScreenChecked.not()
                    )
                }
            }
            is MainEvent.OnHideFloatingButton  -> {
                _uiState.update { current ->
                    current.copy(
                        isHideFloatingButtonChecked = current.isHideFloatingButtonChecked.not()
                    )
                }
            }
            MainEvent.OnStartButton            -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(app).not()) {
                    getOverlayPermission()
                }
                else {
                    _uiState.update { current ->
                        current.copy(
                            hasStarted = true
                        )
                    }
                    startBlackScreen()
                }
                
            }
            MainEvent.OnStopButton             -> {
                _uiState.update { current ->
                    current.copy(
                        hasStarted = false
                    )
                }
                stopBlackScreen()
            }
            MainEvent.OnSheetDismiss           -> {
                _uiState.update { current ->
                    current.copy(
                        isSheetShowed = false
                    )
                }
            }
            MainEvent.OnYearlyPlanClick        -> {
                _uiState.update { current ->
                    current.copy(
                        yearlyPlanSelected = true,
                        monthlyPlanSelected = false,
                        lifetimePlanSelected = false,
                    )
                }
            }
            MainEvent.OnMonthlyPlanClick       -> {
                _uiState.update { current ->
                    current.copy(
                        yearlyPlanSelected = false,
                        monthlyPlanSelected = true,
                        lifetimePlanSelected = false,
                    )
                }
            }
            MainEvent.OnLifetimePlanClick      -> {
                _uiState.update { current ->
                    current.copy(
                        yearlyPlanSelected = false,
                        monthlyPlanSelected = false,
                        lifetimePlanSelected = true,
                    )
                }
            }
            MainEvent.OnBuyPlanClick           -> {
                buyProduct()
            }
        }
    }
    
    private fun initTools() {
        val isLifetimeUser = dataStoreBlackCover.getBoolean(Constant.DS_BLACK_COVER_IS_LIFETIME_USER, false)
        if (isLifetimeUser) {
            _uiState.update { current ->
                current.copy(
                    isSubscriber = true
                )
            }
        }
    }
    
    private fun checkFabService() {
        _uiState.update { current ->
            current.copy(
                hasStarted = app.isServiceRunning(FloatingFabService::class.java)
            )
        }
    }
    
    private fun showTimeDate() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(10) // Delay for one second
                
                // Tools
                val calendar = Calendar.getInstance()
                
                _uiState.update { current ->
                    current.copy(
                        date = getDateInfo(calendar),
                        time = getTimeInfo(calendar),
                    )
                }
            }
        }
    }
    
    private fun getDateInfo(calendar: Calendar): String {
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        val dayOfWeekName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return "$dayOfWeekName, $monthName$dayOfMonth"
    }
    
    private fun getTimeInfo(calendar: Calendar): String {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }
    
    @SuppressLint("InlinedApi")
    private fun getOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${app.packageName}"),
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(intent)
        
    }
    
    private fun startBlackScreen() {
        val intent = Intent(app, FloatingFabService::class.java)
        intent.putExtra("fab_icon_id", fetchFabIconId(uiState.value.selectedFabIndex))
        intent.putExtra("font_id", fetchFontId(uiState.value.selectedFontIndex))
        intent.putExtra("tap_counter", uiState.value.selectedTapCounterIndex)
        intent.putExtra("is_skip_unlock_screen", uiState.value.isSkipUnlockScreenChecked)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            app.startForegroundService(intent)
        }
        else {
            app.startService(intent)
        }
    }
    
    private fun stopBlackScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            app.stopService(Intent(app, FloatingFabService::class.java))
        }
        else {
            app.stopService(Intent(app, FloatingFabService::class.java))
        }
    }
    
    private fun fetchFabIconId(selectedFabIndex: Int): Int {
        return when (selectedFabIndex) {
            0    -> R.drawable.ic_stroke_button
            1    -> R.drawable.ic_full_moon
            2    -> R.drawable.ic_music_note
            3    -> R.drawable.ic_music
            4    -> R.drawable.ic_lock
            5    -> R.drawable.ic_skull
            6    -> R.drawable.ic_clown
            else -> R.drawable.ic_stroke_button
        }
    }
    
    private fun fetchFontId(selectedFontIndex: Int): Int {
        return when (selectedFontIndex) {
            0    -> R.font.oswald_regular
            1    -> R.font.a_bee_zee_regular
            2    -> R.font.abril_fatface_regular
            3    -> R.font.agdasima_regular
            4    -> R.font.aldrich_regular
            5    -> R.font.allerta_regular
            6    -> R.font.alumni_sans_pinstripe_regular
            7    -> R.font.audiowide_regular
            else -> R.font.oswald_regular
        }
    }
    
    fun changeIsSubscriber(newValue: Boolean) {
        _uiState.update { current ->
            current.copy(
                isSubscriber = newValue
            )
        }
    }
    
    private fun showPremiumSheet() {
        _uiState.update { current ->
            current.copy(
                isSheetShowed = true
            )
        }
    }
    
    private fun buyProduct() {
        viewModelScope.launch {
            val productId =
                if (uiState.value.yearlyPlanSelected) {
                    Constant.PRODUCT_ID_YEARLY_SUB
                }
                else if (uiState.value.monthlyPlanSelected) {
                    Constant.PRODUCT_ID_MONTHLY_SUG
                }
                else {
                    Constant.PRODUCT_ID_LIFETIME
                }
            _event.send(
                ViewModelEvent.OnBuyClick(
                    productId = productId
                )
            )
            _uiState.update { current ->
                delay(300) // Show close animation
                current.copy(
                    isSheetShowed = false
                )
            }
        }
    }
    
}