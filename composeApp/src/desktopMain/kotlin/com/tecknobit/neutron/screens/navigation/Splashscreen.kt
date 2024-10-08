package com.tecknobit.neutron.screens.navigation

import UpdaterDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.helpers.DesktopLocalUser
import com.tecknobit.neutron.screens.Screen
import com.tecknobit.neutron.ui.PROJECT_LABEL
import com.tecknobit.neutron.ui.displayFontFamily
import com.tecknobit.neutron.ui.navigator
import com.tecknobit.neutron.ui.theme.AppTypography
import com.tecknobit.neutron.ui.theme.primaryLight
import com.tecknobit.neutron.viewmodels.NeutronViewModel.Companion.requester
import com.tecknobit.neutroncore.helpers.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.neutroncore.helpers.InputValidator.LANGUAGES_SUPPORTED
import com.tecknobit.neutroncore.helpers.NeutronRequester
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.app_name
import neutron.composeapp.generated.resources.app_version
import neutron.composeapp.generated.resources.project
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import java.util.*

/**
 * The **Splashscreen** class is the entry point of the application
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see Screen
 */
class Splashscreen : Screen() {

    companion object {

        /**
         * **localUser** the user of the current logged-in session, used to make the requests to the
         * backed
         */
        val localUser = DesktopLocalUser()

    }

    /**
     * Function to show the content of the screen
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun ShowScreen() {
        var startApp by remember { mutableStateOf(true) }
        setLocale()
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
        UpdaterDialog(
            appName = stringResource(Res.string.app_name),
            currentVersion = stringResource(Res.string.app_version),
            onUpdateAvailable = {
                startApp = false
            },
            dismissAction = {
                startApp = true
            }
        )
        if(startApp) {
            requester = NeutronRequester(
                host = localUser.hostAddress,
                userId = localUser.userId,
                userToken = localUser.userToken
            )
            navigator.navigate(
                if (localUser.isAuthenticated)
                    HOME_SCREEN
                else
                    CONNECT_SCREEN
            )
        }
    }

    /**
     * Function to set locale language for the application
     *
     * No-any params required
     */
    private fun setLocale() {
        var tag: String = DEFAULT_LANGUAGE
        LANGUAGES_SUPPORTED.keys.forEach { key ->
            if(key == localUser.language) {
                tag = key
                return@forEach
            }
        }
        Locale.setDefault(Locale.forLanguageTag(tag))
    }

}