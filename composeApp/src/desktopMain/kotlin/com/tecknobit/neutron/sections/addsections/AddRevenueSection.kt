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
import com.tecknobit.apimanager.annotations.Structure
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

/**
 * The **AddRevenueSection** class is the section where the user can create and insert a new revenue
 *
 * @param show: whether to show this section
 * @param startContext: the context from this section has been invoked
 * @param mainViewModel: the view model of the class which invoked this section
 * @param viewModel: the viewmodel used to manage the creation of the revenues
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Structure
abstract class AddRevenueSection (
    val show: MutableState<Boolean>,
    val startContext: Class<*>,
    val mainViewModel: NeutronViewModel,
    open val viewModel: AddRevenueViewModel
) {

    /**
     * *calendar* -> the helper used to validate the date values inserted
     */
    private val calendar: Calendar = Calendar.getInstance()

    /**
     * *formatter* -> the helper used to format the time values
     */
    protected val formatter: TimeFormatter = TimeFormatter.getInstance()

    /**
     * *dateFormat* -> the pattern to use to format the date
     */
    protected val dateFormat = "dd/MM/yyyy"

    /**
     * *timeFormat* -> the pattern to use to format the time
     */
    protected val timeFormat = "HH:mm:ss"

    /**
     * *showKeyboard* -> whether display the keyboard
     */
    protected lateinit var showKeyboard: MutableState<Boolean>

    /**
     * *digits* -> the queue of the current decimal digits inserted in the revenue value
     */
    private val digits : ArrayDeque<Int> = ArrayDeque()

    /**
     * *snackbarHostState* -> the host to launch the snackbar messages
     */
    protected val snackbarHostState by lazy {
        SnackbarHostState()
    }

    /**
     * Function to display the section where the user can insert the revenue data
     *
     * No-any params required
     */
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

    /**
     * Function to display a custom digits keyboard on screen
     *
     * No-any params required
     */
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

    /**
     * Function to create a button for the custom [Keyboard] to insert a number value
     *
     * @param modifier: the modifier to apply to the button
     * @param number: the value of the number to apply to the button
     */
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

    /**
     * Function to create a button for the custom [Keyboard] to execute any action
     *
     * @param modifier: the modifier to apply to the button
     * @param onClick: the action to execute when the button has been clicked
     * @param text: the text of the button
     * @param fontSize: the font size of the [text]
     */
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

    /**
     * Function to create a button for the custom [Keyboard] to execute an action
     *
     * @param modifier: the modifier to apply to the button
     * @param action: the action to execute when the button has been clicked
     * @param icon: the icon of the button
     */
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

    /**
     * Function to display the form where the user can insert the details of the revenue to add,
     * so will be different if the revenue is a [GeneralRevenue] or it will be a [ProjectRevenue]
     *
     * No-any params required
     */
    @Composable
    protected abstract fun InputForm()

    /**
     * Function to display a temporal value
     *
     * @param info: the info displayed
     * @param infoValue: value of the time info displayed
     * @param onClick: the action to execute when the button has been clicked
     */
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

    /**
     * Function to display the section of a temporal value
     *
     * @param dateTitle: the title for the date section
     * @param date: the date value
     * @param displayDatePickerDialog: whether display the [DatePickerDialog]
     * @param dateState: the state attached to the [displayDatePickerDialog]
     * @param timeTitle: the title for the time section
     * @param time: the time value
     * @param displayTimePickerDialog: whether display the [TimePickerDialog]
     * @param timePickerState: the state attached to the [displayTimePickerDialog]
     */
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

    /**
     * Function to get a [TimePickerState] to use
     *
     * No-any params required
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected fun getTimePickerState(): TimePickerState {
        return rememberTimePickerState(
            initialHour = calendar.get(HOUR_OF_DAY),
            initialMinute = calendar.get(MINUTE)
        )
    }

    /**
     * Function to display the dialog to insert the time value
     *
     * @param showTimePicker: whether display the [TimePickerDialog]
     * @param timeState: the state attached to the [TimePickerDialog]
     * @param confirmAction: the action to execute when the user confirmed
     *
     */
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

    /**
     * Function to execute the back navigation from the current activity to the previous activity
     *
     * No-any params required
     */
    protected fun navBack() {
        viewModel.revenueValue.value = "0"
        showKeyboard.value = true
        viewModel.revenueTitle.value = ""
        viewModel.revenueDescription.value = ""
        digits.clear()
        show.value = false
    }

}