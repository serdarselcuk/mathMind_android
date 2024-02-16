package com.example.mathmind.ui.login

class LoginActivity {
    private val password_validation:Regex = Regex(
        "^(?=(.*[a-z]))" +//   1 lower case
                "(?=(.*[A-Z]))" +//   1 capital case
                "(?=.*[?!@#*%^&-+])" +//    1 special char
                "(?=(.*\\d))" +//    1 digit
                "[a-zA-Z?!@#*%^&-+\\d]{8}$")// 8 character

    fun validatePassword(string: String):Boolean{
        return password_validation.matches(string)
    }

    fun validateUser(name:String, password:String):String{
        var authToken:String= getAuthToken(name)
//      TODO() implement throwing error when validation fails
        return if(validatePassword(password)&&decryptAutToken(authToken,password))  "userName" else ""

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