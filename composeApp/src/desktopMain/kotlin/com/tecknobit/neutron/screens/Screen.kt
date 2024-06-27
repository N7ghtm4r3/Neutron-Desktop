package com.tecknobit.neutron.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecknobit.apimanager.annotations.Structure

/**
 * The **Screen** class is useful to create a screen with the behavior to show the UI
 * data correctly
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Structure
abstract class Screen {

    companion object {

        /**
         * **SPLASH_SCREEN** -> route to navigate to the [SplashScreen]
         */
        const val SPLASH_SCREEN = "splashscreen"

        /**
         * **HOME_SCREEN** -> route to navigate to the [Home]
         */
        const val HOME_SCREEN = "home"

        /**
         * **PROJECT_REVENUE_SCREEN** -> route to navigate to the [ProjectRevenueScreen]
         */
        const val PROJECT_REVENUE_SCREEN = "project_revenue"

        /**
         * **PROFILE_SCREEN** -> route to navigate to the [ProfileScreen]
         */
        const val PROFILE_SCREEN = "profile"

        /**
         * **CONNECT_SCREEN** -> route to navigate to the [ConnectScreen]
         */
        const val CONNECT_SCREEN = "connect"

    }

    /**
     * *snackbarHostState* -> the host to launch the snackbar messages
     */
    protected val snackbarHostState by lazy {
        SnackbarHostState()
    }

    /**
     * Function to show the content of the screen
     *
     * No-any params required
     */
    @Composable
    abstract fun ShowScreen()

    /**
     * Function to display the content of an activity
     *
     * @param headerHeight: height of the header
     * @param header: the content of the header used as top bar
     * @param body: the body content to display in the activity
     */
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