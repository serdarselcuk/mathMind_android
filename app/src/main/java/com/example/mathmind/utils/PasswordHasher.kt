package com.example.mathmind.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object PasswordHasher {
    fun hashPassword(password: String, salt: String): String? {
        val saltedPassword = password + salt
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hashedBytes = md.digest(saltedPassword.toByteArray())

            // Convert hashed bytes to hexadecimal format
            val stringBuilder = StringBuilder()
            for (b in hashedBytes) {
                stringBuilder.append(String.format("%02x", b))
            }
            stringBuilder.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }
}