import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.tecknobit.neutron.ui.App
import neutron.composeapp.generated.resources.Res
import neutron.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        state = WindowState(
            placement = WindowPlacement.Maximized
        )
    ) {
        App()
    }
}