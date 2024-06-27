package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.apimanager.trading.TradingTools.roundValue
import com.tecknobit.apimanager.trading.TradingTools.textualizeAssetPercent
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.screens.session.Home
import com.tecknobit.neutroncore.records.User.*
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.Revenue.returnRevenues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.YearMonth

/**
 * The **HomeViewModel** class is the support class used by the [Home] to refresh
 * the [revenues] list of the user and the wallet data
 *
 * @param snackbarHostState: the host to launch the snackbar messages
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronViewModel
 * @see ViewModel
 * @see FetcherManagerWrapper
 */
class HomeViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    /**
     * **monthFormatter** -> the helper used to format the month temporal data
     */
    private val monthFormatter = TimeFormatter.getInstance("MM")

    /**
     * **_revenues** -> the list of the user's current revenues
     */
    private val _revenues = MutableStateFlow<MutableList<Revenue>>(mutableListOf())
    val revenues: StateFlow<MutableList<Revenue>> = _revenues

    /**
     * **_walletBalance** -> the current balance of the user
     */
    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance

    /**
     * **_walletTrend** -> the current wallet trend of the user related to the last month
     */
    private val _walletTrend = MutableStateFlow("0")
    val walletTrend: StateFlow<String> = _walletTrend

    /**
     * Function to restart the current [refreshRoutine] after other requests has been executed,
     * will relaunch the [getRevenuesList] routine
     *
     * No-any params required
     */
    override fun restartRefresher() {
        getRevenuesList()
    }

    /**
     * Function to execute the request to refresh the [_revenues].
     * Will be updated, if necessary, also the currency and the profile pic of the user
     *
     * No-any params required
     */
    fun getRevenuesList() {
        execRefreshingRoutine(
            currentContext = Home::class.java,
            routine = {
                requester.sendRequest(
                    request = {
                        requester.listRevenues()
                    },
                    onSuccess = { helper ->
                        _revenues.value = returnRevenues(helper.getJSONArray(RESPONSE_MESSAGE_KEY))
                        getWalletBalance()
                        getWalletTrend()
                        localUser.currency = NeutronCurrency.valueOf(helper.getString(CURRENCY_KEY))
                        localUser.profilePic = helper.getString(PROFILE_PIC_KEY)
                    },
                    onFailure = { showSnack(it) }
                )
            }
        )
    }

    /**
     * Function to update the wallet balance of the user after that the [_revenues] have been updated
     *
     * No-any params required
     */
    private fun getWalletBalance() {
        var balance = 0.0
        _revenues.value.forEach { revenue ->
            balance += revenue.value
        }
        _walletBalance.value = roundValue(balance, 2)
    }

    /**
     * Function to update the wallet trend of the user after that the [_revenues] have been updated
     *
     * No-any params required
     */
    private fun getWalletTrend() {
        val currentMonth = YearMonth.now()
        val lastMonth = currentMonth.minusMonths(1)
        val currentMonthTrend = getRevenuesPerMonth(
            month = currentMonth
        )
        val lastMonthTrend = getRevenuesPerMonth(
            month = lastMonth
        )
        if (lastMonthTrend == 0.0 && _walletBalance.value > 0)
            _walletTrend.value = textualizeAssetPercent(100.0)
        else
            _walletTrend.value = textualizeAssetPercent(lastMonthTrend, currentMonthTrend, 2)
    }

    /**
     * Function to get the total values of the revenues inserted or created in a specific month
     *
     * @param month: the value of the month target to get the total values of the revenues inserted
     * or created in that month
     *
     * @return total values of the revenues inserted or created in that month as [Double]
     */
    private fun getRevenuesPerMonth(
        month: YearMonth
    ): Double {
        val targetMonth = month.monthValue
        var amount = 0.0
        _revenues.value.forEach { revenue ->
            if (revenue is ProjectRevenue) {
                val initialRevenue = revenue.initialRevenue
                if (targetMonth == getRevenueMonth(initialRevenue.revenueTimestamp))
                    amount += initialRevenue.value
                revenue.tickets.forEach { ticket ->
                    if (targetMonth == getRevenueMonth(ticket.revenueTimestamp) ||
                        targetMonth == getRevenueMonth(ticket.closingTimestamp)
                    ) {
                        amount += ticket.value
                    }
                }
            } else if (targetMonth == getRevenueMonth(revenue.revenueTimestamp))
                amount += revenue.value
        }
        return amount
    }

    /**
     * Function to get the month value
     *
     * @param timestamp: the timestamp from fetch the month value
     *
     * @return the month value as [Int]
     */
    private fun getRevenueMonth(
        timestamp: Long
    ): Int {
        return monthFormatter.formatAsString(timestamp).toInt()
    }

}