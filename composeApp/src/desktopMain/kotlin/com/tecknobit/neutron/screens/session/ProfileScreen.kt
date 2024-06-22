package com.tecknobit.neutron.screens.session

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.tecknobit.neutron.screens.Screen
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.ui.NeutronAlertDialog
import com.tecknobit.neutron.ui.NeutronOutlinedTextField
import com.tecknobit.neutron.ui.displayFontFamily
import com.tecknobit.neutron.ui.navigator
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.errorLight
import com.tecknobit.neutron.viewmodels.ProfileViewModel
import com.tecknobit.neutroncore.helpers.InputValidator.*
import com.tecknobit.neutroncore.records.User.ApplicationTheme
import com.tecknobit.neutroncore.records.User.ApplicationTheme.*
import com.tecknobit.neutroncore.records.User.NeutronCurrency
import com.tecknobit.neutroncore.records.User.UserStorage.Local
import kotlinx.coroutines.delay
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.util.*

class ProfileScreen: Screen() {

    private lateinit var theme: MutableState<ApplicationTheme>

    private lateinit var hostLocalSignIn: MutableState<Boolean>

    private val currentStorageIsLocal = localUser.storage == Local

    private val viewModel = ProfileViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * **fileType** -> list of allowed image types
     */
    private val fileType = listOf("jpg", "png", "jpeg")

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun ShowScreen() {
        viewModel.setActiveContext(this::class.java)
        theme = remember { mutableStateOf(localUser.theme) }
        val profilePic = remember { mutableStateOf(localUser.profilePic) }
        var pickProfilePic by remember { mutableStateOf(false) }
        NeutronTheme (
            darkTheme = when(theme.value) {
                Light -> false
                Dark -> true
                else -> isSystemInDarkTheme()
            }
        ) {
            Scaffold (
                floatingActionButton = {
                    val showDeleteAlert = remember { mutableStateOf(false) }
                    FloatingActionButton(
                        onClick = {
                            showDeleteAlert.value = true
                        },
                        containerColor = errorLight
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null
                        )
                    }
                    NeutronAlertDialog(
                        icon = Icons.Default.Cancel,
                        show = showDeleteAlert,
                        title = Res.string.delete,
                        text = Res.string.delete_message,
                        confirmAction = {
                            viewModel.deleteAccount {
                                navToSplash()
                            }
                        }
                    )
                }
            ) {
                DisplayContent(
                    headerHeight = 250.dp,
                    header = {
                        Column {
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column {
                                    IconButton(
                                        modifier = Modifier
                                            .padding(
                                                top = 16.dp
                                            )
                                            .align(Alignment.Start),
                                        onClick = { navBack() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                }
                                if(currentStorageIsLocal) {
                                    hostLocalSignIn = remember { mutableStateOf(false) }
                                    HostLocalSignIn()
                                    Column (
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        IconButton(
                                            modifier = Modifier
                                                .padding(
                                                    top = 16.dp
                                                ),
                                            onClick = { hostLocalSignIn.value = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lan,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            }
                            //TODO: TO FIX
                            Button(
                                onClick = {
                                    pickProfilePic = true
                                }
                            ) {
                                Text("to remove")
                            }
                            /*AsyncImage(
                                modifier = Modifier
                                    .size(150.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .shadow(
                                        elevation = 5.dp,
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable { pickProfilePic = true },
                                imageLoader = imageLoader,
                                contentScale = ContentScale.Crop,
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(profilePic)
                                    .crossfade(true)
                                    .crossfade(500)
                                    .build(),
                                //TODO: USE THE REAL IMAGE ERROR .error(),
                                contentDescription = null
                            )*/
                            FilePicker(
                                show = pickProfilePic,
                                fileExtensions = fileType
                            ) { profilePicPath ->
                                if(profilePicPath != null) {
                                    viewModel.changeProfilePic(
                                        imagePath = profilePicPath.path,
                                        profilePic = profilePic
                                    )
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        bottom = 16.dp
                                    )
                                    .fillMaxWidth(),
                                text = localUser.completeName,
                                fontFamily = displayFontFamily,
                                fontSize = 20.sp
                            )
                        }
                    },
                    body = {
                        Column (
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            val showChangeEmailAlert = remember { mutableStateOf(false) }
                            var userEmail by remember { mutableStateOf(localUser.email) }
                            viewModel.newEmail = remember { mutableStateOf("") }
                            viewModel.newEmailError = remember { mutableStateOf(false) }
                            val resetEmailLayout = {
                                viewModel.newEmail.value = ""
                                viewModel.newEmailError.value = false
                                showChangeEmailAlert.value = false
                            }
                            UserInfo(
                                header = Res.string.email,
                                info = userEmail,
                                onClick = { showChangeEmailAlert.value = true }
                            )
                            NeutronAlertDialog(
                                onDismissAction = resetEmailLayout,
                                icon = Icons.Default.Email,
                                show = showChangeEmailAlert,
                                title = Res.string.change_email,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = viewModel.newEmail,
                                        label = Res.string.new_email,
                                        errorText = Res.string.email_not_valid,
                                        isError = viewModel.newEmailError,
                                        validator = { isEmailValid(it) }
                                    )
                                },
                                confirmAction = {
                                    viewModel.changeEmail(
                                        onSuccess = {
                                            userEmail = viewModel.newEmail.value
                                            resetEmailLayout.invoke()
                                        }
                                    )
                                }
                            )
                            val showChangePasswordAlert = remember { mutableStateOf(false) }
                            viewModel.newPassword = remember { mutableStateOf("") }
                            viewModel.newPasswordError = remember { mutableStateOf(false) }
                            val resetPasswordLayout = {
                                viewModel.newPassword.value = ""
                                viewModel.newPasswordError.value = false
                                showChangePasswordAlert.value = false
                            }
                            var hiddenPassword by remember { mutableStateOf(true) }
                            UserInfo(
                                header = Res.string.password,
                                info = "****",
                                onClick = { showChangePasswordAlert.value = true }
                            )
                            NeutronAlertDialog(
                                onDismissAction = resetPasswordLayout,
                                icon = Icons.Default.Password,
                                show = showChangePasswordAlert,
                                title = Res.string.change_password,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = viewModel.newPassword,
                                        label = Res.string.new_password,
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
                                        isError = viewModel.newPasswordError,
                                        validator = { isPasswordValid(it) }
                                    )
                                },
                                confirmAction = {
                                    viewModel.changePassword(
                                        onSuccess = resetPasswordLayout
                                    )
                                }
                            )
                            val changeLanguage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = Res.string.language,
                                info = LANGUAGES_SUPPORTED[localUser.language]!!,
                                onClick = { changeLanguage.value = true }
                            )
                            ChangeLanguage(
                                changeLanguage = changeLanguage
                            )
                            val changeCurrency = remember { mutableStateOf(false) }
                            val currency = remember { mutableStateOf(localUser.currency.isoName) }
                            UserInfo(
                                header = Res.string.currency,
                                info = currency.value,
                                onClick = { changeCurrency.value = true }
                            )
                            ChangeCurrency(
                                changeCurrency = changeCurrency,
                                currencyValue = currency
                            )
                            val changeTheme = remember { mutableStateOf(false) }
                            UserInfo(
                                header = Res.string.theme,
                                info = localUser.theme.name,
                                buttonText = Res.string.change,
                                onClick = { changeTheme.value = true }
                            )
                            ChangeTheme(
                                changeTheme = changeTheme
                            )
                            val showChangeStorage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = Res.string.storage_data,
                                info = localUser.storage.name,
                                buttonText = Res.string.change,
                                onClick = { showChangeStorage.value = true }
                            )
                            ChangeStorage(
                                changeStorage = showChangeStorage
                            )
                            val showLogoutAlert = remember { mutableStateOf(false) }
                            UserInfo(
                                header = Res.string.disconnect,
                                info = stringResource(Res.string.logout),
                                buttonText = Res.string.execute,
                                onClick = { showLogoutAlert.value = true }
                            )
                            NeutronAlertDialog(
                                icon = Icons.AutoMirrored.Filled.ExitToApp,
                                show = showLogoutAlert,
                                title = Res.string.logout,
                                text = Res.string.logout_message,
                                confirmAction = {
                                    viewModel.clearSession {
                                        navToSplash()
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
    @Composable
    private fun HostLocalSignIn() {
        val isListening = remember { mutableStateOf(true) }
        // TODO: TO REMOVE GET FROM THE REAL REQUEST RESPONSE
        val success = remember { mutableStateOf(Random().nextBoolean()) }
        if(hostLocalSignIn.value) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(
                    confirmValueChange = { !isListening.value }
                ),
                onDismissRequest = {
                    if(!isListening.value)
                        hostLocalSignIn.value = false
                }
            ) {
                // TODO: IMPLEMENT THE SOCKETMANAGER OR THE WRAPPER CLASS TO EXECUTE THE HOSTING AND THE DATA TRANSFER

                // TODO: TO REMOVE MAKE THE REAL WORKFLOW INSTEAD
                LaunchedEffect(
                    key1 = true
                ) {
                    delay(3000L)
                    isListening.value = false
                }
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if(isListening.value) {
                        Text(
                            text = stringResource(Res.string.hosting_local_sign_in),
                            fontFamily = displayFontFamily,
                            fontSize = 20.sp
                        )
                    }
                    ResponseStatusUI(
                        isWaiting = isListening,
                        statusText = Res.string.waiting_for_the_request,
                        isSuccessful = success,
                        successText = Res.string.sign_in_executed_successfully,
                        failedText = Res.string.sign_in_failed_message
                    )
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.End),
                        onClick = {
                            // TODO: CLOSE THE LISTENING THEN
                            hostLocalSignIn.value = false
                            isListening.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(
                                if(isListening.value)
                                    Res.string.cancel
                                else
                                    Res.string.close
                            )
                        )
                    }
                }
            }
        } else
            isListening.value = true
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun UserInfo(
        header: StringResource,
        info: String,
        buttonText: StringResource = Res.string.edit,
        onClick: () -> Unit
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 12.dp
                )
        ) {
            Text(
                text = stringResource(header),
                fontSize = 18.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 5.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = info,
                    fontSize = 20.sp,
                    fontFamily = displayFontFamily
                )
                Button(
                    modifier = Modifier
                        .height(25.dp),
                    onClick = onClick,
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        end = 10.dp,
                        top = 0.dp,
                        bottom = 0.dp
                    ),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(
                        text = stringResource(buttonText),
                        fontSize = 12.sp
                    )
                }
            }
        }
        HorizontalDivider()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeLanguage(
        changeLanguage: MutableState<Boolean>
    ) {
        ChangeInfo(
            showModal = changeLanguage
        ) {
            LANGUAGES_SUPPORTED.keys.forEach { language ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.changeLanguage(
                                newLanguage = language,
                                onSuccess = {
                                    changeLanguage.value = false
                                    navToSplash()
                                }
                            )
                        }
                        .padding(
                            all = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = if (localUser.language == language)
                            MaterialTheme.colorScheme.primary
                        else
                            LocalContentColor.current
                    )
                    Text(
                        text = LANGUAGES_SUPPORTED[language]!!,
                        fontFamily = displayFontFamily
                    )
                }
                HorizontalDivider()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeCurrency(
        changeCurrency: MutableState<Boolean>,
        currencyValue: MutableState<String>
    ) {
        ChangeInfo(
            showModal = changeCurrency
        ) {
            NeutronCurrency.entries.forEach { currency ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.changeCurrency(
                                newCurrency = currency,
                                onSuccess = {
                                    currencyValue.value = currency.isoName
                                    changeCurrency.value = false
                                }
                            )
                        }
                        .padding(
                            all = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = if (localUser.currency == currency)
                            MaterialTheme.colorScheme.primary
                        else
                            LocalContentColor.current
                    )
                    Text(
                        text = "${currency.isoName} ${currency.isoCode}",
                        fontFamily = displayFontFamily
                    )
                }
                HorizontalDivider()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeTheme(
        changeTheme: MutableState<Boolean>
    ) {
        ChangeInfo(
            showModal = changeTheme
        ) {
            entries.forEach { theme ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.changeTheme(
                                newTheme = theme,
                                onChange = {
                                    changeTheme.value = false
                                    this@ProfileScreen.theme.value = theme
                                }
                            )
                        }
                        .padding(
                            all = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = when(theme) {
                            Light -> Icons.Default.LightMode
                            Dark-> Icons.Default.DarkMode
                            else -> Icons.Default.AutoMode
                        },
                        contentDescription = null,
                        tint = if (localUser.theme == theme)
                            MaterialTheme.colorScheme.primary
                        else
                            LocalContentColor.current
                    )
                    Text(
                        text = theme.toString(),
                        fontFamily = displayFontFamily
                    )
                }
                HorizontalDivider()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
    @Composable
    private fun ChangeStorage(
        changeStorage: MutableState<Boolean>
    ) {
        viewModel.hostAddress = remember { mutableStateOf("") }
        viewModel.hostError = remember { mutableStateOf(false) }
        viewModel.serverSecret = remember { mutableStateOf("") }
        viewModel.serverSecretError = remember { mutableStateOf(false) }
        viewModel.isExecuting = remember { mutableStateOf(false) }
        viewModel.waiting = remember { mutableStateOf(true) }
        viewModel.success = remember { mutableStateOf(false) }
        val executeRequest = {
            viewModel.isExecuting.value = true
            viewModel.waiting.value = true
            viewModel.success.value = false
            viewModel.changeStorage()
        }
        val resetLayout = {
            viewModel.isExecuting.value = false
            viewModel.hostAddress.value = ""
            viewModel.hostError.value = false
            viewModel.serverSecret.value = ""
            viewModel.serverSecretError.value = false
            changeStorage.value = false
            viewModel.waiting.value = true
            viewModel.success.value = false
        }
        ChangeInfo(
            showModal = changeStorage,
            sheetState = rememberModalBottomSheetState(
                confirmValueChange = { !viewModel.isExecuting.value || viewModel.success.value }
            ),
            onDismissRequest = { resetLayout.invoke() }
        ) {
            Column (
                modifier = Modifier
                    .padding(
                        all = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!viewModel.isExecuting.value) {
                    val awareText = stringResource(
                        if(currentStorageIsLocal)
                            Res.string.aware_server_message
                        else
                            Res.string.aware_local_message
                    )
                    Text(
                        text = stringResource(Res.string.change_storage_location),
                        fontFamily = displayFontFamily,
                        fontSize = 20.sp
                    )
                    Text(
                        text = awareText,
                        textAlign = TextAlign.Justify
                    )
                    if(currentStorageIsLocal) {
                        NeutronOutlinedTextField(
                            modifier = Modifier
                                .width(300.dp),
                            value = viewModel.hostAddress,
                            label = Res.string.host_address,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )
                        NeutronOutlinedTextField(
                            modifier = Modifier
                                .width(300.dp),
                            value = viewModel.serverSecret,
                            label = Res.string.server_secret
                        )
                    }
                } else {
                    ResponseStatusUI(
                        isWaiting = viewModel.waiting,
                        statusText = Res.string.transferring_data,
                        isSuccessful = viewModel.success,
                        successText = Res.string.transfer_executed_successfully,
                        failedText = Res.string.transfer_failed
                    )
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!viewModel.isExecuting.value) {
                        TextButton(
                            onClick = { resetLayout.invoke() }
                        ) {
                            Text(
                                text = stringResource(
                                    if (viewModel.isExecuting.value)
                                        Res.string.cancel
                                    else
                                        Res.string.dismiss
                                )
                            )
                        }
                        TextButton(
                            onClick = { executeRequest.invoke() }
                        ) {
                            Text(
                                text = stringResource(Res.string.confirm)
                            )
                        }
                    } else {
                        TextButton(
                            onClick = {
                                if (viewModel.waiting.value)
                                    viewModel.isExecuting.value = false
                                else {
                                    if (viewModel.success.value) {
                                        resetLayout.invoke()
                                        changeStorage.value = false
                                            Local
                                        navToSplash()
                                    } else
                                        executeRequest.invoke()
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    if (viewModel.waiting.value)
                                        Res.string.cancel
                                    else {
                                        if (viewModel.success.value)
                                            Res.string.close
                                        else
                                            Res.string.retry
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeInfo(
        showModal: MutableState<Boolean>,
        sheetState: SheetState = rememberModalBottomSheetState(),
        onDismissRequest: () -> Unit = { showModal.value = false },
        content: @Composable ColumnScope.() -> Unit
    ) {
        if(showModal.value) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = onDismissRequest
            ) {
                Column (
                    content = content
                )
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ResponseStatusUI(
        isWaiting: MutableState<Boolean>,
        statusText: StringResource,
        isSuccessful: MutableState<Boolean>,
        successText: StringResource,
        failedText: StringResource
    ) {
        if(isWaiting.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(
                        top = 20.dp
                    )
                    .size(75.dp)
            )
            Text(
                modifier = Modifier
                    .padding(
                        top = 10.dp
                    ),
                text = stringResource(statusText),
                fontSize = 14.sp
            )
        } else {
            Image(
                modifier = Modifier
                    .size(125.dp),
                imageVector = if(isSuccessful.value)
                    Icons.Default.CheckCircle
                else
                    Icons.Default.Cancel,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = if(isSuccessful.value)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            )
            Text(
                text = stringResource(
                    if (isSuccessful.value)
                        successText
                    else
                        failedText
                ),
                fontSize = 14.sp
            )
        }
    }

    private fun navBack() {
        navigator.navigate(HOME_SCREEN)
    }

    private fun navToSplash() {
        navigator.navigate(SPLASH_SCREEN)
    }

}