package com.tecknobit.neutron.ui.screens.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.apimanager.trading.TradingTools.textualizeAssetPercent
import com.tecknobit.neutron.ui.DisplayRevenues
import com.tecknobit.neutron.ui.bodyFontFamily
import com.tecknobit.neutron.ui.getWalletBalance
import com.tecknobit.neutron.ui.navigator
import com.tecknobit.neutron.ui.screens.Screen
import com.tecknobit.neutron.ui.screens.navigation.Splashscreen.Companion.user
import com.tecknobit.neutron.ui.sections.addsections.AddRevenuesSection
import com.tecknobit.neutroncore.records.revenues.*
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.earnings
import neutron.composeapp.generated.resources.last_month
import org.jetbrains.compose.resources.stringResource

class Home: Screen() {

    companion object {

        val revenues = mutableStateListOf<Revenue>()

    }

    init {
        // TODO: LOAD CORRECTLY
        revenues.add(
            ProjectRevenue(
                "gag",
                "Prova",
                System.currentTimeMillis(),
                InitialRevenue(
                    "gaga",
                    2000.0,
                    System.currentTimeMillis()
                ),
                listOf(
                    TicketRevenue(
                        "g11aga",
                        "Ciao",
                        1000.0,
                        System.currentTimeMillis(),
                        "gaaga",
                        1715893715000L
                    ),
                    TicketRevenue(
                        "g1aga",
                        "Ciao",
                        1000.0,
                        System.currentTimeMillis(),
                        "gaaga",
                        System.currentTimeMillis()
                    ),
                    TicketRevenue(
                        "gaga",
                        "Ciao",
                        1000.0,
                        System.currentTimeMillis(),
                        "gaaga",
                        System.currentTimeMillis()
                    ),
                    TicketRevenue(
                        "25gaga",
                        "Ciao",
                        1000.0,
                        System.currentTimeMillis(),
                        "gaaga",
                        System.currentTimeMillis()
                    ),
                    TicketRevenue(
                        "24gaga",
                        "Ciao",
                        1000.0,
                        System.currentTimeMillis(),
                        "gaaga"
                    ),
                    TicketRevenue(
                        "4gaga",
                        "Ciao",
                        1000.0,
                        System.currentTimeMillis(),
                        "gaaga",
                        System.currentTimeMillis()
                    ),
                    TicketRevenue(
                        "3gaga",
                        "Ciao",
                        1000.0,
                        System.currentTimeMillis(),
                        "gaaga"
                    )
                )
            )
        )
        revenues.add(
            GeneralRevenue(
                "aaa",
                "General",
                100000.0,
                System.currentTimeMillis(),
                listOf(
                    RevenueLabel(
                        "ff",
                        "Prog",
                        "#33A396"
                    ),
                    RevenueLabel(
                        "sff",
                        "Proggag",
                        "#8BAEA2"
                    ),
                    RevenueLabel(
                        "sffa",
                        "cfnafna",
                        "#59EC21"
                    )
                ),
                "Lorem Ipsum è un testo segnaposto utilizzato nel settore della tipografia e della stampa. Lorem Ipsum è considerato il testo segnaposto standard sin dal sedicesimo secolo, quando un anonimo tipografo prese una cassetta di caratteri e li assemblò per preparare un testo campione. È sopravvissuto non solo a più di cinque secoli, ma anche al passaggio alla videoimpaginazione, pervenendoci sostanzialmente inalterato. Fu reso popolare, negli anni ’60, con la diffusione dei fogli di caratteri trasferibili “Letraset”, che contenevano passaggi del Lorem Ipsum, e più recentemente da software di impaginazione come Aldus PageMaker, che includeva versioni del Lorem Ipsum."
            )
        )
        revenues.add(
            GeneralRevenue(
                "aaaa",
                "General",
                2000.0,
                System.currentTimeMillis(),
                emptyList(),
                "Prova\nagag\naagagaga\nanan\n"
            )
        )
    }

    @Composable
    override fun ShowScreen() {
        // TODO: USE THE REAL DATA
        val addRevenue = remember { mutableStateOf(false) }
        val walletTrendPercent by remember { mutableDoubleStateOf(1.0) }
        Scaffold (
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { addRevenue.value = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        ) {
            AddRevenuesSection(addRevenue).AddRevenue()
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
                            text = "${revenues.getWalletBalance()}${user.currency.symbol}",
                            fontFamily = bodyFontFamily,
                            fontSize = 45.sp
                        )
                        Text(
                            text = "${textualizeAssetPercent(walletTrendPercent)}/"
                                    + stringResource(Res.string.last_month)
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
                                .clickable {
                                    // TODO: NAV TO PROFILE
                                },
                            contentScale = ContentScale.Crop,
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(user.profilePic)
                                .crossfade(true)
                                .crossfade(500)
                                .build(),
                            //TODO: USE THE REAL IMAGE ERROR .error(),
                            contentDescription = null
                        )
                    }
                },
                body = {
                    DisplayRevenues(
                        revenues = revenues,
                        navToProject = { revenue ->
                            navigator.navigate(
                                route = PROJECT_REVENUE_SCREEN + revenue.id
                            )
                        }
                    )
                }
            )
        }
    }

}