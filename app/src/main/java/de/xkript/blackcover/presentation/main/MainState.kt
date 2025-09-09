package de.xkript.blackcover.presentation.main

data class MainState(
    val isLoading: Boolean = false,
    val time: String = "",
    val date: String = "",
    val isSubscriber: Boolean = false,
    val isSheetShowed: Boolean = false,
    val selectedFontIndex: Int = 0,
    val selectedFabIndex: Int = 0,
    val selectedTapCounterIndex: Int = 0,
    val isSkipUnlockScreenChecked: Boolean = false,
    val isHideFloatingButtonChecked: Boolean = false,
    val hasStarted: Boolean = false,
    val yearlyPlanSelected: Boolean = true,
    val monthlyPlanSelected: Boolean = false,
    val lifetimePlanSelected: Boolean = false,
)