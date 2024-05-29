package com.tecknobit.neutron.ui
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.tecknobit.neutron.ui.screens.Screen.Companion.HOME_SCREEN
import com.tecknobit.neutron.ui.screens.Screen.Companion.SPLASH_SCREEN
import com.tecknobit.neutron.ui.screens.navigation.Splashscreen
import com.tecknobit.neutron.ui.screens.session.Home
import com.tecknobit.neutron.ui.screens.session.ProjectRevenueScreen
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.anektelugu
import neutron.composeapp.generated.resources.lilitaone
import org.jetbrains.compose.resources.Font

lateinit var bodyFontFamily: FontFamily

lateinit var displayFontFamily: FontFamily

lateinit var navigator: Navigator

@Composable
fun App() {
    bodyFontFamily = FontFamily(Font(Res.font.anektelugu))
    displayFontFamily = FontFamily(Font(Res.font.lilitaone))
    PreComposeApp {
        NeutronTheme {
            navigator = rememberNavigator()
            NavHost(
                navigator = navigator,
                initialRoute = SPLASH_SCREEN
            ) {
                scene(
                    route = SPLASH_SCREEN
                ) {
                    Splashscreen().ShowScreen()
                }
                scene(
                    route = HOME_SCREEN
                ) {
                    Home().ShowScreen()
                }
                scene(
                    route = "project_revenue/{$IDENTIFIER_KEY}"
                ) { backstackEntry ->
                    ProjectRevenueScreen(
                        projectRevenueId = backstackEntry.pathMap[IDENTIFIER_KEY]
                    ).ShowScreen()
                }
            }
        }
    }
}