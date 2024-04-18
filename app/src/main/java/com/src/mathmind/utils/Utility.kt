package com.src.mathmind.utils

import com.src.mathmind.models.GuessModel
import com.src.mathmind.ui.guesser.ScoreCalculus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import com.src.mathmind.utils.ERROR_CONSTANTS.Companion as ERROR

class Utility {

    companion object {
        fun arrayToNum(array: IntArray): Int {
            return "${array[0]}${array[1]}${array[2]}${array[3]}".toInt()
        }

        fun numToArray(number: Int): MutableList<Int> {
            val digits = mutableListOf<Int>()
            var num = number

            // Extract digits until the number becomes zero
            while (num > 0) {
                // Extract the last digit and add it to the list
                val digit = num % 10
                digits.add(0, digit) // Add the digit at the beginning of the list
                num /= 10 // Remove the last digit
            }

            return digits
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

        fun getCurrentDate(pattern:String): String {
            // Define the desired date format
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern(pattern)
            return LocalDate.now().format(formatter)

        }

        /*returning an array of
        * placed number , not_placed number
        * also updating data
        * */
        fun evaluateNumber(numberKept: Int, data: GuessModel, scoreCalculus: ScoreCalculus? = null):MutableList<Int> {
            val feedBack: Array<Int> = if (data.guessedNumber == numberKept) {
                arrayOf(4,0)
            } else {
                generateFeedBack(
                    numToArray(numberKept),
                    numToArray(data.guessedNumber),
                    scoreCalculus
                )
            }
            data.placedNumber = feedBack[0]
            data.notPlacedNumber = feedBack[1]

            return mutableListOf( feedBack[0], feedBack[1] )
        }

        fun generateFeedBack(keptNumArray:List<Int>, numToCompare:List<Int>, scoreCalculus: ScoreCalculus?): Array<Int>{
            var placedNumber = 0
            var notPlacedNumber = 0

            numToCompare.forEachIndexed{index,digit->
                if (keptNumArray[index] == digit) {
                    scoreCalculus?.setPositionalPoint(index, true)
                    placedNumber++
                } else if (keptNumArray.contains(digit)) {
                    val positionOfDigit = keptNumArray.indexOf(digit)
                    scoreCalculus?.setPositionalPoint(positionOfDigit, false)
                    notPlacedNumber++
                }
            }

            return arrayOf(placedNumber, notPlacedNumber)
        }

    }
}
