@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.neutron.screens.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.screens.Screen
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.screens.session.Home.Companion.revenues
import com.tecknobit.neutron.sections.addsections.AddTicketRevenueSection
import com.tecknobit.neutron.ui.*
import com.tecknobit.neutron.viewmodels.ProjectRevenueActivityViewModel
import com.tecknobit.neutron.viewmodels.addactivities.AddTicketViewModel
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.delete_project
import neutron.composeapp.generated.resources.delete_project_warn_text
import neutron.composeapp.generated.resources.total_revenues
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

class ProjectRevenueScreen(
    val projectRevenueId: String?
): Screen() {

    private lateinit var projectRevenue: State<ProjectRevenue>

    private lateinit var viewModel: ProjectRevenueActivityViewModel

    private lateinit var addTicket: MutableState<Boolean>

    @Composable
    override fun ShowScreen() {
        if(projectRevenueId != null) {
            val currentProjectRevenue = revenues.value!!.getProjectRevenue(projectRevenueId)
            addTicket = remember { mutableStateOf(false) }
            if(currentProjectRevenue != null) {
                viewModel = ProjectRevenueActivityViewModel(
                    snackbarHostState = snackbarHostState,
                    initialProjectRevenue = currentProjectRevenue
                )
                viewModel.setActiveContext(this::class.java)
                viewModel.showDeleteProject = remember { mutableStateOf(false) }
                viewModel.refreshProjectRevenue()
                projectRevenue = viewModel.projectRevenue.collectAsState()
                Scaffold (
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { addTicket.value = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                ) {
                    CreateTicket()
                    DisplayContent(
                        header = {
                            Column {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { navigator.goBack() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                    Column (
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.showDeleteProject.value = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                        DeleteProjectRevenue()
                                    }
                                }
                                Column (
                                    modifier = Modifier
                                        .padding(
                                            start = 10.dp
                                        )
                                ) {
                                    Text(
                                        text = projectRevenue.value.title,
                                        fontSize = 45.sp
                                    )
                                    Text(
                                        text = stringResource(
                                            Res.string.total_revenues,
                                            projectRevenue.value.value,
                                            localUser.currency.symbol
                                        ),
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        },
                        body = {
                            DisplayTickets(
                                projectRevenue = projectRevenue.value,
                                onRight = { ticket ->
                                    viewModel.closeTicket(
                                        ticket = ticket
                                    )
                                },
                                onDelete = { ticket ->
                                    viewModel.deleteTicket(
                                        ticket = ticket
                                    )
                                }
                            )
                        }
                    )
                }
            } else
                ErrorUI()
        } else
            ErrorUI()
    }

    @Composable
    private fun DeleteProjectRevenue() {
        if (viewModel.showDeleteProject.value)
            viewModel.suspendRefresher()
        NeutronAlertDialog(
            icon = Icons.Default.Delete,
            show = viewModel.showDeleteProject,
            title = Res.string.delete_project,
            text = Res.string.delete_project_warn_text,
            confirmAction = {
                viewModel.deleteProjectRevenue {
                    navigator.goBack()
                }
            }
        )
    }

    @Composable
    private fun CreateTicket() {
        val ticketViewModel = AddTicketViewModel(
            snackbarHostState = snackbarHostState
        )
        AddTicketRevenueSection(
            show = addTicket,
            projectRevenue = projectRevenue.value,
            viewModel = ticketViewModel
        ).AddRevenue()
    }

}