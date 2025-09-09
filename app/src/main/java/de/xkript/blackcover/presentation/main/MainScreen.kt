package de.xkript.blackcover.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.xkript.blackcover.R
import de.xkript.blackcover.core.ui.theme.BlackCoverFont
import de.xkript.blackcover.core.ui.theme.BlackCoverTheme
import de.xkript.blackcover.core.ui.theme.LightYellow
import de.xkript.blackcover.core.ui.theme.backModal
import de.xkript.blackcover.core.ui.theme.black
import de.xkript.blackcover.core.ui.theme.gray
import de.xkript.blackcover.core.ui.theme.white
import de.xkript.blackcover.core.ui.theme.yellow
import kotlinx.coroutines.launch

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreView() {
    BlackCoverTheme {
        MainScreen(
            MainState(
                isLoading = false,
                time = "14:14",
                date = "Sun, Apr21",
                hasStarted = false,
            ),
            onEvent = {}
        )
    }
}

@Composable
fun MainScreen(
    uiState: MainState,
    onEvent: (mainEvent: MainEvent) -> Unit,
) {
    if (uiState.hasStarted) {
        BlackScreen(uiState, onEvent)
    }
    else {
        HomeScreen(uiState, onEvent)
    }
    //        SheetContent(uiState, onEvent, {})
}

// Screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: MainState,
    onEvent: (mainEvent: MainEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.black
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState),
        ) {
            Header()
            PreViewScreen(
                time = uiState.time,
                date = uiState.date,
                selectedFontIndex = uiState.selectedFontIndex,
                onPreViewClick = {
                    onEvent(MainEvent.OnPreViewClick)
                }
            )
            WatchStyle(
                time = uiState.time,
                uiState.selectedFontIndex,
                onItemClick = {
                    onEvent(MainEvent.OnTimeFontItemClick(it))
                }
            )
            ButtonsStyle(
                uiState.isSubscriber,
                uiState.selectedFabIndex
            ) {
                onEvent(MainEvent.OnFabItemClick(it))
            }
            TapToWakeUp(
                uiState.isSubscriber,
                uiState.selectedTapCounterIndex
            ) {
                onEvent(MainEvent.OnTabCounterItemClick(it))
            }
            Option(
                optionName = stringResource(R.string.skip_unlock_screen),
                isChecked = uiState.isSkipUnlockScreenChecked,
                onCheckedChange = {
                    onEvent(MainEvent.OnAlwaysOnDisplay(it))
                }
            )
            //            Option(
            //                optionName = stringResource(R.string.hide_floating_button),
            //                isChecked = uiState.isHideFloatingButtonChecked,
            //                onCheckedChange = {
            //                    onEvent(MainEvent.OnHideFloatingButton(it))
            //                }
            //            )
            Spacer(modifier = Modifier.height(94.dp))
        }
        Button(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            onClick = {
                onEvent(MainEvent.OnStartButton)
            },
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.yellow)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 42.dp)
                    .padding(vertical = 14.dp),
                text = stringResource(R.string.s_t_a_r_t),
                fontSize = 14.sp,
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.black,
            )
        }
        if (uiState.isSheetShowed)
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxSize(),
                onDismissRequest = {
                    onEvent(MainEvent.OnSheetDismiss)
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.backModal
            ) {
                SheetContent(
                    uiState,
                    onEvent,
                    onBuyClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        onEvent(MainEvent.OnBuyPlanClick)
                    }
                )
            }
    }
}

