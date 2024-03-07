package com.src.mathmind.ui.login

class LoginActivity {


    fun validateUser(name:String, password:String):String{
        var authToken:String= getAuthToken(name)
//      TODO() implement throwing error when validation fails
        return if(true) "userName" else ""

    }

    private fun getAuthToken(username: String): String {
//        TODO() = implement service connection to get user token
        return "token"
    }

    private fun decryptAutToken(name:String, password:String): Boolean {
//        TODO() = implement token resolution and return user name
        return true
    }

}