package com.src.mathmind.ui.signOn

import android.accounts.AuthenticatorException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.src.mathmind.MainActivity
import com.src.mathmind.R
import com.src.mathmind.models.UserModel
import com.src.mathmind.service.CallService
import com.src.mathmind.utils.ERROR_CONSTANTS
import com.src.mathmind.utils.LogTag
import com.src.mathmind.utils.PasswordHasher
import com.src.mathmind.utils.RandomGenerator
import com.src.mathmind.utils.Utility
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SignOnViewModel : ViewModel() {

    private var _userName = MutableLiveData<String>().apply { value = "" }
    val userName: LiveData<String> = _userName

    private var _password = MutableLiveData<String>().apply { value = "" }
    val password: LiveData<String> = _password

    private var _firstName = MutableLiveData<String>().apply { value = "" }
    val firstName: LiveData<String> = _firstName

    private var _lastName = MutableLiveData<String>().apply { value = "" }
    val lastName: LiveData<String> = _lastName

    private var _email = MutableLiveData<String>().apply { value = "" }
    val email: LiveData<String> = _email

    private var _confirmPassword = MutableLiveData<Boolean>().apply { value = false }
    val confirmPassword: LiveData<Boolean> = _confirmPassword


    fun checkAllEmptyInputs(): MutableList<String> {

        val errorList = mutableListOf<String>()

        if (userName.value!!.isEmpty()) {
            errorList.add(ERROR_CONSTANTS.USERNAME_EMPTY)
        }

        if (firstName.value!!.isEmpty()) {
            errorList.add(ERROR_CONSTANTS.FIRST_NAME_EMPTY)
        }

        if (lastName.value!!.isEmpty()) {
            errorList.add(ERROR_CONSTANTS.LAST_NAME_EMPTY)
        }

        if (email.value!!.isEmpty()) {
            errorList.add(ERROR_CONSTANTS.EMAIL_EMPTY)
        }

        if (password.value!!.isEmpty()) {
            errorList.add(ERROR_CONSTANTS.PASSWORD_EMPTY)
        }

        if (!confirmPassword.value!!) {
            errorList.add(ERROR_CONSTANTS.CONFIRM_PASSWORD)
        }

        return errorList
    }

    fun getHashedPassword(): Map<String, String> {

        val hashKey = RandomGenerator().generateSalt()

        val password = password.value?.let {
            PasswordHasher.hashPassword(it, hashKey)
        } ?: throw AuthenticatorException(ERROR_CONSTANTS.SERVICE_ERROR)

        return mapOf(password to hashKey)
    }

    fun createUser(mainActivity: MainActivity, hashedCredentials: Map<String, String>) {
        val date = Utility.getCurrentDate(mainActivity.getString(R.string.date_pattern))
        return mainActivity.callService().saveUser(
            UserModel(
                null,
                userName.value.toString(),
                date,
                firstName.value.toString(),
                lastName.value.toString(),
                email.value.toString(),
                hashedCredentials.keys.first(),
                hashedCredentials.values.first()
            )
        ) { response ->
            if (response != null) {
                Log.e(LogTag.SIGN_ON_VIEW, "in callback $response")
                response.toString()
            } else {
                // Handle the case where an error occurred
                Log.e(
                    LogTag.SIGN_ON_VIEW,
                    "Service Error occurred. User not saved. Response: $response"
                )
                throw Error(ERROR_CONSTANTS.SERVICE_ERROR)
            }
        }
    }

    fun clearValues() {
        _email.value = ""
        _userName.value = ""
        _password.value = ""
        _confirmPassword.value = false
        _firstName.value = ""
        _lastName.value = ""
    }

    fun confirmPassword(string: String): Boolean? {
        return if (_password.value.isNullOrEmpty()) {
            setConfirmPassword(false)
            null
        } else if (string != password.value) {
            setConfirmPassword(false)
        } else {
            setConfirmPassword(true)
        }
    }

    fun setConfirmPassword(boolean: Boolean): Boolean {
        _confirmPassword.value = boolean
        return boolean
    }

    fun setPassword(string: String) {
        _password.value = string
    }

    fun validatePassword(passwordText: String): String? {

        val errorList = Utility.validatePassword(passwordText)
        return if (errorList.isEmpty()) {
            _password.value = passwordText
            null
        } else {
            val text = if (errorList.size == 1) "" else "Error list:\n"
            errorList.joinToString("\n", text)
        }
    }

    //    setting first and last name in same method
    fun setName(name: String, inputId: Int) {
        when (inputId) {
            R.id.firstNameInput -> _firstName.value = name
            R.id.lastNameInput -> _lastName.value = name
        }
    }

    suspend fun validateUserName(callService: CallService, userNameFieldText: String): String? {
        return suspendCancellableCoroutine { continuation ->
            callService.validateUserName(userNameFieldText) { userExists ->
                if (userExists != null) {
                    if (userExists) {
                        Log.d(LogTag.SIGN_ON_VIEW,"user name $userNameFieldText exists!")
                        continuation.resume(ERROR_CONSTANTS.USER_EXISTS)
                    } else {
                        setUserName(userNameFieldText)
                        continuation.resume(null)
                    }
                } else {
                    Log.d(LogTag.SIGN_ON_VIEW,"user name service failed!")
                    continuation.resume(ERROR_CONSTANTS.SERVICE_ERROR)
                }
            }
        }
    }

    fun setUserName(userName: String) {
        _userName.value = userName
    }

    fun setEMail(userName: String) {
        _userName.value = userName
    }

}