package com.tecknobit.neutron.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

abstract class Screen {

    companion object {

        const val SPLASH_SCREEN = "splashscreen"

        const val HOME_SCREEN = "home"

        const val PROJECT_REVENUE_SCREEN = "project_revenue/"

        const val PROFILE_SCREEN = "profile"

    }

    @Composable
    abstract fun ShowScreen()

    @Composable
    protected fun DisplayContent(
        headerHeight: Dp = 175.dp,
        header: @Composable RowScope.() -> Unit,
        body: @Composable () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 100.dp,
                        start = 100.dp
                    ),
                shape = RoundedCornerShape(
                    topStart = 40.dp
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 15.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column (
                    modifier = Modifier
                        .height(headerHeight)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 35.dp,
                                end = 35.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        content = header
                    )
                }
                HorizontalDivider()
                body.invoke()
            }
        }
    }

}