package com.tecknobit.neutron.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.intl.Locale.Companion.current
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.neutron.screens.Screen.Companion.HOME_SCREEN
import com.tecknobit.neutron.screens.navigation.Splashscreen.Companion.localUser
import com.tecknobit.neutron.ui.navigator
import com.tecknobit.neutroncore.helpers.Endpoints.BASE_ENDPOINT
import com.tecknobit.neutroncore.helpers.InputValidator.*
import com.tecknobit.neutroncore.records.User.*

class ConnectViewModel(
    snackbarHostState: SnackbarHostState
) : NeutronViewModel(
    snackbarHostState = snackbarHostState
) {

    lateinit var isSignUp: MutableState<Boolean>

    lateinit var storeDataOnline: MutableState<Boolean>

    lateinit var showQrCodeLogin: MutableState<Boolean>

    lateinit var host: MutableState<String>

    lateinit var hostError: MutableState<Boolean>

    lateinit var serverSecret: MutableState<String>

    lateinit var serverSecretError: MutableState<Boolean>

    lateinit var name: MutableState<String>

    lateinit var nameError: MutableState<Boolean>

    lateinit var surname: MutableState<String>

    lateinit var surnameError: MutableState<Boolean>

    lateinit var email: MutableState<String>

    lateinit var emailError: MutableState<Boolean>

    lateinit var password: MutableState<String>

    lateinit var passwordError: MutableState<Boolean>

    fun auth() {
        if (isSignUp.value)
            signUp()
        else
            signIn()
    }

    private fun signUp() {
        if (signUpFormIsValid()) {
            if (storeDataOnline.value) {
                val currentLanguageTag = current.toLanguageTag().substringBefore("-")
                var language = LANGUAGES_SUPPORTED[currentLanguageTag]
                language = if (language == null)
                    DEFAULT_LANGUAGE
                else
                    currentLanguageTag
                requester.changeHost(host.value + BASE_ENDPOINT)
                requester.sendRequest(
                    request = {
                        requester.signUp(
                            serverSecret = serverSecret.value,
                            name = name.value,
                            surname = surname.value,
                            email = email.value,
                            password = password.value,
                            language = language
                        )
                    },
                    onSuccess = { response ->
                        launchApp(
                            name = name.value,
                            surname = surname.value,
                            language = language,
                            response = response
                        )
                    },
                    onFailure = { showSnack(it) }
                )
            } else {
                // TODO: LOCAL SIGN UP
            }
        }
    }

    private fun signUpFormIsValid(): Boolean {
        var isValid: Boolean
        if (storeDataOnline.value) {
            isValid = isHostValid(host.value)
            if (!isValid) {
                hostError.value = true
                return false
            }
        }
        if (storeDataOnline.value) {
            isValid = isServerSecretValid(serverSecret.value)
            if (!isValid) {
                serverSecretError.value = true
                return false
            }
        }
        isValid = isNameValid(name.value)
        if (!isValid) {
            nameError.value = true
            return false
        }
        isValid = isSurnameValid(surname.value)
        if (!isValid) {
            surnameError.value = true
            return false
        }
        isValid = isEmailValid(email.value)
        if (!isValid) {
            emailError.value = true
            return false
        }
        isValid = isPasswordValid(password.value)
        if (!isValid) {
            passwordError.value = true
            return false
        }
        return true
    }

    private fun signIn() {
        if (signInFormIsValid()) {
            if (storeDataOnline.value) {
                requester.changeHost(host.value + BASE_ENDPOINT)
                requester.sendRequest(
                    request = {
                        requester.signIn(
                            email = email.value,
                            password = password.value
                        )
                    },
                    onSuccess = { response ->
                        launchApp(
                            name = response.getString(NAME_KEY),
                            surname = response.getString(SURNAME_KEY),
                            language = response.getString(LANGUAGE_KEY),
                            response = response
                        )
                    },
                    onFailure = { showSnack(it) }
                )
            } else {
                // TODO: EXECUTE THE LOCAL SIGN IN
            }
        }
    }

    private fun signInFormIsValid(): Boolean {
        var isValid: Boolean
        if (storeDataOnline.value) {
            isValid = isHostValid(host.value)
            if (!isValid) {
                hostError.value = true
                return false
            }
        }
        isValid = isEmailValid(email.value)
        if (!isValid) {
            emailError.value = true
            return false
        }
        isValid = isPasswordValid(password.value)
        if (!isValid) {
            passwordError.value = true
            return false
        }
        return true
    }

    private fun launchApp(
        response: JsonHelper,
        name: String,
        surname: String,
        language: String
    ) {
        requester.setUserCredentials(
            userId = response.getString(IDENTIFIER_KEY),
            userToken = response.getString(TOKEN_KEY)
        )
        localUser.insertNewUser(
            host.value,
            name,
            surname,
            email.value,
            password.value,
            language,
            response
        )
        navigator.navigate(HOME_SCREEN)
    }

}