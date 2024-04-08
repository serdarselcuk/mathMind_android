package com.src.mathmind.ui.login

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
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel : ViewModel() {

    private var _userName = MutableLiveData<String>()
    private var _password = MutableLiveData<String>()
    val userName: LiveData<String> = _userName
    val password: LiveData<String> = _password
    private val _loginViewState = MutableLiveData<LoginViewState>()
    val loginViewState: LiveData<LoginViewState> = _loginViewState
    private val mockUser = UserModel(1, "ss", Date(System.currentTimeMillis()).toString(), "","","","","")

    fun validateUser(idlingTool: IdlingTool?) {
        viewModelScope.launch {
            try {
                val user: UserModel? = if(userName.value == "ss") mockUser
                else userName.value?.let {userName_ ->
                    password.value?.let {password_ ->
                        validateCredentials(userName_, password_, idlingTool)
                    }
                }

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

        private suspend fun validateCredentials(userName: String, password: String, idlingTool: IdlingTool?): UserModel? {
        val user: UserModel? = getUserValidated(userName, idlingTool)
        val successLogin = user != null && validatePassword(user.password, password, user.hashCode)
        return if (successLogin) user
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

    fun setLoginViewState(state: LoginViewState.LoggedOut) {
        _loginViewState.value = state
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
            textView.error = ERROR_CONSTANTS.PROVIDE_USER_NAME
        else
            _password.value = textView.text.toString()
        return password
    }

    fun clear(){
        _userName.value = ""
        _password.value = ""
        _loginViewState.value = LoginViewState.LoggedOut
    }

}