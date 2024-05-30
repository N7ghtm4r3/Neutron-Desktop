package com.tecknobit.neutron.ui.screens.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.ui.PROJECT_LABEL
import com.tecknobit.neutron.ui.displayFontFamily
import com.tecknobit.neutron.ui.navigator
import com.tecknobit.neutron.ui.screens.Screen
import com.tecknobit.neutron.ui.theme.AppTypography
import com.tecknobit.neutron.ui.theme.primaryLight
import com.tecknobit.neutroncore.records.User
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import kotlinx.coroutines.delay
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.app_name
import neutron.composeapp.generated.resources.project
import org.jetbrains.compose.resources.stringResource

class Splashscreen : Screen() {

    companion object {

        // TODO: TO INIT CORRECTLY
        val user = User()

    }

    @Composable
    override fun ShowScreen() {
        PROJECT_LABEL = RevenueLabel(
            stringResource(Res.string.project),
            ProjectRevenue.PROJECT_LABEL_COLOR
        )
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(primaryLight)
        ) {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = stringResource(Res.string.app_name),
                    color = Color.White,
                    style = AppTypography.displayLarge,
                    fontSize = 55.sp,
                )
            }
            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(
                        bottom = 16.dp
                    ),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "by Tecknobit",
                    color = Color.White,
                    fontFamily = displayFontFamily,
                    fontSize = 14.sp,
                )
            }
        }
        LaunchedEffect(true) {
            delay(250L)
            // TODO: MAKE THE REAL NAVIGATION
            navigator.navigate(HOME_SCREEN)
        }
    }

}