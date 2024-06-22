package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.FetcherManager
import com.tecknobit.equinox.FetcherManager.Companion.activeContext
import com.tecknobit.equinox.FetcherManager.FetcherManagerWrapper
import com.tecknobit.equinox.Requester
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutroncore.helpers.NeutronRequester
import com.tecknobit.neutroncore.records.User.UserStorage.Local
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class NeutronViewModel(
    protected val snackbarHostState: SnackbarHostState
) : ViewModel(), FetcherManagerWrapper {

    companion object {

        lateinit var requester: NeutronRequester

    }

    protected val refreshRoutine = CoroutineScope(Dispatchers.Default)

    private val fetcherManager = FetcherManager(refreshRoutine)

    override fun canRefresherStart(): Boolean {
        return fetcherManager.canStart()
    }

    override fun continueToFetch(
        currentContext: Class<*>
    ): Boolean {
        return fetcherManager.continueToFetch(currentContext)
    }

    override fun execRefreshingRoutine(
        currentContext: Class<*>,
        routine: () -> Unit,
        repeatRoutine: Boolean,
        refreshDelay: Long
    ) {
        fetcherManager.execute(
            currentContext = currentContext,
            routine = routine,
            repeatRoutine = repeatRoutine,
            refreshDelay = refreshDelay
        )
    }

    override fun restartRefresher() {
        fetcherManager.restart()
    }

    override fun suspendRefresher() {
        fetcherManager.suspend()
    }

    protected fun showSnack(
        helper: JsonHelper
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            snackbarHostState.showSnackbar(helper.getString(Requester.RESPONSE_MESSAGE_KEY))
        }
    }

    protected fun workInLocal(): Boolean {
        return localUser.storage == Local
    }

    fun setActiveContext(
        context: Class<*>
    ) {
        activeContext = context
    }

}