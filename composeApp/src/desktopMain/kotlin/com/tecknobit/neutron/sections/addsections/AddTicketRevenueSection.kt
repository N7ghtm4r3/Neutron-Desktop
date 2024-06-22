@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.neutron.sections.addsections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronTextField
import com.tecknobit.neutron.viewmodels.ProjectRevenueViewModel
import com.tecknobit.neutron.viewmodels.addactivities.AddTicketViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueDescriptionValid
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueTitleValid
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import neutron.composeapp.generated.resources.*
import neutron.composeapp.generated.resources.Res.string
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

class AddTicketRevenueSection(
    show: MutableState<Boolean>,
    startContext: Class<*>,
    mainViewModel: ProjectRevenueViewModel,
    override val viewModel: AddTicketViewModel,
    val projectRevenue: ProjectRevenue
) : AddRevenueSection(
    show = show,
    startContext = startContext,
    mainViewModel = mainViewModel,
    viewModel = viewModel
){

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun InputForm() {
        AnimatedVisibility(
            visible = !showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            viewModel.revenueTitle = remember { mutableStateOf("") }
            viewModel.revenueDescription = remember { mutableStateOf("") }
            viewModel.revenueTitleError = remember { mutableStateOf(false) }
            viewModel.revenueDescription = remember { mutableStateOf("") }
            viewModel.revenueDescriptionError = remember { mutableStateOf(false) }
            var isClosed by remember { mutableStateOf(false) }
            viewModel.currentOpeningDate =
                remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            viewModel.currentClosingDate =
                remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            viewModel.currentOpeningTime =
                remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            viewModel.currentClosingTime =
                remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayDatePickerDialog = remember { mutableStateOf(false) }
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis(),
                selectableDates = if(isClosed) {
                    object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis <= formatter.formatAsTimestamp(
                                viewModel.currentClosingDate.value,
                                dateFormat
                            )
                        }
                    }
                } else
                    DatePickerDefaults.AllDates
            )
            val currentOpeningTime = remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayTimePickerDialog = remember { mutableStateOf(false) }
            val timePickerState = getTimePickerState()
            val displayClosingDatePickerDialog = remember { mutableStateOf(false) }
            val dateClosingState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= formatter.formatAsTimestamp(
                            viewModel.currentOpeningDate.value,
                            dateFormat
                        )
                    }
                }
            )
            val currentClosingTime = remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayClosingTimePickerDialog = remember { mutableStateOf(false) }
            val timeClosingPickerState = getTimePickerState()
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        top = 15.dp,
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 15.dp,
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Switch(
                        checked = isClosed,
                        onCheckedChange = { isClosed = it }
                    )
                    Text(
                        text = stringResource(string.closed)
                    )
                }
                NeutronTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = viewModel.revenueTitle,
                    label = string.title,
                    errorText = string.title_not_valid,
                    isError = viewModel.revenueTitleError,
                    validator = { isRevenueTitleValid(it) }
                )
                NeutronTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = 250.dp
                        ),
                    value = viewModel.revenueDescription,
                    label = string.description,
                    isTextArea = true,
                    errorText = string.description_not_valid,
                    isError = viewModel.revenueDescriptionError,
                    validator = { isRevenueDescriptionValid(it) }
                )
                TimeInfoSection(
                    dateTitle = string.opening_date_title,
                    date = viewModel.currentOpeningDate,
                    displayDatePickerDialog = displayDatePickerDialog,
                    dateState = dateState,
                    timeTitle = string.opening_time,
                    time = currentOpeningTime,
                    displayTimePickerDialog = displayTimePickerDialog,
                    timePickerState = timeClosingPickerState
                )
                if(isClosed) {
                    TimeInfoSection(
                        dateTitle = string.closing_date_title,
                        date = viewModel.currentClosingDate,
                        displayDatePickerDialog = displayClosingDatePickerDialog,
                        dateState = dateClosingState,
                        timeTitle = string.closing_time,
                        time = currentClosingTime,
                        displayTimePickerDialog = displayClosingTimePickerDialog,
                        timePickerState = timePickerState
                    )
                }
                NeutronButton(
                    onClick = {
                        viewModel.addTicket(
                            projectRevenue = projectRevenue,
                            isClosed = isClosed,
                            onSuccess = { navBack() }
                        )
                    },
                    text = string.add_ticket
                )
            }
        }
    }

}