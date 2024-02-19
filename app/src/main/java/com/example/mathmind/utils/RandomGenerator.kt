package com.example.mathmind.utils

import java.security.SecureRandom
import java.util.Base64

class RandomGenerator(
    val secureRandom: SecureRandom = SecureRandom()
) {

    lateinit var array: ByteArray

    fun generateSalt(): String {
        array = ByteArray(16)
        secureRandom.nextBytes(array)
        val salt = array
        return Base64.getEncoder().encodeToString(salt);
    }

    fun generateRandom4uniqueDigit(size:Int):Int {
        var result = 0
        var usedDigits = mutableSetOf<Int>()

        repeat(size) {
            var digit: Int
            do {
                digit = secureRandom.nextInt(10)
            } while (usedDigits.contains(digit))

            usedDigits.add(digit)
            result = result * 10 + digit
        }

        return result
    }
}