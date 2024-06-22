@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.neutron.sections.addsections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.tecknobit.neutron.ui.InsertionLabelBadge
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronTextField
import com.tecknobit.neutron.ui.displayFontFamily
import com.tecknobit.neutron.viewmodels.HomeViewModel
import com.tecknobit.neutron.viewmodels.addactivities.AddRevenuesViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueDescriptionValid
import com.tecknobit.neutroncore.helpers.InputValidator.isRevenueTitleValid
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

class AddRevenuesSection(
    show: MutableState<Boolean>,
    startContext: Class<*>,
    mainViewModel: HomeViewModel,
    override val viewModel: AddRevenuesViewModel
): AddRevenueSection(
    show = show,
    startContext = startContext,
    mainViewModel = mainViewModel,
    viewModel = viewModel,
) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun InputForm() {
        AnimatedVisibility(
            visible = !showKeyboard.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            viewModel.revenueTitleError = remember { mutableStateOf(false) }
            viewModel.revenueDescriptionError = remember { mutableStateOf(false) }
            viewModel.currentDate = remember { mutableStateOf(formatter.formatAsNowString(dateFormat)) }
            viewModel.currentTime = remember { mutableStateOf(formatter.formatAsNowString(timeFormat)) }
            val displayDatePickerDialog = remember { mutableStateOf(false) }
            val dateState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
            val displayTimePickerDialog = remember { mutableStateOf(false) }
            val timePickerState = getTimePickerState()
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        top = 25.dp,
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 25.dp,
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                var isProjectRevenue by remember { mutableStateOf(false) }
                SingleChoiceSegmentedButtonRow (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { showKeyboard.value = !showKeyboard.value}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    SegmentedButton(
                        selected = !isProjectRevenue,
                        onClick = { isProjectRevenue = !isProjectRevenue },
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.general_revenue)
                        )
                    }
                    SegmentedButton(
                        selected = isProjectRevenue,
                        onClick = { isProjectRevenue = !isProjectRevenue },
                        shape = RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.project)
                        )
                    }
                }
                NeutronTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = viewModel.revenueTitle,
                    label = Res.string.title,
                    errorText = Res.string.title_not_valid,
                    isError = viewModel.revenueTitleError,
                    validator = { isRevenueTitleValid(it) }
                )
                if(!isProjectRevenue) {
                    NeutronTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = 250.dp
                            ),
                        value = viewModel.revenueDescription,
                        label = Res.string.description,
                        isTextArea = true,
                        errorText = Res.string.description_not_valid,
                        isError = viewModel.revenueDescriptionError,
                        validator = { isRevenueDescriptionValid(it) }
                    )
                    viewModel.labels = remember { mutableStateListOf() }
                    Labels(
                        labels = viewModel.labels
                    )
                }
                TimeInfoSection(
                    dateTitle = Res.string.insertion_date,
                    date = viewModel.currentDate,
                    displayDatePickerDialog = displayDatePickerDialog,
                    dateState = dateState,
                    timeTitle = Res.string.insertion_time,
                    time = viewModel.currentTime,
                    displayTimePickerDialog = displayTimePickerDialog,
                    timePickerState = timePickerState
                )
                NeutronButton(
                    onClick = {
                        viewModel.createRevenue(
                            isProject = isProjectRevenue,
                            onSuccess = { navBack() }
                        )
                    },
                    text = if(isProjectRevenue)
                        Res.string.add_project
                    else
                        Res.string.add_revenue
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
    @Composable
    private fun Labels(
        labels: SnapshotStateList<RevenueLabel>
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.labels),
                fontSize = 18.sp
            )
            LazyRow (
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 5.dp,
                    top = 5.dp,
                    bottom = 5.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if(labels.size < 5) {
                    stickyHeader {
                        val showAddLabel = remember { mutableStateOf(false) }
                        FloatingActionButton(
                            modifier = Modifier
                                .size(35.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(
                                size = 10.dp
                            ),
                            onClick = { showAddLabel.value = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                        AddLabel(
                            showAddLabel = showAddLabel,
                            labels = labels
                        )
                    }
                }
                items(
                    items = labels,
                    key = { it.text }
                ) { label ->
                    InsertionLabelBadge(
                        labels = labels,
                        label = label
                    )
                }
            }
            HorizontalDivider()
        }
    }

    @Composable
    private fun AddLabel(
        showAddLabel: MutableState<Boolean>,
        labels: SnapshotStateList<RevenueLabel>
    ) {
        if(showAddLabel.value) {
            Dialog(
                onDismissRequest = { showAddLabel.value = false },
            ) {
                Card (
                    modifier = Modifier
                        .width(400.dp),
                    shape = RoundedCornerShape(
                        size = 15.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 16.dp
                            ),
                        text = stringResource(Res.string.add_label),
                        fontFamily = displayFontFamily,
                        fontSize = 20.sp
                    )
                    Column (
                        modifier = Modifier
                            .padding(
                                all = 16.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val labelText = remember { mutableStateOf("") }
                        var hexColor by remember { mutableStateOf("#FFFFFF") }
                        InsertionLabelBadge(
                            modifier = Modifier
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(
                                        size = 5.dp
                                    )
                                ),
                            label = RevenueLabel(
                                labelText.value.ifEmpty { stringResource(Res.string.label_text) },
                                hexColor
                            )
                        )
                        NeutronTextField(
                            modifier = Modifier,
                            value = labelText,
                            label = Res.string.label_text
                        )
                        ClassicColorPicker(
                            modifier = Modifier
                                .padding(
                                    top = 16.dp
                                )
                                .size(250.dp),
                            onColorChanged = { color: HsvColor ->
                                hexColor = "#" + Integer.toHexString(color.toColor().toArgb()).substring(2)
                            },
                            showAlphaBar = false,
                            color = HsvColor.from(Color.White)
                        )
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showAddLabel.value = false }
                            ) {
                                Text(
                                    text = stringResource(Res.string.dismiss)
                                )
                            }
                            TextButton(
                                onClick = {
                                    labels.add(
                                        RevenueLabel(
                                            labelText.value,
                                            hexColor
                                        )
                                    )
                                    showAddLabel.value = false
                                }
                            ) {
                                Text(
                                    text = stringResource(Res.string.confirm)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}