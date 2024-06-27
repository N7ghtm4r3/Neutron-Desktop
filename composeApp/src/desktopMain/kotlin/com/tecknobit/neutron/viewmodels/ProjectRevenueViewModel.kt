package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.neutron.screens.session.ProjectRevenueScreen
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.TicketRevenue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The **ProjectRevenueViewModel** class is the support class used by the [ProjectRevenueScreen]
 * to refresh and work on a project
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 * @param initialProjectRevenue: the initial project value to set
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
class ProjectRevenueViewModel(
    snackbarHostState: SnackbarHostState,
    initialProjectRevenue: ProjectRevenue
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **_projectRevenue** -> the current project revenue displayed
     */
    private val _projectRevenue = MutableStateFlow(
        value = initialProjectRevenue
    )
    val projectRevenue: StateFlow<ProjectRevenue> = _projectRevenue

    /**
     * **showDeleteProject** -> whether show the dialog to warn about the project deletion
     */
    lateinit var showDeleteProject: MutableState<Boolean>

    /**
     * Function to restart the current [refreshRoutine] after other requests has been executed,
     * will relaunch the [refreshProjectRevenue] routine
     *
     * No-any params required
     */
    override fun restartRefresher() {
        refreshProjectRevenue()
    }

    /**
     * Function to execute the refreshing routine to update the [_projectRevenue]
     *
     * No-any params required
     */
    fun refreshProjectRevenue() {
        execRefreshingRoutine(
            currentContext = ProjectRevenueScreen::class.java,
            routine = {
                requester.sendRequest(
                    request = {
                        requester.getProjectRevenue(
                            revenue = _projectRevenue.value
                        )
                    },
                    onSuccess = { helper ->
                        _projectRevenue.value = ProjectRevenue(helper.getJSONObject(RESPONSE_MESSAGE_KEY))
                    },
                    onFailure = { showSnack(it) }
                )
            }
        )
    }

    /**
     * Function to execute the request to close a [TicketRevenue] of the project
     *
     * @param ticket: the ticket to close
     */
    fun closeTicket(
        ticket: TicketRevenue
    ) {
        suspendRefresher()
        requester.sendRequest(
            request = {
                requester.closeProjectRevenueTicket(
                    projectRevenue = _projectRevenue.value,
                    ticket = ticket
                )
            },
            onSuccess = {
                restartRefresher()
            },
            onFailure = {
                restartRefresher()
                showSnack(it)
            }
        )
    }

    /**
     * Function to execute the request to delete a [TicketRevenue] of the project
     *
     * @param ticket: the ticket to delete
     */
    fun deleteTicket(
        ticket: TicketRevenue
    ) {
        requester.sendRequest(
            request = {
                requester.deleteProjectRevenueTicket(
                    projectRevenue = _projectRevenue.value,
                    ticket = ticket
                )
            },
            onSuccess = {},
            onFailure = { showSnack(it) }
        )
    }

    /**
     * Function to execute the request to delete the [_projectRevenue] displayed
     *
     * @param onSuccess: the action to execute if the request has been successful
     */
    fun deleteProjectRevenue(
        onSuccess: () -> Unit
    ) {
        requester.sendRequest(
            request = {
                requester.deleteRevenue(
                    revenue = _projectRevenue.value
                )
            },
            onSuccess = {
                onSuccess.invoke()
            },
            onFailure = {
                showDeleteProject.value = false
                restartRefresher()
                showSnack(it)
            }
        )
    }

}