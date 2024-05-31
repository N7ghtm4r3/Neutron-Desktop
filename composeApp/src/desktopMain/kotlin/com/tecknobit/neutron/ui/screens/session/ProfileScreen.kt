package com.tecknobit.neutron.ui.screens.session

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.tecknobit.neutron.ui.*
import com.tecknobit.neutron.ui.screens.Screen
import com.tecknobit.neutron.ui.screens.navigation.Splashscreen.Companion.user
import com.tecknobit.neutron.ui.theme.NeutronTheme
import com.tecknobit.neutron.ui.theme.errorLight
import com.tecknobit.neutroncore.records.User.*
import com.tecknobit.neutroncore.records.User.ApplicationTheme.*
import com.tecknobit.neutroncore.records.User.UserStorage.Local
import com.tecknobit.neutroncore.records.User.UserStorage.Online
import kotlinx.coroutines.delay
import neutron.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.util.*

class ProfileScreen: Screen() {

    private lateinit var theme: MutableState<ApplicationTheme>

    private lateinit var hostLocalSignIn: MutableState<Boolean>

    private val currentStorageIsLocal = user.storage == Local

    /**
     * **fileType** -> list of allowed image types
     */
    private val fileType = listOf("jpg", "png", "jpeg")

    @Composable
    override fun ShowScreen() {
        theme = remember { mutableStateOf(user.theme) }
        var profilePic by remember { mutableStateOf(user.profilePic) }
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
                            // TODO: MAKE THE REQUEST THEN
                            navToSplash()
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
                            AsyncImage(
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
                            )
                            FilePicker(
                                show = pickProfilePic,
                                fileExtensions = fileType
                            ) { profilePicPath ->
                                if(profilePicPath != null) {
                                    // TODO: MAKE THE REQUEST THEN

                                    // TODO: TO REMOVE
                                    profilePic = "https://t4.ftcdn.net/jpg/03/86/82/73/360_F_386827376_uWOOhKGk6A4UVL5imUBt20Bh8cmODqzx.jpg"

                                    // TODO: TO USE THIS INSTEAD THE REAL PATH FROM THE REQUEST RESPONSE
                                    //  profilePic = PATH FROM THE REQUEST RESPONSE

                                    user.profilePic = profilePic
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        bottom = 16.dp
                                    )
                                    .fillMaxWidth(),
                                text = user.completeName,
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
                            var userEmail by remember { mutableStateOf(user.email) }
                            val newEmail = remember { mutableStateOf("") }
                            UserInfo(
                                header = Res.string.email,
                                info = userEmail,
                                onClick = { showChangeEmailAlert.value = true }
                            )
                            NeutronAlertDialog(
                                dismissAction = {
                                    newEmail.value = ""
                                    showChangeEmailAlert.value = false
                                },
                                icon = Icons.Default.Email,
                                show = showChangeEmailAlert,
                                title = Res.string.change_email,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = newEmail,
                                        label = Res.string.new_email
                                    )
                                },
                                confirmAction = {
                                    // TODO: MAKE THE REQUEST AND SAVE IN LOCAL THEN
                                    userEmail = newEmail.value
                                    user.email = userEmail
                                    showChangeEmailAlert.value = false
                                }
                            )
                            val showChangePasswordAlert = remember { mutableStateOf(false) }
                            val newPassword = remember { mutableStateOf("") }
                            var hiddenPassword by remember { mutableStateOf(true) }
                            UserInfo(
                                header = Res.string.password,
                                info = "****",
                                onClick = { showChangePasswordAlert.value = true }
                            )
                            NeutronAlertDialog(
                                dismissAction = {
                                    newPassword.value = ""
                                    showChangePasswordAlert.value = false
                                },
                                icon = Icons.Default.Password,
                                show = showChangePasswordAlert,
                                title = Res.string.change_password,
                                text = {
                                    NeutronOutlinedTextField(
                                        value = newPassword,
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
                                        )
                                    )
                                },
                                confirmAction = {
                                    // TODO: MAKE THE REQUEST AND SAVE IN LOCAL THEN
                                    showChangePasswordAlert.value = false
                                }
                            )
                            val changeLanguage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = Res.string.language,
                                info = LANGUAGES_SUPPORTED[user.language]!!,
                                onClick = { changeLanguage.value = true }
                            )
                            ChangeLanguage(
                                changeLanguage = changeLanguage
                            )
                            val changeCurrency = remember { mutableStateOf(false) }
                            val currency = remember { mutableStateOf(user.currency.isoName) }
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
                                info = user.theme.name,
                                buttonText = Res.string.change,
                                onClick = { changeTheme.value = true }
                            )
                            ChangeTheme(
                                changeTheme = changeTheme
                            )
                            val showChangeStorage = remember { mutableStateOf(false) }
                            UserInfo(
                                header = Res.string.storage_data,
                                info = user.storage.name,
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
                                    // TODO: MAKE THE OPE TO LOGOUT THEN
                                    navToSplash()
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
                            // TODO: MAKE THE REQUEST THEN
                            user.language = language
                            changeLanguage.value = false
                            navToSplash()
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
                        tint = if(user.language == language)
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
                            // TODO: MAKE THE REQUEST AND FETCH THE NEW CHANGE RATE THEN
                            user.currency = currency
                            currencyValue.value = currency.isoName
                            changeCurrency.value = false
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
                        tint = if(user.currency == currency)
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
                            // TODO: MAKE THE REQUEST THEN
                            user.theme = theme
                            changeTheme.value = false
                            this@ProfileScreen.theme.value = theme
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
                        tint = if(user.theme == theme)
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChangeStorage(
        changeStorage: MutableState<Boolean>
    ) {
        val hostAddress = remember { mutableStateOf("") }
        val serverSecret = remember { mutableStateOf("") }
        var isExecuting by remember { mutableStateOf(false) }
        val waiting = remember { mutableStateOf(true) }
        val success = remember { mutableStateOf(false) }
        val executeRequest = {
            isExecuting = true
            waiting.value = true
            success.value = false
            // TODO: MAKE THE REQUEST THEN
        }
        val resetLayout = {
            isExecuting = false
            hostAddress.value = ""
            serverSecret.value = ""
            changeStorage.value = false
            waiting.value = true
            success.value = false
        }
        ChangeInfo(
            showModal = changeStorage,
            sheetState = rememberModalBottomSheetState(
                confirmValueChange = { !isExecuting || success.value }
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
                if(!isExecuting) {
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
                            value = hostAddress,
                            label = Res.string.host_address,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )
                        NeutronOutlinedTextField(
                            modifier = Modifier
                                .width(300.dp),
                            value = serverSecret,
                            label = Res.string.server_secret
                        )
                    }
                } else {
                    // TODO: TO REMOVE
                    LaunchedEffect(key1 = waiting.value){
                        delay(3000L)
                        waiting.value = false
                        // TODO: TO REMOVE GET FROM THE REAL REQUEST RESPONSE
                        success.value = Random().nextBoolean()
                        // TODO: IF success = true STORE DATA
                    }
                    ResponseStatusUI(
                        isWaiting = waiting,
                        statusText = Res.string.transferring_data,
                        isSuccessful = success,
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
                    if(!isExecuting) {
                        TextButton(
                            onClick = { resetLayout.invoke() }
                        ) {
                            Text(
                                text = stringResource(
                                    if(isExecuting)
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
                                if(waiting.value) {
                                    // TODO: STOP THE TRANSFER THEN
                                    isExecuting = false
                                } else {
                                    if(success.value) {
                                        resetLayout.invoke()
                                        changeStorage.value = false
                                        user.storage = if(currentStorageIsLocal)
                                            Online
                                        else
                                            Local
                                        navToSplash()
                                    } else
                                        executeRequest.invoke()
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    if(waiting.value)
                                        Res.string.cancel
                                    else {
                                        if(success.value)
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