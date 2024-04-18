package com.src.mathmind.ui.guesser

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.src.mathmind.models.GuessModel
import com.src.mathmind.models.ScoreModel
import com.src.mathmind.service.CallService
import com.src.mathmind.utils.IdlingTool
import com.src.mathmind.utils.RandomGenerator
import com.src.mathmind.utils.Utility
import java.time.LocalDate

class GuessViewModel : ViewModel() {

    // LiveData to hold the list of guessed numbers
    private val _guessed_number_list =
        MutableLiveData<MutableList<GuessModel>>().apply { value = mutableListOf() }
    val guessed_number_list: LiveData<MutableList<GuessModel>> = _guessed_number_list
    private val _score = MutableLiveData<ScoreCalculus>().apply { value = ScoreCalculus() }
    val score: LiveData<ScoreCalculus> = _score

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
    fun getGuessCount(): Int {
        return guessed_number_list.value?.size ?: 0
    }

    // Function to start the evaluation of guessed numbers
    fun startEvaluation(numberArray: IntArray): Any? {
        val data = GuessModel(
            Utility.arrayToNum(numberArray), // guessed number
            0,//correctly placed number
            0 // numbers on wrong place
        )
        // Ensure _guessed_number_list is not null
        if (guessed_number_list.value == null) _guessed_number_list.value = mutableListOf()

        // if this number already guessed
        if (_guessed_number_list.value?.contains(data) == true) return data


        score.value?.increaseTurn()
        // decide about feedback & update data object
        Utility.evaluateNumber(numberKept, data, score.value)
        return if (data.placedNumber == 4) {// if all numbers matches will return null
//            update game end score
            guessed_number_list.value?.let { score.value?.setGameEndPoint() }
            null
        } else {// some feedback is in data
            guessed_number_list.value!!.add(data)
            guessed_number_list.value?.size
        }
    }

    fun saveScore(idlingTool: IdlingTool, userName: String?): Boolean {

        return if (userName != null) {
            score.value?.let { ScoreModel(userName, it.getPoint(), LocalDate.now()) }?.let {
                CallService().saveScore(it, idlingTool)
            }
            true
        } else {
            false
        }
    }

}

data class ScoreCalculus(
    var turn: Int = 0,
    var point: Double = 0.0,
    var position: MutableList<Boolean?> = MutableList(4) { null },
) {

    fun setGameEndPoint() {
        if (turn < 15) point += ((15 - turn) * 100)
        if (turn > 20) point -= (turn - 20)
    }

    /*
   true is indicating correct position
   false stands for wrong positioned digits
    */
    fun setPositionalPoint(positionalInfo: Int, correctPosition: Boolean) {

        if (correctPosition) {
            if (position[positionalInfo] != true) {
                point += (100 / turn)
                this.position[positionalInfo] = true
            }
        } else {
            if (position[positionalInfo] == null) {
                point += (50 / turn)
                this.position[positionalInfo] = false
            }
        }
    }

    fun increaseTurn(): ScoreCalculus {
        turn++
        return this
    }

    fun getPoint(): Int {
        return point.toInt()
    }
}
