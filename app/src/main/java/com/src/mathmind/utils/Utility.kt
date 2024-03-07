package com.src.mathmind.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.src.mathmind.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import com.src.mathmind.utils.ERROR_CONSTANTS.Companion as ERROR

class Utility {

    companion object {
        fun arrayToNum(array: IntArray): Int {
            return "${array[0]}${array[1]}${array[2]}${array[3]}".toInt()
        }

        suspend fun highlightElement(
            obj: View,
            color: Int = R.color.red_highlight,
            context: Context?,
            flash: Boolean = true,
            repeatCount: Int = 5,
            timeOut: Long = 50
        ) {
            val highlightingColor: Int? = context?.let { ContextCompat.getColor(it, color) }
            val background = obj.background
            var backgroundColor = 0
            if (background is ColorDrawable) {
                // Get the color of the ColorDrawable
                backgroundColor = background.color
                println("Background color: $color")
            } else {
                println("View background is not a ColorDrawable")
            }
            println("element found to highlight: ${obj.id}")
            var _repeatCount = 1
            withContext(Dispatchers.Main) {
                //if background color not found flushing is not possible
                if (flash && backgroundColor != 0) _repeatCount = repeatCount
                repeat(_repeatCount) {
                    if (highlightingColor is Int) obj.setBackgroundColor(highlightingColor)
                    if (backgroundColor != 0) {
                        delay(timeOut)
                        obj.setBackgroundColor(backgroundColor)
                        delay(timeOut)
                    }
                }
            }
        }


        fun checkEmail(email: String): String {
            val emailRegex =
                "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            val pattern = Pattern.compile(emailRegex)
            val matcher = pattern.matcher(email)

            return if (matcher.matches()) {
                email
            } else {
                throw IllegalArgumentException(ERROR.INVALID_EMAIL)
            }
        }

        fun validatePassword(password: String):String{

            val validationMap = mapOf(
                ERROR.LEAST_LENGTH to (password.length >= 8),
                ERROR.WHITESPACE to (password.none { it.isWhitespace() }),
                ERROR.DIGIT to (password.any { it.isDigit() }),
                ERROR.UPPER to (password.any { it.isUpperCase() }),
                ERROR.SPECIAL to (password.any { !it.isLetterOrDigit() }),
                ERROR.LOWER to (password.any { it.isLowerCase() })
            )

            //list of failures
            val errorList = validationMap.filter { !it.value }.keys

            //if any failures create error message and throw error
            if( errorList.isNotEmpty())
                throw IllegalArgumentException(
                    errorList.joinToString("\n","Error list:\n")
                )

            return password
        }
    }
}
