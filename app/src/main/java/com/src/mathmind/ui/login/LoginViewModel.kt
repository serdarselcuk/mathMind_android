package com.src.mathmind.ui.login

import android.content.SharedPreferences
import android.widget.TextView
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
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel : ViewModel() {

    private var _userName = MutableLiveData<String>()
    private var _password = MutableLiveData<String>()
    val userName: LiveData<String> = _userName
    val password: LiveData<String> = _password
    private val _loginViewState = MutableLiveData<LoginViewState>()
    val loginViewState: LiveData<LoginViewState> = _loginViewState

    fun validateUser(callService: CallService, sharedEdit: SharedPreferences?) {

        viewModelScope.launch {
            try {
                val user: UserModel? = userName.value?.let {it1 ->
                    password.value?.let {it2 ->
                       validateCredentials(it1, it2, callService)
                    }
                }

                if (user != null) {
                    sharedEdit!!.edit()?.putString("userName", user.userName)?.apply()
                    // shared data can be accessed from any class
                    _loginViewState.value = LoginViewState.ValidationSuccess
                } else {
                    _loginViewState.value = LoginViewState.ValidationError(ERROR_CONSTANTS.CREDENTIALS_VALIDATION_FAILURE)
                }
            } catch (e: Exception) {
                _loginViewState.value = LoginViewState.Error("Something went wrong")
            }
        }
    }

    private suspend fun validateCredentials(userName: String, password: String, callService: CallService): UserModel? {
        val user: UserModel? = getUserValidated(userName, callService)
        val successLogin = user != null && validatePassword(user.password, password, user.hashCode)
        return if (successLogin) user
        else null
    }

    private suspend fun getUserValidated(username: String,  callService: CallService): UserModel? {
        return suspendCoroutine { continuation ->
            callService.getUser(username) { serviceResponse ->
                println("${serviceResponse.data?.firstName} found")
                continuation.resume(serviceResponse.data)
            }
        }
    }

    private fun validatePassword(cryptPass: String, password: String, hash: String): Boolean {
        return cryptPass == PasswordHasher.hashPassword(password, hash)
    }

    fun setUserName(textView: TextView): LiveData<String> {
        if (textView.text.isBlank())
            textView.error = ERROR_CONSTANTS.PROVIDE_USER_NAME
        else
            _userName.value = textView.text.toString()
        return userName
    }

    fun setPassword(textView: TextView): LiveData<String> {
        if (textView.text.isBlank())
            textView.error = ERROR_CONSTANTS.PROVIDE_PASSWORD
        else
            _password.value = textView.text.toString()
        return password
    }

    fun clear(): Boolean {
        _userName.value = ""
        _password.value = ""
        _loginViewState.value = LoginViewState.LoggedOut

        return userName.value == null && password.value == null && _loginViewState.value is LoginViewState.LoggedOut
    }

}