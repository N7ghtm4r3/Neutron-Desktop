package com.tecknobit.neutron.ui

import androidx.compose.ui.graphics.Color
import com.tecknobit.apimanager.formatters.TimeFormatter
import com.tecknobit.apimanager.trading.TradingTools.textualizeAssetPercent
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.Revenue
import java.time.YearMonth

private val monthFormatter = TimeFormatter.getInstance("MM")

fun String.backgroundColor(): Color {
    return Color(("ff" + removePrefix("#").lowercase()).toLong(16))
}

fun List<Revenue>.getWalletBalance(): Double {
    var balance = 0.0
    forEach { revenue ->
        balance += revenue.value
    }
    return balance
}

fun List<Revenue>.getWalletTrend(): String? {
    val currentMonth = YearMonth.now()
    val lastMonth = currentMonth.minusMonths(1)
    val currentMonthTrend = this.getRevenuesPerMonth(
        month = currentMonth
    )
    val lastMonthTrend = this.getRevenuesPerMonth(
        month = lastMonth
    )
    return textualizeAssetPercent(lastMonthTrend.second, currentMonthTrend.second)
}

private fun List<Revenue>.getRevenuesPerMonth(
    month: YearMonth
) : Pair<Int, Double> {
    val targetMonth = month.monthValue
    var counter = 0
    var amount = 0.0
    forEach { revenue ->
        val revenueMonth = monthFormatter.formatAsString(revenue.revenueTimestamp).toInt()
        if(targetMonth == revenueMonth) {
            counter++
            amount += revenue.value
        }
    }
    return Pair(counter, amount)
}

fun List<Revenue>.getProjectRevenue(
    revenueId: String
): ProjectRevenue? {
    forEach { revenue ->
        if(revenue.id == revenueId)
            return revenue as ProjectRevenue
    }
    return null
}