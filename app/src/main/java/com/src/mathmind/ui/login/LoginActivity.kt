package com.src.mathmind.ui.login

import com.src.mathmind.models.UserModel
import com.src.mathmind.service.CallService
import com.src.mathmind.utils.PasswordHasher
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginActivity {

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