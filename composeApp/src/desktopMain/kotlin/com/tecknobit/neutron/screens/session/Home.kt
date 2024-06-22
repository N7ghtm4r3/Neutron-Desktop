package com.tecknobit.neutron.screens.session

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.screens.Screen
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.sections.addsections.AddRevenuesSection
import com.tecknobit.neutron.ui.*
import com.tecknobit.neutron.viewmodels.HomeViewModel
import com.tecknobit.neutron.viewmodels.addactivities.AddRevenuesViewModel
import com.tecknobit.neutroncore.records.revenues.Revenue
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.earnings
import neutron.composeapp.generated.resources.last_month
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

class Home: Screen() {

    companion object {

        lateinit var revenues: State<MutableList<Revenue>?>

    }

    private lateinit var addRevenue: MutableState<Boolean>

    private val viewModel = HomeViewModel(
        snackbarHostState = snackbarHostState
    )

    private lateinit var addRevenuesViewModel: AddRevenuesViewModel

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
                            text = "${revenues.value!!.getWalletBalance()}${localUser.currency.symbol}",
                            fontFamily = bodyFontFamily,
                            fontSize = 45.sp
                        )
                        val walletTrend = revenues.value!!.getWalletTrend()
                        if(walletTrend != null) {
                            Text(
                                text = "$walletTrend/" + stringResource(Res.string.last_month)
                            )
                        }
                    }
                    Column (
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        //TODO: TO FIX
                        Button(
                            onClick = {
                                navigator.navigate(PROFILE_SCREEN)
                            }
                        ) {
                            Text("to remove")
                        }
                        /*AsyncImage(
                            modifier = Modifier
                                .size(125.dp)
                                .shadow(
                                    elevation = 5.dp,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .clickable {  },
                            imageLoader = imageLoader,
                            contentScale = ContentScale.Crop,
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                //.data(localUser.profilePic)
                                .crossfade(true)
                                .crossfade(500)
                                .build(),
                            //TODO: USE THE REAL IMAGE ERROR .error(),
                            contentDescription = null
                        )*/
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