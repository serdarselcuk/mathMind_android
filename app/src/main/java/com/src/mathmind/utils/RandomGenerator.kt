package com.src.mathmind.utils

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

    fun generateRandomUniqueDigits(size:Int, digits: MutableSet<Int> = mutableSetOf()):Int {
        var result = 0
        repeat(size) {
            var digit: Int

        // if it is first digit it can not be 0. Also we are not allowing duplicated numbers
            do {
                digit = secureRandom.nextInt(10)
            } while ((result < 10 && digit == 0) || digits.contains(digit))

            digits.add(digit)
            result = result * 10 + digit
        }
        println("number kept : "+result)
        return result
    }
}