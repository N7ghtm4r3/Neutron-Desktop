package com.tecknobit.neutron.ui.screens.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.apimanager.apis.QRCodeHelper
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.displayFontFamily
import com.tecknobit.neutron.ui.navigator
import com.tecknobit.neutron.ui.screens.Screen
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.json.JSONObject
import java.awt.Desktop
import java.io.InputStream
import java.net.URI
import java.util.*

class ConnectScreen: Screen() {

    private lateinit var isSignUp: MutableState<Boolean>

    private lateinit var storeDataOnline: MutableState<Boolean>

    private lateinit var showQrCodeLogin: MutableState<Boolean>

    private var localDatabaseNotExists: Boolean = true

    @Composable
    override fun ShowScreen() {
        isSignUp = remember { mutableStateOf(true) }
        storeDataOnline = remember { mutableStateOf(false) }
        showQrCodeLogin = remember { mutableStateOf(false) }
        localDatabaseNotExists = Random().nextBoolean() // TODO: TO INIT CORRECTLY FETCHING THE DATABASE
        Scaffold (
            floatingActionButton = {
                AnimatedVisibility(
                    visible = !isSignUp.value && !storeDataOnline.value && localDatabaseNotExists
                ) {
                    FloatingActionButton(
                        onClick = { showQrCodeLogin.value = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null
                        )
                    }
                    LoginQrCode()
                }
            }
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
            ) {
                HeaderSection()
                FormSection()
            }
        }
    }

    @Composable
    private fun HeaderSection() {
        Column (
            modifier = Modifier
                .height(110.dp)
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                Res.string.hello
                            else
                                Res.string.welcome_back
                        ),
                        fontFamily = displayFontFamily,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                Res.string.sign_up
                            else
                                Res.string.sign_in
                        ),
                        fontFamily = displayFontFamily,
                        color = Color.White,
                        fontSize = 35.sp
                    )
                }
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                Desktop.getDesktop().browse(URI("https://github.com/N7ghtm4r3/Neutron-Desktop"))
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.github),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "v. ${stringResource(Res.string.app_version)}",
                            fontFamily = displayFontFamily,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun FormSection() {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Switch(
                        checked = storeDataOnline.value,
                        onCheckedChange = { storeDataOnline.value = it }
                    )
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                Res.string.store_data_online
                            else
                                Res.string.stored_data_online
                        )
                    )
                }
                val keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
                val host = remember { mutableStateOf("") }
                val serverSecret = remember { mutableStateOf("") }
                val name = remember { mutableStateOf("") }
                val surname = remember { mutableStateOf("") }
                AnimatedVisibility(
                    visible = storeDataOnline.value
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeutronOutlinedTextField(
                            value = host,
                            label = Res.string.host_address,
                            keyboardOptions = keyboardOptions
                        )
                        AnimatedVisibility(
                            visible = isSignUp.value
                        ) {
                            NeutronOutlinedTextField(
                                value = serverSecret,
                                label = Res.string.server_secret,
                                keyboardOptions = keyboardOptions
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = isSignUp.value
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeutronOutlinedTextField(
                            value = name,
                            label = Res.string.name,
                            keyboardOptions = keyboardOptions

                        )
                        NeutronOutlinedTextField(
                            value = surname,
                            label = Res.string.surname,
                            keyboardOptions = keyboardOptions
                        )
                    }
                }
                val email = remember { mutableStateOf("") }
                AnimatedVisibility(
                    visible = !isSignUp.value && !storeDataOnline.value && localDatabaseNotExists
                ) {
                    Text(
                        modifier = Modifier
                            .width(300.dp),
                        text = stringResource(Res.string.local_sign_in_message),
                        textAlign = TextAlign.Justify,
                        fontSize = 12.sp
                    )
                }
                NeutronOutlinedTextField(
                    value = email,
                    label = Res.string.email,
                    keyboardOptions = keyboardOptions
                )
                val password = remember { mutableStateOf("") }
                var hiddenPassword by remember { mutableStateOf(true) }
                NeutronOutlinedTextField(
                    value = password,
                    label = Res.string.password,
                    trailingIcon = {
                        IconButton(
                            onClick = { hiddenPassword = !hiddenPassword }
                        ) {
                            Icon(
                                imageVector = if(hiddenPassword)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if(hiddenPassword)
                        PasswordVisualTransformation()
                    else
                        VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
                NeutronButton(
                    modifier = Modifier
                        .padding(
                            top = 10.dp
                        )
                        .width(300.dp),
                    onClick = {
                        // TODO: MAKE THE REQUEST THEN
                        navigator.navigate(HOME_SCREEN)
                    },
                    text = if(isSignUp.value)
                        Res.string.sign_up_btn
                    else
                        Res.string.sign_in_btn
                )
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(
                            if(isSignUp.value)
                                Res.string.have_an_account
                            else
                                Res.string.are_you_new_to_neutron
                        ),
                        fontSize = 14.sp
                    )
                    Text(
                        modifier = Modifier
                            .clickable { isSignUp.value = !isSignUp.value },
                        text = stringResource(
                            if(isSignUp.value)
                                Res.string.sign_in_btn
                            else
                                Res.string.sign_up_btn
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun LoginQrCode() {
        val qrCodeHelper = QRCodeHelper()
        val qrcode: InputStream
        if(showQrCodeLogin.value) {
            // TODO: TO CREATE THE SESSION WITH THE SOCKETMANAGER TO PASS IN THE QRCODE DATA
            qrcode = qrCodeHelper.getQRCodeStream(
                JSONObject().put("data", "real_data"),
                "localSignIn.png",
                200
            )
            ModalBottomSheet(
                onDismissRequest = {
                    qrCodeHelper.deleteQRCode(qrcode)
                    showQrCodeLogin.value = false
                }
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = 16.dp
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(
                                    size = 10.dp
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    size = 10.dp
                                )
                            )
                            .size(175.dp),
                        bitmap = loadImageBitmap(qrcode),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 10.dp
                            ),
                        text = stringResource(Res.string.signup_qr_code_title)
                    )
                }
            }
        }
    }

}