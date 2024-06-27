package com.tecknobit.neutron.screens.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The **ProfileScreen** class is the screen where the user manage his/her profile account
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see Screen
 */
class ProfileScreen: Screen() {

    /**
     * *theme* -> the current user's theme
     */
    private lateinit var theme: MutableState<ApplicationTheme>

    /**
     * *viewModel* -> the support view model to manage the requests to the backend
     */
    private val viewModel = ProfileViewModel(
        snackbarHostState = snackbarHostState
    )

    /**
     * **fileType** -> list of allowed image types
     */
    private val fileType = listOf("jpg", "png", "jpeg")

    /**
     * Function to show the content of the screen
     *
     * No-any params required
     */
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
                                        mustBeInLowerCase = true,
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

    /**
     * Function to display a specific info details of the user
     *
     * @param header: the header of the info to display
     * @param info: the info details value to display
     * @param buttonText: the text of the setting button
     * @param onClick: the action to execute when the [buttonText] has been clicked
     */
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

    /**
     * Function to allow the user to change the current language setting
     *
     * @param changeLanguage: the state whether display this section
     */
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

    /**
     * Function to allow the user to change the current currency setting
     *
     * @param changeCurrency: the state whether display this section
     * @param currencyValue: the current value of the currency used by the user
     */
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

    /**
     * Function to allow the user to change the current theme setting
     *
     * @param changeTheme: the state whether display this section
     */
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

    /**
     * Function to allow the user to change a current setting
     *
     * @param showModal: the state whether display the [ModalBottomSheet]
     * @param sheetState: the state to apply to the [ModalBottomSheet]
     * @param onDismissRequest: the action to execute when the the [ModalBottomSheet] has been dismissed
     * @param content: the content to display
     */
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

    /**
     * Function to execute the back navigation from the current activity to the previous activity
     *
     * No-any params required
     */
    private fun navBack() {
        navigator.navigate(HOME_SCREEN)
    }

    /**
     * Function to execute the back navigation from the [Splashscreen] activity after user changed any
     * setting which required the refresh of the [localUser]
     *
     * No-any params required
     */
    private fun navToSplash() {
        navigator.navigate(SPLASH_SCREEN)
    }

}