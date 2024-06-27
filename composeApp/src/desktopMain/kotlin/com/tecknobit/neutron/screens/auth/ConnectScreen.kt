package com.tecknobit.neutron.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.neutron.screens.Screen
import com.tecknobit.neutron.ui.NeutronButton
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.displayFontFamily
import com.tecknobit.neutron.viewmodels.ConnectViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.*
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.net.URI

/**
 * The **ConnectScreen** class is useful to manage the authentication requests of the user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see Screen
 */
class ConnectScreen: Screen() {

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = ConnectViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * Function to show the content of the screen
     *
     * No-any params required
     */
    @Composable
    override fun ShowScreen() {
        viewModel.isSignUp = remember { mutableStateOf(true) }
        viewModel.host = remember { mutableStateOf("") }
        viewModel.hostError = remember { mutableStateOf(false) }
        viewModel.serverSecret = remember { mutableStateOf("") }
        viewModel.serverSecretError = remember { mutableStateOf(false) }
        viewModel.name = remember { mutableStateOf("") }
        viewModel.nameError = remember { mutableStateOf(false) }
        viewModel.surname = remember { mutableStateOf("") }
        viewModel.surnameError = remember { mutableStateOf(false) }
        viewModel.email = remember { mutableStateOf("") }
        viewModel.emailError = remember { mutableStateOf(false) }
        viewModel.password = remember { mutableStateOf("") }
        viewModel.passwordError = remember { mutableStateOf(false) }
        Scaffold (
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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

    /**
     * Function to create the header section of the activity
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
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
                            if (viewModel.isSignUp.value)
                                Res.string.hello
                            else
                                Res.string.welcome_back
                        ),
                        fontFamily = displayFontFamily,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(
                            if (viewModel.isSignUp.value)
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

    /**
     * Function to create the form where the user can fill it with his credentials
     *
     * No-any params required
     */
    @OptIn(ExperimentalResourceApi::class)
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
                val keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
                NeutronOutlinedTextField(
                    value = viewModel.host,
                    label = Res.string.host_address,
                    keyboardOptions = keyboardOptions,
                    errorText = Res.string.host_address_not_valid,
                    isError = viewModel.hostError,
                    validator = { isHostValid(it) }
                )
                AnimatedVisibility(
                    visible = viewModel.isSignUp.value
                ) {
                    NeutronOutlinedTextField(
                        value = viewModel.serverSecret,
                        label = Res.string.server_secret,
                        keyboardOptions = keyboardOptions,
                        errorText = Res.string.server_secret_not_valid,
                        isError = viewModel.serverSecretError,
                        validator = { isServerSecretValid(it) }
                    )
                }
                AnimatedVisibility(
                    visible = viewModel.isSignUp.value
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeutronOutlinedTextField(
                            value = viewModel.name,
                            label = Res.string.name,
                            keyboardOptions = keyboardOptions,
                            errorText = Res.string.name_not_valid,
                            isError = viewModel.nameError,
                            validator = { isNameValid(it) }

                        )
                        NeutronOutlinedTextField(
                            value = viewModel.surname,
                            label = Res.string.surname,
                            keyboardOptions = keyboardOptions,
                            errorText = Res.string.surname_not_valid,
                            isError = viewModel.surnameError,
                            validator = { isSurnameValid(it) }
                        )
                    }
                }
                NeutronOutlinedTextField(
                    value = viewModel.email,
                    label = Res.string.email,
                    mustBeInLowerCase = true,
                    keyboardOptions = keyboardOptions,
                    errorText = Res.string.email_not_valid,
                    isError = viewModel.emailError,
                    validator = { isEmailValid(it) }
                )
                var hiddenPassword by remember { mutableStateOf(true) }
                NeutronOutlinedTextField(
                    value = viewModel.password,
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
                    ),
                    errorText = Res.string.password_not_valid,
                    isError = viewModel.passwordError,
                    validator = { isPasswordValid(it) }
                )
                NeutronButton(
                    modifier = Modifier
                        .padding(
                            top = 10.dp
                        )
                        .width(300.dp),
                    onClick = { viewModel.auth() },
                    text = if (viewModel.isSignUp.value)
                        Res.string.sign_up_btn
                    else
                        Res.string.sign_in_btn
                )
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (viewModel.isSignUp.value)
                                Res.string.have_an_account
                            else
                                Res.string.are_you_new_to_neutron
                        ),
                        fontSize = 14.sp
                    )
                    Text(
                        modifier = Modifier
                            .clickable { viewModel.isSignUp.value = !viewModel.isSignUp.value },
                        text = stringResource(
                            if (viewModel.isSignUp.value)
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

}