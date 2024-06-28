package com.tecknobit.neutron.ui
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import coil3.ImageLoader
import coil3.addLastModifiedToFileCacheKey
import coil3.compose.LocalPlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import com.tecknobit.neutron.screens.Screen.Companion.CONNECT_SCREEN
import com.tecknobit.neutron.screens.Screen.Companion.HOME_SCREEN
import com.tecknobit.neutron.screens.Screen.Companion.PROFILE_SCREEN
import com.tecknobit.neutron.screens.Screen.Companion.PROJECT_REVENUE_SCREEN
import com.tecknobit.neutron.screens.Screen.Companion.SPLASH_SCREEN
import com.tecknobit.neutron.screens.auth.ConnectScreen
import com.tecknobit.neutron.screens.navigation.Splashscreen
import com.tecknobit.neutron.screens.session.Home
import com.tecknobit.neutron.screens.session.ProfileScreen
import com.tecknobit.neutron.screens.session.ProjectRevenueScreen
import com.tecknobit.neutron.ui.theme.NeutronTheme
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.anektelugu
import neutron.composeapp.generated.resources.lilitaone
import okhttp3.OkHttpClient
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * **bodyFontFamily** -> the Neutron's body font family
 */
lateinit var bodyFontFamily: FontFamily

/**
 * **displayFontFamily** -> the Neutron's font family
 */
lateinit var displayFontFamily: FontFamily

/**
 * **navigator** -> the navigator instance is useful to manage the navigation between the screens of the application
 */
lateinit var navigator: Navigator

/**
 * **sslContext** -> the context helper to TLS protocols
 */
private val sslContext = SSLContext.getInstance("TLS")

/**
 * **imageLoader** -> the image loader used by coil library to load the image and by-passing the https self-signed certificates
 */
lateinit var imageLoader: ImageLoader

/**
 * Method to sho the layout of **Neutron** desktop app.
 * No-any params required
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    bodyFontFamily = FontFamily(Font(Res.font.anektelugu))
    displayFontFamily = FontFamily(Font(Res.font.lilitaone))
    sslContext.init(null, validateSelfSignedCertificate(), SecureRandom())
    imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
        .components {
            add(
                OkHttpNetworkFetcherFactory {
                    OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.socketFactory,
                            validateSelfSignedCertificate()[0] as X509TrustManager
                        )
                        .hostnameVerifier { _: String?, _: SSLSession? -> true }
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .build()
                }
            )
        }
        .addLastModifiedToFileCacheKey(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
    PreComposeApp {
        navigator = rememberNavigator()
        NavHost(
            navigator = navigator,
            initialRoute = SPLASH_SCREEN
        ) {
            scene(
                route = SPLASH_SCREEN
            ) {
                NeutronTheme {
                    Splashscreen().ShowScreen()
                }
            }
            scene(
                route = HOME_SCREEN
            ) {
                NeutronTheme {
                    Home().ShowScreen()
                }
            }
            scene(
                route = PROJECT_REVENUE_SCREEN
            ) {
                NeutronTheme {
                    ProjectRevenueScreen().ShowScreen()
                }
            }
            scene(
                route = PROFILE_SCREEN
            ) {
                NeutronTheme {
                    ProfileScreen().ShowScreen()
                }
            }
            scene(
                route = CONNECT_SCREEN
            ) {
                NeutronTheme {
                    ConnectScreen().ShowScreen()
                }
            }
        }
    }
}

/**
 * Method to validate a self-signed SLL certificate and bypass the checks of its validity<br></br>
 * No-any params required
 *
 * @return list of trust managers as [Array] of [TrustManager]
 * @apiNote this method disable all checks on the SLL certificate validity, so is recommended to
 * use for test only or in a private distribution on own infrastructure
 */
private fun validateSelfSignedCertificate(): Array<TrustManager> {
    return arrayOf(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
    })
}