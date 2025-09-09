package de.xkript.blackcover.presentation.main

sealed class MainEvent {
    data object OnPreViewClick : MainEvent()
    data class OnTimeFontItemClick(val index: Int) : MainEvent()
    data class OnFabItemClick(val index: Int) : MainEvent()
    data class OnTabCounterItemClick(val index: Int) : MainEvent()
    data class OnAlwaysOnDisplay(val isChecked: Boolean) : MainEvent()
    data class OnHideFloatingButton(val isChecked: Boolean) : MainEvent()
    data object OnStartButton: MainEvent()
    data object OnStopButton: MainEvent()
    data object OnSheetDismiss: MainEvent()
    data object OnYearlyPlanClick: MainEvent()
    data object OnMonthlyPlanClick: MainEvent()
    data object OnLifetimePlanClick: MainEvent()
    data object OnBuyPlanClick: MainEvent()
}