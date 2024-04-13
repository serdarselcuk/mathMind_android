package com.src.mathmind.ui.guesser

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.src.mathmind.R
import com.src.mathmind.models.GuessModel
import com.src.mathmind.utils.RandomGenerator
import com.src.mathmind.utils.Utility

class GuessViewModel : ViewModel() {

    // LiveData to hold the list of guessed numbers
    var _guessed_number_list = MutableLiveData<MutableList<GuessModel>>().apply { value = mutableListOf() }
    var guessed_number_list: LiveData<MutableList<GuessModel>> = _guessed_number_list

    // Generate a random number for guessing
    private val numberKept = RandomGenerator().generateRandomUniqueDigits(4)

    // Function to validate the input cells
    fun validateCells(numCellArray: Array<EditText>): IntArray? {
        val iterateCells: Iterator<EditText> = numCellArray.iterator()
        val guessed_number = IntArray(4) { -1 }
        var i = 0
        while (iterateCells.hasNext()) {
            val cell: EditText = iterateCells.next()
            val number = if (!cell.text.isNullOrBlank()) cell.text.toString().toInt() else -1
            // Validation for input numbers
            if ((i == 0 && number == 0) || number < 0 || guessed_number.contains(number)) {
                cell.requestFocus()
                return null
            } else {
                guessed_number[i++] = number
            }
        }
        println(" guessed_number is ${guessed_number.contentToString()}")
        return guessed_number
    }

    // Function to update the count of guessed numbers
    fun updateGuessCount(context: Context, guessHeader: TextView) {
        if (guessed_number_list.value!!.size > 0) {
            val countText = context.getString(R.string.numbers_count, guessed_number_list.value!!.size)
            val headerText = context.getString(R.string.guessed_numbers_list_header)
            val updatedHeader = "$headerText $countText"
            guessHeader.text = updatedHeader
        }
    }

    // Function to start the evaluation of guessed numbers
    fun startEvaluation(numberArray: IntArray): Any? {
        val data = GuessModel(
            Utility.arrayToNum(numberArray), // guessed number
            0,//correctly placed number
            0 // numbers on wrong place
        )
        Utility.evaluateNumber(numberKept, data) // decide about feedback & update data object
        // Ensure _guessed_number_list is not null
        if (guessed_number_list.value == null) _guessed_number_list.value = mutableListOf()
        return if (_guessed_number_list.value?.contains(data) == true) {
            data
        } else if (data.placedNumber == 4) {
            null
        } else {
            guessed_number_list.value!!.add(data)
            guessed_number_list.value?.size
        }
    }

}
