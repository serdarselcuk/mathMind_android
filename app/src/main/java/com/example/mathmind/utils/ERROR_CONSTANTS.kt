package com.example.mathmind.utils

class ERROR_CONSTANTS {
    companion object{
        const val REQUIRED = "Required field"
        const val USER_EXISTS = "User exists. Please try another name"
        const val SERVICE_ERROR = "Service Error"
        const val PASSWORD_EMPTY = "Please provide a password"
        const val EMAIL_EMPTY = "Please provide an Email"
        const val USERNAME_EMPTY = "Please provide a user name"
        const val LEAST_LENGTH = "Password must have at least eight characters!"
        const val WHITESPACE = "Password must not contain whitespace!"
        const val DIGIT = "Password must contain at least one digit!"
        const val UPPER = "Password must have at least one uppercase letter!"
        const val LOWER = "Password must have at least one lowercase letter!"
        const val SPECIAL =
            "Password must have at least one special character, such as: _%-=+#@"
        const val INVALID_EMAIL = "Invalid email format"
        const val ENTER_PASSWORD = "Please enter password"
        const val CONFIRM_PASSWORD = "Please confirm password"
        const val PASSWORDS_NOT_MATCHING = "Passwords are not matching"

    }
}