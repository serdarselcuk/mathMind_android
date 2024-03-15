package com.src.mathmind.ui.login

import com.src.mathmind.models.UserModel
import com.src.mathmind.service.CallService
import com.src.mathmind.utils.PasswordHasher
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginActivity {

//    suspend fun getValidatedUser(userName: String, password: String): UserModel? {
//        val user: UserModel? = LoginActivity().getUserValidated(userName)
//        return if (user != null && LoginActivity()
//                .validatePassword(user.password, password, user.hashCode)) {
//            user
//        } else {
//            null
//        }
//    }
//    suspend fun getUserValidated(username: String): UserModel? {
//        return suspendCoroutine { continuation ->
//            CallService().getUser(username) { serviceResponse ->
//                if (serviceResponse != null) {
//                    println("${serviceResponse.data?.firstName} found")
//                    continuation.resume(serviceResponse.data)
//                } else {
//                    continuation.resume(null)
//                }
//            }
//        }
//    }
//
//    fun validatePassword(cryptPass: String, password: String, hash: String): Boolean {
//        return cryptPass == PasswordHasher.hashPassword(password, hash)
//    }
//

}