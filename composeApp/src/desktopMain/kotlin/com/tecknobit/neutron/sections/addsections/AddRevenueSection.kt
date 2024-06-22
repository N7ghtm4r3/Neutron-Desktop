@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.neutron.sections.addsections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.displayFontFamily
import com.tecknobit.neutron.viewmodels.NeutronViewModel
import com.tecknobit.neutron.viewmodels.addactivities.AddRevenueViewModel
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import kotlin.collections.ArrayDeque

abstract class AddRevenueSection (
    val show: MutableState<Boolean>,
    val startContext: Class<*>,
    val mainViewModel: NeutronViewModel,
    open val viewModel: AddRevenueViewModel
) {

    private val calendar: Calendar = Calendar.getInstance()

    protected val formatter: TimeFormatter = TimeFormatter.getInstance()

    protected val dateFormat = "dd/MM/yyyy"

    protected val timeFormat = "HH:mm:ss"

    protected lateinit var showKeyboard: MutableState<Boolean>

    private val digits : ArrayDeque<Int> = ArrayDeque()

    protected val snackbarHostState by lazy {
        SnackbarHostState()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddRevenue() {
        viewModel.revenueValue = remember { mutableStateOf("0") }
        viewModel.revenueTitle = remember { mutableStateOf("") }
        viewModel.revenueDescription = remember { mutableStateOf("") }
        showKeyboard = remember { mutableStateOf(true) }
        if(show.value) {
            viewModel.setActiveContext(this::class.java)
            mainViewModel.suspendRefresher()
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),
                onDismissRequest = { navBack() }
            ) {
                Box {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(MaterialTheme.colorScheme.primary),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    start = 32.dp,
                                    end = 32.dp
                                ),
                            text = "${viewModel.revenueValue.value}${localUser.currency.symbol}",
                            color = Color.White,
                            fontFamily = displayFontFamily,
                            fontSize = 50.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Card (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = 200.dp
                            ),
                        shape = RoundedCornerShape(
                            topStart = 50.dp,
                            topEnd = 50.dp
                        )
                    ) {
                        Keyboard()
                        InputForm()
                    }
                }
            }
        } else {
            mainViewModel.setActiveContext(startContext)
            mainViewModel.restartRefresher()
        }
    }

    @Composable
    protected fun Keyboard() {
        AnimatedVisibility(
            visible = showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                LazyColumn {
                    repeat(3) { j ->
                        item {
                            Row (
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                repeat(3) { i ->
                                    NumberKeyboardButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        number = (j * 3) + i + 1
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Row (
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column (
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                ActionButton(
                                    action = {
                                        if(digits.isNotEmpty()) {
                                            if (viewModel.revenueValue.value.last() == '.')
                                                viewModel.revenueValue.value =
                                                    viewModel.revenueValue.value.removeSuffix(".")
                                            else {
                                                val digit = digits.removeLast()
                                                viewModel.revenueValue.value =
                                                    if (viewModel.revenueValue.value.contains("."))
                                                        viewModel.revenueValue.value.removeSuffix(digit.toString())
                                                else
                                                        ((viewModel.revenueValue.value.toInt() - digit) / 10).toString()
                                            }
                                        } else if (viewModel.revenueValue.value.last() == '.')
                                            viewModel.revenueValue.value =
                                                viewModel.revenueValue.value.removeSuffix(".")
                                    },
                                    icon = Icons.AutoMirrored.Filled.Backspace
                                )
                            }
                            NumberKeyboardButton(
                                modifier = Modifier
                                    .weight(1f),
                                number = 0
                            )
                            KeyboardButton(
                                modifier = Modifier
                                    .weight(1f),
                                onClick = {
                                    if (!viewModel.revenueValue.value.contains("."))
                                        viewModel.revenueValue.value += "."
                                },
                                text = ".",
                                fontSize = 50.sp
                            )
                        }
                    }
                    item {
                        NeutronButton(
                            modifier = Modifier
                                .padding(
                                    top = 25.dp,
                                    start = 32.dp,
                                    end = 32.dp
                                ),
                            onClick = {
                                if (viewModel.revenueValue.value != "0")
                                    showKeyboard.value = !showKeyboard.value
                            },
                            text = Res.string.next
                        )
                    }
                }
            }
        }
    }

    @Wrapper
    @Composable
    protected fun NumberKeyboardButton(
        modifier: Modifier,
        number: Int
    ) {
        KeyboardButton(
            modifier = modifier,
            onClick = {
                viewModel.revenueValue.value = if (viewModel.revenueValue.value.contains(".")) {
                    if (viewModel.revenueValue.value.split(".")[1].length < 2)
                        viewModel.revenueValue.value + number
                    else
                        viewModel.revenueValue.value
                } else
                    (viewModel.revenueValue.value.toInt() * 10 + number).toString()
                digits.add(number)
            },
            text = number.toString()
        )
    }

    @Composable
    protected fun KeyboardButton(
        modifier: Modifier,
        onClick: () -> Unit,
        text: String,
        fontSize: TextUnit = 45.sp
    ) {
        TextButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Text(
                text = text,
                fontSize = fontSize
            )
        }
    }

    @Composable
    protected fun ActionButton(
        modifier: Modifier = Modifier,
        action: () -> Unit,
        icon: ImageVector
    ) {
        Button(
            modifier = modifier
                .size(75.dp)
                .clip(CircleShape),
            onClick = action,
        ) {
            Icon(
                modifier = Modifier
                    .size(25.dp),
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    }

    @Composable
    protected abstract fun InputForm()

    @Composable
    protected fun TimeInfo(
        info: StringResource,
        infoValue: String,
        onClick: () -> Unit
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(info),
                fontSize = 18.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = infoValue,
                    fontSize = 20.sp,
                    fontFamily = displayFontFamily
                )
                Button(
                    modifier = Modifier
                        .height(25.dp),
                    onClick = onClick,
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        end = 10.dp,
                        top = 0.dp,
                        bottom = 0.dp
                    ),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.edit),
                        fontSize = 12.sp
                    )
                }
            }
            HorizontalDivider()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun TimeInfoSection(
        dateTitle: StringResource,
        date: MutableState<String>,
        displayDatePickerDialog: MutableState<Boolean>,
        dateState: DatePickerState,
        timeTitle: StringResource,
        time: MutableState<String>,
        displayTimePickerDialog: MutableState<Boolean>,
        timePickerState: TimePickerState
    ) {
        TimeInfo(
            info = dateTitle,
            infoValue = date.value,
            onClick = { displayDatePickerDialog.value = true }
        )
        if(displayDatePickerDialog.value) {
            DatePickerDialog(
                onDismissRequest = { displayDatePickerDialog.value = false },
                dismissButton = {
                    TextButton(
                        onClick = { displayDatePickerDialog.value = false }
                    ) {
                        Text(
                            text = stringResource(Res.string.dismiss)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            date.value = formatter.formatAsString(
                                dateState.selectedDateMillis!!,
                                dateFormat
                            )
                            displayDatePickerDialog.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.confirm)
                        )
                    }
                }
            ) {
                DatePicker(
                    state = dateState
                )
            }
        }
        TimeInfo(
            info = timeTitle,
            infoValue = time.value,
            onClick = { displayTimePickerDialog.value = true }
        )
        TimePickerDialog(
            showTimePicker = displayTimePickerDialog,
            timeState = timePickerState,
            confirmAction = {
                time.value = "${timePickerState.hour}:${timePickerState.minute}:00"
                displayTimePickerDialog.value = false
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun getTimePickerState(): TimePickerState {
        return rememberTimePickerState(
            initialHour = calendar.get(HOUR_OF_DAY),
            initialMinute = calendar.get(MINUTE)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun TimePickerDialog(
        showTimePicker: MutableState<Boolean>,
        timeState: TimePickerState,
        confirmAction: () -> Unit
    ) {
        if (showTimePicker.value) {
            AlertDialog(
                onDismissRequest = { showTimePicker.value = false },
                title = {},
                text = { TimePicker(state = timeState) },
                dismissButton = {
                    TextButton(
                        onClick = { showTimePicker.value = false }
                    ) {
                        Text(
                            text = stringResource(Res.string.dismiss)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = confirmAction
                    ) {
                        Text(
                            text = stringResource(Res.string.confirm)
                        )
                    }
                }
            )
        }
    }

    protected fun navBack() {
        viewModel.revenueValue.value = "0"
        showKeyboard.value = true
        viewModel.revenueTitle.value = ""
        viewModel.revenueDescription.value = ""
        digits.clear()
        show.value = false
    }

}