@Composable
private fun SheetContent(
    uiState: MainState,
    onEvent: (mainEvent: MainEvent) -> Unit,
    onBuyClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.backModal)
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.get_black_cover_pro_no_ads_exclusive_features_premium_experience),
            fontFamily = BlackCoverFont.oswald,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.white,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.upgrade_now),
            fontFamily = BlackCoverFont.oswald,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.white,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = if (uiState.yearlyPlanSelected) MaterialTheme.colorScheme.gray else MaterialTheme.colorScheme.backModal,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    onEvent(MainEvent.OnYearlyPlanClick)
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.yearly),
                    fontFamily = BlackCoverFont.oswald,
                    fontWeight = FontWeight.Light,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.white,
                )
                Text(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.yellow,
                            shape = RoundedCornerShape(6.dp),
                        )
                        .padding(vertical = 6.dp, horizontal = 12.dp),
                    text = stringResource(R.string._20_off),
                    fontFamily = BlackCoverFont.oswald,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.white,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.billed_9_99_year_or_0_83_month),
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.white,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = if (uiState.monthlyPlanSelected) MaterialTheme.colorScheme.gray else MaterialTheme.colorScheme.backModal,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    onEvent(MainEvent.OnMonthlyPlanClick)
                }
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.monthly),
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.white,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string._0_99_month),
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.white,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = if (uiState.lifetimePlanSelected) MaterialTheme.colorScheme.gray else MaterialTheme.colorScheme.backModal,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    onEvent(MainEvent.OnLifetimePlanClick)
                }
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.lifetime),
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.white,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string._10_99),
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.white,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                onBuyClick()
            },
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.yellow)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 42.dp)
                    .padding(vertical = 14.dp),
                text = stringResource(R.string.update),
                fontSize = 24.sp,
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.black,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.cancel_your_subscription_at_any_time_in_the_play_store) +
                    stringResource(R.string.purchases_are_automatically_restored) +
                    stringResource(R.string.use_your_subscription_on_multiple_devices_with_the_same_google_account),
            fontFamily = BlackCoverFont.oswald,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.white,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
    
}

@Composable
private fun BlackScreen(
    uiState: MainState,
    onEvent: (mainEvent: MainEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.black
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Header()
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 34.dp),
                    text = uiState.time,
                    fontSize = 42.sp,
                    fontFamily = getFontFamily(uiState.selectedFontIndex),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.white,
                )
                Text(
                    text = uiState.date,
                    fontSize = 18.sp,
                    fontFamily = getFontFamily(uiState.selectedFontIndex),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.white,
                )
            }
        }
        Button(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            onClick = {
                onEvent(MainEvent.OnStopButton)
            },
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.yellow)
        ) {
            
            Text(
                modifier = Modifier
                    .padding(horizontal = 42.dp)
                    .padding(vertical = 14.dp),
                text = stringResource(R.string.s_t_o_p),
                fontSize = 14.sp,
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.black,
            )
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(68.dp),
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "BlackCover",
            fontFamily = BlackCoverFont.oswald,
            fontWeight = FontWeight.Light,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.white,
        )
        
    }
}

@Composable
private fun PreViewScreen(
    time: String,
    date: String,
    selectedFontIndex: Int,
    onPreViewClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 24.dp)
                .width(94.dp)
                .height(204.dp)
                .background(
                    color = MaterialTheme.colorScheme.black,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = LightYellow,
                    shape = RoundedCornerShape(10.dp),
                )
                .clickable {
                    onPreViewClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 34.dp),
                text = time,
                fontSize = 16.sp,
                fontFamily = getFontFamily(selectedFontIndex),
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.white,
            )
            Text(
                text = date,
                fontSize = 8.sp,
                fontFamily = getFontFamily(selectedFontIndex),
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.white,
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            text = stringResource(R.string.unlock),
            fontSize = 10.sp,
            fontFamily = getFontFamily(selectedFontIndex),
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.white,
        )
    }
}

