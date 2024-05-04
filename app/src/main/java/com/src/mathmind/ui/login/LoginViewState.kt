package com.src.mathmind.ui.login

sealed class LoginViewState {
    object ValidationSuccess : LoginViewState()

    object LoggedOut : LoginViewState()
    data class ValidationError(val message: String) : LoginViewState()
    data class Error(val message: String) : LoginViewState()
}
