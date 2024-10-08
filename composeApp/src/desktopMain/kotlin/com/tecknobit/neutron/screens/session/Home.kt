package com.tecknobit.neutron.screens.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.neutron.screens.Screen
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.sections.addsections.AddRevenuesSection
import com.tecknobit.neutron.ui.DisplayRevenues
import com.tecknobit.neutron.ui.bodyFontFamily
import com.tecknobit.neutron.ui.imageLoader
import com.tecknobit.neutron.ui.navigator
import com.tecknobit.neutron.viewmodels.HomeViewModel
import com.tecknobit.neutron.viewmodels.addactivities.AddRevenuesViewModel
import com.tecknobit.neutroncore.records.revenues.Revenue
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.earnings
import neutron.composeapp.generated.resources.last_month
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

/**
 * The **Home** class is the screen where the user can show his/her revenues and create
 * others revenues
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see Screen
 */
class Home: Screen() {

    companion object {

        /**
         * **revenues** the current list of the user's revenues
         */
        lateinit var revenues: State<MutableList<Revenue>?>

    }

    /**
     * **addRevenue** whether to display the section to add a new revenues
     */
    private lateinit var addRevenue: MutableState<Boolean>

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = HomeViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private lateinit var addRevenuesViewModel: AddRevenuesViewModel

    /**
     * Function to show the content of the screen
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun ShowScreen() {
        viewModel.setActiveContext(this::class.java)
        addRevenue = remember { mutableStateOf(false) }
        Scaffold (
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.suspendRefresher()
                        addRevenue.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        ) {
            viewModel.getRevenuesList()
            revenues = viewModel.revenues.collectAsState()
            val walletBalance = viewModel.walletBalance.collectAsState()
            val walletTrend = viewModel.walletTrend.collectAsState()
            AddRevenue()
            DisplayContent(
                header = {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.earnings)
                        )
                        Text(
                            text = "${walletBalance.value}${localUser.currency.symbol}",
                            fontFamily = bodyFontFamily,
                            fontSize = 45.sp
                        )
                        Text(
                            text = "${walletTrend.value}/" + stringResource(Res.string.last_month)
                        )
                    }
                    Column (
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(125.dp)
                                .shadow(
                                    elevation = 5.dp,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .clickable { navigator.navigate(PROFILE_SCREEN) },
                            imageLoader = imageLoader,
                            contentScale = ContentScale.Crop,
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(localUser.profilePic)
                                .crossfade(true)
                                .crossfade(500)
                                .build(),
                            error = painterResource("logo.png"),
                            contentDescription = null
                        )
                    }
                },
                body = {
                    DisplayRevenues(
                        snackbarHostState = snackbarHostState,
                        revenues = revenues.value!!,
                        navToProject = { revenue ->
                            ProjectRevenueScreen.projectRevenueId = revenue.id
                            navigator.navigate(PROJECT_REVENUE_SCREEN)
                        }
                    )
                }
            )
        }
    }

    /**
     * Function to create the section to add the new revenues
     *
     * No-any params required
     */
    @Composable
    private fun AddRevenue() {
        if (::addRevenuesViewModel.isInitialized.not()) {
            addRevenuesViewModel = AddRevenuesViewModel(
                snackbarHostState = snackbarHostState
            )
            AddRevenuesSection(
                startContext = this::class.java,
                mainViewModel = viewModel,
                show = addRevenue,
                viewModel = addRevenuesViewModel
            ).AddRevenue()
        }
    }

}