@Composable
private fun WatchStyle(
    time: String,
    selectedTimeFontIndex: Int,
    onItemClick: (index: Int) -> Unit
) {
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Text(
            modifier = Modifier
                .padding(start = 20.dp),
            text = stringResource(R.string.watch_style),
            fontSize = 24.sp,
            fontFamily = BlackCoverFont.oswald,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.white,
        )
        LazyRow(
            modifier = Modifier
                .padding(top = 12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(20.dp))
            }
            items(8) { index ->
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.black,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = if (selectedTimeFontIndex == index) 3.dp else 2.dp,
                            color = if (selectedTimeFontIndex == index) MaterialTheme.colorScheme.yellow else MaterialTheme.colorScheme.gray,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable {
                            onItemClick(index)
                        }
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = time,
                        fontSize = 16.sp,
                        fontFamily = getFontFamily(index),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.gray,
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
private fun ButtonsStyle(
    isSubscriber: Boolean,
    selectedFabIndex: Int,
    onItemClick: (index: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 20.dp),
            text = stringResource(R.string.buttons_style),
            fontSize = 24.sp,
            fontFamily = BlackCoverFont.oswald,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.white,
        )
        LazyRow(
            modifier = Modifier
                .padding(top = 12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(20.dp))
            }
            items(7) { index ->
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.black,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable {
                            onItemClick(index)
                        }
                ) {
                    Image(
                        modifier = Modifier
                            .border(
                                width = if (selectedFabIndex == index) 3.dp else 2.dp,
                                color = if (selectedFabIndex == index) MaterialTheme.colorScheme.yellow else MaterialTheme.colorScheme.gray,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .padding(12.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = selectFabIcon(index)),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(
                            color = if (selectedFabIndex == index) MaterialTheme.colorScheme.yellow else MaterialTheme.colorScheme.gray,
                        )
                    )
                    if (isSubscriber.not() && (index >= 3)) // Show emblem icon only for regular user and only for premium one
                        Image(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 10.dp, y = 16.dp),
                            painter = painterResource(R.drawable.ic_emblem),
                            contentDescription = null,
                        )
                }
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
private fun TapToWakeUp(
    isSubscriber: Boolean,
    selectedTabCounterIndex: Int,
    onItemClick: (index: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 20.dp),
            text = stringResource(R.string.tap_to_wake_up),
            fontSize = 24.sp,
            fontFamily = BlackCoverFont.oswald,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.white,
        )
        LazyRow(
            modifier = Modifier
                .padding(top = 12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(20.dp))
            }
            items(5) { index ->
                Box {
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = if (selectedTabCounterIndex == index) 3.dp else 2.dp,
                                color = if (selectedTabCounterIndex == index) MaterialTheme.colorScheme.yellow else MaterialTheme.colorScheme.gray,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable {
                                onItemClick(index)
                            }
                            .padding(horizontal = 30.dp, vertical = 10.dp),
                        text = "${index + 1}",
                        fontSize = 14.sp,
                        fontFamily = BlackCoverFont.oswald,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.white,
                    )
                    if (isSubscriber.not() && (index == 3 || index == 4)) // Show emblem icon only for regular user and only for 4 and 5 counter
                        Image(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 10.dp, y = 16.dp),
                            painter = painterResource(R.drawable.ic_emblem),
                            contentDescription = null,
                        )
                }
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
private fun Option(
    optionName: String,
    isChecked: Boolean,
    onCheckedChange: (isChecked: Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 38.dp),
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = optionName,
                fontSize = 24.sp,
                fontFamily = BlackCoverFont.oswald,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.white,
            )
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    onCheckedChange(it)
                },
                colors = SwitchDefaults.colors().copy(
                    checkedThumbColor = MaterialTheme.colorScheme.white,
                    checkedTrackColor = MaterialTheme.colorScheme.yellow,
                    checkedBorderColor = MaterialTheme.colorScheme.yellow,
                    uncheckedThumbColor = MaterialTheme.colorScheme.gray,
                    uncheckedTrackColor = MaterialTheme.colorScheme.backModal,
                    uncheckedBorderColor = MaterialTheme.colorScheme.backModal,
                )
            )
        }
    }
}

// Other
private fun getFontFamily(index: Int): FontFamily {
    return when (index) {
        0    -> BlackCoverFont.oswald
        1    -> BlackCoverFont.aBeeZee
        2    -> BlackCoverFont.abrilFatface
        3    -> BlackCoverFont.agdasima
        4    -> BlackCoverFont.aldrich
        5    -> BlackCoverFont.allerta
        6    -> BlackCoverFont.alumniSansPinstripe
        7    -> BlackCoverFont.audiowide
        else -> BlackCoverFont.oswald
    }
}

private fun selectFabIcon(index: Int): Int {
    return when (index) {
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