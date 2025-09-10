package de.xkript.blackcover.presentation.main

import de.xkript.blackcover.core.BlackCoverApp
import de.xkript.blackcover.core.util.Constant
import de.xkript.blackcover.core.util.dataStores.DataStoreBlackCover
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private class FakeApp : BlackCoverApp()

    private lateinit var app: BlackCoverApp
    private lateinit var dataStore: DataStoreBlackCover

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        app = FakeApp()
        dataStore = mock()

        whenever(
            dataStore.getBoolean(Constant.DS_BLACK_COVER_IS_LIFETIME_USER, false)
        ).thenReturn(false)
    }

    @Test
    fun `initTools sets isSubscriber true when lifetime user`() = runTest(dispatcher) {
        whenever(
            dataStore.getBoolean(Constant.DS_BLACK_COVER_IS_LIFETIME_USER, false)
        ).thenReturn(true)

        val vm = MainViewModel(app, dataStore)

        advanceUntilIdle()

        assertTrue(vm.uiState.value.isSubscriber)
    }

    @Test
    fun `OnTimeFontItemClick updates selectedFontIndex`() = runTest(dispatcher) {
        val vm = MainViewModel(app, dataStore)

        vm.onEvent(MainEvent.OnTimeFontItemClick(index = 5))
        advanceUntilIdle()

        assertEquals(5, vm.uiState.value.selectedFontIndex)
    }

    @Test
    fun `OnAlwaysOnDisplay toggles skipUnlock flag`() = runTest(dispatcher) {
        val vm = MainViewModel(app, dataStore)
        val before = vm.uiState.value.isSkipUnlockScreenChecked

        vm.onEvent(MainEvent.OnAlwaysOnDisplay)
        advanceUntilIdle()

        assertEquals(!before, vm.uiState.value.isSkipUnlockScreenChecked)
    }

    @Test
    fun `OnFabItemClick shows premium sheet for non-subscriber when index >= 3`() = runTest(dispatcher) {
        val vm = MainViewModel(app, dataStore)
        vm.changeIsSubscriber(false)

        vm.onEvent(MainEvent.OnFabItemClick(index = 3))
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isSheetShowed)
    }

    @Test
    fun `OnFabItemClick selects icon for non-subscriber when index less than 3`() = runTest(dispatcher) {
        val vm = MainViewModel(app, dataStore)
        vm.changeIsSubscriber(false)

        vm.onEvent(MainEvent.OnFabItemClick(index = 2))
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.selectedFabIndex)
        assertFalse(vm.uiState.value.isSheetShowed)
    }

    @Test
    fun `OnTabCounterItemClick sets value for allowed indices`() = runTest(dispatcher) {
        val vm = MainViewModel(app, dataStore)
        vm.changeIsSubscriber(false)

        vm.onEvent(MainEvent.OnTabCounterItemClick(index = 2))
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.selectedTapCounterIndex)
    }

    @Test
    fun `OnTabCounterItemClick shows premium sheet for non-subscriber when index 3 or 4`() = runTest(dispatcher) {
        val vm = MainViewModel(app, dataStore)
        vm.changeIsSubscriber(false)

        vm.onEvent(MainEvent.OnTabCounterItemClick(index = 3))
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isSheetShowed)
    }

    @Test
    fun `changeIsSubscriber updates state`() = runTest(dispatcher) {
        val vm = MainViewModel(app, dataStore)
        vm.changeIsSubscriber(true)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isSubscriber)
    }
}
