package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.neutron.screens.session.Home
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.Revenue.returnRevenues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    private val _revenues = MutableStateFlow<MutableList<Revenue>>(mutableListOf())
    val revenues: StateFlow<MutableList<Revenue>> = _revenues

    override fun restartRefresher() {
        getRevenuesList()
    }

    fun getRevenuesList() {
        if (workInLocal()) {
            // TODO: FETCH FROM LOCAL 
        } else {
            execRefreshingRoutine(
                currentContext = Home::class.java,
                routine = {
                    requester.sendRequest(
                        request = {
                            requester.listRevenues()
                        },
                        onSuccess = { helper ->
                            _revenues.value = returnRevenues(helper.getJSONArray(RESPONSE_MESSAGE_KEY))
                        },
                        onFailure = { showSnack(it) }
                    )
                }
            )
        }
    }

}