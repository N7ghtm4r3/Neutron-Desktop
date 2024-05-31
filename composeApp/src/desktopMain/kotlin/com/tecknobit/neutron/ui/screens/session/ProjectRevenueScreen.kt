package com.tecknobit.neutron.ui.screens.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.ui.*
import com.tecknobit.neutron.ui.screens.Screen
import com.tecknobit.neutron.ui.screens.navigation.Splashscreen.Companion.user
import com.tecknobit.neutron.ui.screens.session.Home.Companion.revenues
import com.tecknobit.neutron.ui.sections.addsections.AddTicketRevenueSection
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.delete_project
import neutron.composeapp.generated.resources.delete_project_warn_text
import neutron.composeapp.generated.resources.total_revenues
import org.jetbrains.compose.resources.stringResource

class ProjectRevenueScreen(
    val projectRevenueId: String?
): Screen() {

    private lateinit var projectRevenue: MutableState<ProjectRevenue>

    private lateinit var showDeleteProject: MutableState<Boolean>

    @Composable
    override fun ShowScreen() {
        if(projectRevenueId != null) {
            val currentProjectRevenue = revenues.getProjectRevenue(projectRevenueId)
            val addTicket = remember { mutableStateOf(false) }
            if(currentProjectRevenue != null) {
                projectRevenue = remember { mutableStateOf(currentProjectRevenue) }
                showDeleteProject = remember { mutableStateOf(false) }
                Scaffold (
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
                    AddTicketRevenueSection(
                        show = addTicket,
                        projectRevenue = projectRevenue.value
                    ).AddRevenue()
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
                                            onClick = { showDeleteProject.value = true }
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
                                            user.currency.symbol
                                        ),
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        },
                        body = {
                            DisplayTickets(
                                projectRevenue = projectRevenue
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
        NeutronAlertDialog(
            icon = Icons.Default.Delete,
            show = showDeleteProject,
            title = Res.string.delete_project,
            text = Res.string.delete_project_warn_text,
            confirmAction = {
                // TODO: MAKE THE REQUEST THEN
                showDeleteProject.value = false
                navigator.goBack()
            }
        )
    }

}