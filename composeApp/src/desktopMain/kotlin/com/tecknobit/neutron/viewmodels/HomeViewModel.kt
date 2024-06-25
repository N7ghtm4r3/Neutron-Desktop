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

class HomeViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    private val monthFormatter = TimeFormatter.getInstance("MM")

    private val _revenues = MutableStateFlow<MutableList<Revenue>>(mutableListOf())
    val revenues: StateFlow<MutableList<Revenue>> = _revenues

    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance

    private val _walletTrend = MutableStateFlow("0")
    val walletTrend: StateFlow<String> = _walletTrend

    override fun restartRefresher() {
        getRevenuesList()
    }

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

    private fun getWalletBalance() {
        var balance = 0.0
        _revenues.value.forEach { revenue ->
            balance += revenue.value
        }
        _walletBalance.value = roundValue(balance, 2)
    }

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

    private fun getRevenueMonth(
        timestamp: Long
    ): Int {
        return monthFormatter.formatAsString(timestamp).toInt()
    }

}