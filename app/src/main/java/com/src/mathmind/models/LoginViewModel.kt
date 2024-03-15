package com.src.mathmind.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.src.mathmind.service.CallService
import com.src.mathmind.ui.login.LoginActivity
import com.src.mathmind.ui.login.LoginViewState
import com.src.mathmind.utils.PasswordHasher
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel : ViewModel() {

    private val _loginViewState = MutableLiveData<LoginViewState>()
    val loginViewState: LiveData<LoginViewState> = _loginViewState

    fun validateUser(userName: String, password: String) {
        viewModelScope.launch {
            try {
                val user: UserModel? = getValidatedUser(userName, password)
                if (user != null) {
                    _loginViewState.value = LoginViewState.ValidationSuccess
                } else {
                    _loginViewState.value = LoginViewState.ValidationError("User or Password is not found")
                }
            } catch (e: Exception) {
                _loginViewState.value = LoginViewState.Error("Something went wrong")
            }
        }
    }

    suspend fun getValidatedUser(userName: String, password: String): UserModel? {
        val user: UserModel? = LoginActivity().getUserValidated(userName)
        return if (user != null && LoginActivity()
                .validatePassword(user.password, password, user.hashCode)) {
            user
        } else {
            null
        }
    }
    suspend fun getUserValidated(username: String): UserModel? {
        return suspendCoroutine { continuation ->
            CallService().getUser(username) { user ->
                if (user != null) {
                    println("${user.firstName} found")
                    continuation.resume(user)
                } else {
                    continuation.resume(null)
                }
            }
        }
    }

    fun validatePassword(cryptPass: String, password: String, hash: String): Boolean {
        return cryptPass == PasswordHasher.hashPassword(password, hash)
    }
}