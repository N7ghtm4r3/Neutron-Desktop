package com.tecknobit.neutron.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.equinox.Requester
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.ui.theme.errorContainerDark
import com.tecknobit.neutron.viewmodels.NeutronViewModel.Companion.requester
import com.tecknobit.neutroncore.records.revenues.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.util.*
import java.util.concurrent.TimeUnit

lateinit var PROJECT_LABEL: RevenueLabel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DisplayRevenues(
    snackbarHostState: SnackbarHostState,
    revenues: MutableList<Revenue>,
    navToProject: (Revenue) -> Unit
) {
    if(revenues.isNotEmpty()) {
        val onDelete: (Revenue) -> Unit = { revenue ->
            requester.sendRequest(
                request = {
                    requester.deleteRevenue(
                        revenue = revenue
                    )
                },
                onSuccess = {},
                onFailure = { helper ->
                    CoroutineScope(Dispatchers.IO).launch {
                        snackbarHostState.showSnackbar(helper.getString(Requester.RESPONSE_MESSAGE_KEY))
                    }
                }
            )
        }
        LazyColumn (
            modifier = Modifier
                .fillMaxHeight(),
            contentPadding = PaddingValues(
                bottom = 16.dp
            )
        ) {
            items(
                items = revenues,
                key = { it.id }
            ) { revenue ->
                if(revenue is GeneralRevenue) {
                    SwipeToDeleteContainer(
                        item = revenue,
                        onDelete = onDelete
                    ) {
                        GeneralRevenue(
                            revenue = revenue
                        )
                    }
                } else {
                    SwipeToDeleteContainer(
                        item = revenue,
                        onDelete = onDelete
                    ) {
                        ProjectRevenue(
                            revenue = revenue as ProjectRevenue,
                            navToProject = navToProject
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    } else {
        EmptyListUI(
            icon = Icons.Default.SpeakerNotesOff,
            subText = Res.string.no_revenues_yet
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun GeneralRevenue(
    revenue: Revenue
) {
    var descriptionDisplayed by remember { mutableStateOf(false) }
    val isInitialRevenue = revenue.title == null
    Column {
        ListItem(
            headlineContent = {
                Text(
                    text = if(isInitialRevenue)
                        stringResource(Res.string.initial_revenue)
                    else
                        revenue.title,
                    fontSize = 20.sp
                )
            },
            supportingContent = {
                RevenueInfo(
                    revenue = revenue
                )
            },
            trailingContent = {
                if(!isInitialRevenue) {
                    Column {
                        if (revenue is TicketRevenue) {
                            val coroutine = rememberCoroutineScope()
                            val state = rememberTooltipState()
                            TooltipBox(
                                modifier = Modifier
                                    .clickable {
                                        coroutine.launch {
                                            state.show(MutatePriority.Default)
                                        }
                                    },
                                positionProvider = TooltipDefaults
                                    .rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    Text(
                                        text = if (revenue.isClosed)
                                            "Closed"
                                        else
                                            "Pending"
                                    )
                                },
                                state = state
                            ) {
                                LabelBadge(
                                    label = revenue.currentLabel
                                )
                            }
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                items(
                                    items = (revenue as GeneralRevenue).labels,
                                    key = {
                                        if (it.id != null)
                                            it.id
                                        else
                                            UUID.randomUUID()
                                    }
                                ) { label ->
                                    LabelBadge(
                                        label = label
                                    )
                                }
                            }
                        }
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.End),
                            onClick = { descriptionDisplayed = !descriptionDisplayed }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(40.dp),
                                imageVector = if(descriptionDisplayed)
                                    Icons.Default.KeyboardArrowUp
                                else
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        )
        AnimatedVisibility(
            visible = descriptionDisplayed
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                text = (revenue as GeneralRevenue).description,
                textAlign = TextAlign.Justify
            )
        }
    }
    if(isInitialRevenue)
        HorizontalDivider()
}

@Composable
private fun ProjectRevenue(
    revenue: ProjectRevenue,
    navToProject: (Revenue) -> Unit
) {
    ListItem(
        modifier = Modifier
            .clickable {
                navToProject.invoke(revenue)
            },
        headlineContent = {
            Text(
                text = revenue.title,
                fontSize = 20.sp
            )
        },
        supportingContent = {
            RevenueInfo(
                revenue = revenue
            )
        },
        trailingContent = {
            Column {
                LabelBadge(
                    label = PROJECT_LABEL
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.End),
                    onClick = { navToProject.invoke(revenue) }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp),
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RevenueInfo(
    revenue: Revenue
) {
    val isTicket = revenue is TicketRevenue
    Column {
        Row (
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(Res.string.revenue)
            )
            Text(
                text = "${revenue.value}${localUser.currency.symbol}",
                fontFamily = displayFontFamily
            )
        }
        Row (
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(
                    if(isTicket)
                        Res.string.opening_date
                    else
                        Res.string.date
                )
            )
            Text(
                text = revenue.revenueDate,
                fontFamily = displayFontFamily
            )
        }
        if(isTicket) {
            val ticket = revenue as TicketRevenue
            if(ticket.isClosed) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.closing_date)
                    )
                    Text(
                        text = ticket.closingDate,
                        fontFamily = displayFontFamily
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.duration),
                    )
                    Text(
                        text = TimeUnit.MILLISECONDS.toDays(ticket.duration).toString()
                                + " " + stringResource(Res.string.days),
                        fontFamily = displayFontFamily
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LabelBadge(
    label: RevenueLabel
) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = label.color.backgroundColor()
        ),
        shape = RoundedCornerShape(
            size = 5.dp
        )
    ) {
        Text(
            modifier = Modifier
                .padding(
                    all = 5.dp
                )
                .basicMarquee(),
            text = label.text,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InsertionLabelBadge(
    modifier: Modifier = Modifier,
    labels: SnapshotStateList<RevenueLabel>? = null,
    label: RevenueLabel
) {
    Card (
        modifier = modifier
            .widthIn(
                max = 125.dp
            )
            .height(35.dp),
        colors = CardDefaults.cardColors(
            containerColor = label.color.backgroundColor()
        ),
        shape = RoundedCornerShape(
            size = 5.dp
        )
    ) {
        Row (
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = if (labels == null)
                        10.dp
                    else
                        0.dp
                )
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .basicMarquee(),
                text = label.text,
                maxLines = 1
            )
            if(labels != null) {
                IconButton(
                    onClick = { labels.remove(label) }
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircleOutline,
                        null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DisplayTickets(
    projectRevenue: ProjectRevenue,
    onRight: (TicketRevenue) -> Unit,
    onDelete: (TicketRevenue) -> Unit
) {
    val tickets = projectRevenue.tickets.toMutableStateList()
    if (tickets.isNotEmpty()) {
        LazyColumn {
            item {
                GeneralRevenue(
                    revenue = projectRevenue.initialRevenue
                )
            }
            items(
                key = { ticket -> ticket.id },
                items = tickets
            ) { ticket ->
                SwipeToDeleteContainer(
                    item = ticket,
                    onRight = if (!ticket.isClosed) {
                        {
                            onRight.invoke(ticket)
                        }
                    } else
                        null,
                    onDelete = {
                        onDelete.invoke(ticket)
                    }
                ) {
                    GeneralRevenue(
                        revenue = ticket
                    )
                }
                HorizontalDivider()
            }
        }
    } else {
        GeneralRevenue(
            revenue = projectRevenue.initialRevenue
        )
        EmptyListUI(
            icon = Icons.AutoMirrored.Filled.StickyNote2,
            subText = Res.string.no_tickets_yet
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onRight: ((T) -> Unit)? = null,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    var swipedToRight by remember { mutableStateOf(false) }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                EndToStart -> {
                    isRemoved = true
                    true
                }
                StartToEnd -> {
                    swipedToRight = true
                    true
                }
                else -> false
            }
        }
    )
    if(onRight != null) {
        LaunchedEffect(key1 = swipedToRight) {
            if(swipedToRight) {
                delay(animationDuration.toLong())
                onRight(item)
            }
        }
    }
    LaunchedEffect(key1 = isRemoved) {
        if(isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }
    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = {
                SwipeBackground(swipeDismissState = state)
            },
            content = { content(item) },
            enableDismissFromEndToStart = true,
            enableDismissFromStartToEnd = onRight != null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    swipeDismissState: SwipeToDismissBoxState
) {
    val isEndToStart = swipeDismissState.dismissDirection == EndToStart
    if(isEndToStart || swipeDismissState.dismissDirection == StartToEnd) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isEndToStart)
                        errorContainerDark
                    else
                        TicketRevenue.CLOSED_TICKET_LABEL_COLOR.backgroundColor()
                )
                .padding(16.dp),
            contentAlignment = if(isEndToStart)
                Alignment.CenterEnd
            else
                Alignment.CenterStart
        ) {
            Icon(
                imageVector = if(isEndToStart)
                    Icons.Default.Delete
                else
                    Icons.Default.CheckCircleOutline,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun NeutronAlertDialog(
    show: MutableState<Boolean>,
    icon: ImageVector? = null,
    onDismissAction: () -> Unit = { show.value = false },
    title: StringResource,
    text: StringResource,
    dismissAction: () -> Unit = onDismissAction,
    dismissText: StringResource = Res.string.dismiss,
    confirmAction: () -> Unit,
    confirmText: StringResource = Res.string.confirm
) {
    NeutronAlertDialog(
        show = show,
        icon = icon,
        onDismissAction = onDismissAction,
        title = title,
        text = {
            Text(
                text = stringResource(text),
                textAlign = TextAlign.Justify
            )
        },
        dismissAction = dismissAction,
        dismissText = dismissText,
        confirmAction = confirmAction,
        confirmText = confirmText
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun NeutronAlertDialog(
    show: MutableState<Boolean>,
    icon: ImageVector? = null,
    onDismissAction: () -> Unit = { show.value = false },
    title: StringResource,
    text: @Composable () -> Unit,
    dismissAction: () -> Unit = onDismissAction,
    dismissText: StringResource = Res.string.dismiss,
    confirmAction: () -> Unit,
    confirmText: StringResource = Res.string.confirm
) {
    if(show.value) {
        AlertDialog(
            modifier = Modifier
                .widthIn(
                    max = 400.dp
                ),
            icon = {
                if(icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            },
            onDismissRequest = onDismissAction,
            title = {
                Text(
                    text = stringResource(title)
                )
            },
            text = text,
            dismissButton = {
                TextButton(
                    onClick = dismissAction
                ) {
                    Text(
                        text = stringResource(dismissText)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = confirmAction
                ) {
                    Text(
                        text = stringResource(confirmText)
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun NeutronTextField(
    modifier: Modifier = Modifier,
    width: Dp = 280.dp,
    value: MutableState<String>,
    isTextArea: Boolean = false,
    validator: ((String) -> Boolean)? = null,
    isError: MutableState<Boolean> = remember { mutableStateOf(false) },
    errorText: StringResource? = null,
    onValueChange: (String) -> Unit = {
        if (validator != null)
            isError.value = value.value.isNotEmpty() && !validator.invoke(it)
        value.value = it
    },
    label: StringResource,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        modifier = modifier
            .width(width),
        value = value.value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(label)
            )
        },
        singleLine = !isTextArea,
        maxLines = 25,
        keyboardOptions = keyboardOptions,
        isError = isError.value,
        supportingText = if (isError.value && errorText != null) {
            {
                Text(
                    text = stringResource(errorText)
                )
            }
        } else
            null
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun NeutronOutlinedTextField(
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
    value: MutableState<String>,
    isTextArea: Boolean = false,
    validator: ((String) -> Boolean)? = null,
    isError: MutableState<Boolean> = remember { mutableStateOf(false) },
    errorText: StringResource? = null,
    onValueChange: (String) -> Unit = {
        if (validator != null)
            isError.value = value.value.isNotEmpty() && !validator.invoke(it)
        value.value = it
    },
    label: StringResource,
    trailingIcon:  @Composable (() -> Unit)? = {
        IconButton(
            onClick = { value.value = "" }
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        modifier = modifier
            .width(width),
        value = value.value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(label)
            )
        },
        trailingIcon = trailingIcon,
        singleLine = !isTextArea,
        maxLines = 25,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = isError.value,
        supportingText = if (isError.value && errorText != null) {
            {
                Text(
                    text = stringResource(errorText)
                )
            }
        } else
            null
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun NeutronButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: StringResource,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(
            size = 15.dp
        ),
        colors = colors,
        onClick = onClick
    ) {
        Text(
            text = stringResource(text),
            fontSize = 18.sp
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EmptyListUI(
    icon: ImageVector,
    subText: StringResource
) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(100.dp),
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = stringResource(subText),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ErrorUI() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(100.dp),
            imageVector = Icons.Default.Error,
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = errorContainerDark
            )
        )
        Text(
            text = stringResource(Res.string.an_error_occurred),
            color = errorContainerDark
        )
        TextButton(
            onClick = { navigator.goBack() }
        ) {
            Text(
                text = stringResource(Res.string.go_back)
            )
        }
    }
}