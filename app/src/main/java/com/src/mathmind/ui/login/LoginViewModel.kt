package com.src.mathmind.ui.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.src.mathmind.models.LoginViewState
import com.src.mathmind.models.UserModel
import com.src.mathmind.service.CallService
import com.src.mathmind.utils.ERROR_CONSTANTS
import com.src.mathmind.utils.IdlingTool
import com.src.mathmind.utils.PasswordHasher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel : ViewModel() {

    private val _loginViewState = MutableLiveData<LoginViewState>()
    val loginViewState: LiveData<LoginViewState> = _loginViewState

    fun validateUser(userName: String, password: String,  idlingTool: IdlingTool?) {
        viewModelScope.launch {
            try {
                val user: UserModel? = getValidatedUser(userName, password, idlingTool)
                if (user != null) {
                    _loginViewState.value = LoginViewState.ValidationSuccess
                } else {
                    _loginViewState.value = LoginViewState.ValidationError(ERROR_CONSTANTS.CREDENTIALS_VALIDATION_FAILURE)
                }
            } catch (e: Exception) {
                _loginViewState.value = LoginViewState.Error("Something went wrong")
            }
        }
    }

    private suspend fun getValidatedUser(userName: String, password: String,  idlingtool: IdlingTool?): UserModel? {
        val user: UserModel? = getUserValidated(userName, idlingtool)
        return if (user != null && validatePassword(user.password, password, user.hashCode)) user
        else null
    }

    private suspend fun getUserValidated(username: String,  idlingtool: IdlingTool?): UserModel? {
        return suspendCoroutine { continuation ->
            CallService().getUser(username, idlingtool) { serviceResponse ->
                println("${serviceResponse.data?.firstName} found")
                continuation.resume(serviceResponse.data)
            }
        }
    }

    private fun validatePassword(cryptPass: String, password: String, hash: String): Boolean {
        return cryptPass == PasswordHasher.hashPassword(password, hash)
    }

}