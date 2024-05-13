package com.src.mathmind.ui.feedbacker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.src.mathmind.models.FeedBackData
import com.src.mathmind.models.GuessModel
import com.src.mathmind.utils.LogTag
import com.src.mathmind.utils.ERROR_CONSTANTS
import com.src.mathmind.utils.Utility
import java.util.stream.Collectors
import kotlin.random.Random

class FeedBackerViewModel : ViewModel() {

    companion object {
        private const val PLUS = "plus"
        private const val MINUS = "minus"
        private val usedNumberSet = mutableSetOf<Int>()
        private val possibleNumbers = mutableListOf<Int>()
    }

    private val _minus = MutableLiveData<Int>().apply { value = 0 }
    val minus: LiveData<Int> = _minus

    private val _plus = MutableLiveData<Int>().apply { value = 0 }
    val plus: LiveData<Int> = _plus

    private val _guessNum = MutableLiveData<Int>().apply { value = 0 }
    val guessNum: LiveData<Int> = _guessNum

    private val _feedBackHistory =
        MutableLiveData<MutableList<GuessModel>>().apply { value = mutableListOf() }
    val feedBackHistory: LiveData<MutableList<GuessModel>> = _feedBackHistory

    private val _removedNumbersHistory : MutableMap<Int, MutableList<Int>> = mutableMapOf()

    private val _feedBackStatus = MutableLiveData<FeedBackerViewState>()
        .apply { value = FeedBackerViewState.NEW }
    val feedBackStatus: LiveData<FeedBackerViewState> = _feedBackStatus


    fun setStatus(status: FeedBackerViewState) {
        Log.d(LogTag.FEEDBACKER_VIEW_MODEL, "Status is setting for $status")
        _feedBackStatus.value = status
    }

    fun getStatus(): FeedBackerViewState? {
        return feedBackStatus.value
    }

    fun increasePlus() {
        _plus.value = (_plus.value!! + 1) % 5
        balanceFeedback(MINUS)
    }

    fun increaseMinus() {
        _minus.value = (_minus.value!! + 1) % 5
        balanceFeedback(PLUS)
    }

    fun resetFeedback() {
        _plus.value = 0
        _minus.value = 0
    }

    //balance plus and minus on the other side
    private fun balanceFeedback(balanceOn: String) {
        val totalValue = minus.value!! + plus.value!!

        if (totalValue > 4) {
            when (balanceOn) {
                PLUS -> _plus.value = totalValue - _minus.value!! - 1
                MINUS -> _minus.value = totalValue - _plus.value!! - 1
            }
        }
    }

    fun guessNumber() {
        val guessModel = feedBackHistory.value?.lastIndex?.let { feedBackHistory.value?.get(it) }
        if (guessModel is GuessModel) {
            if (guessModel.feedBackData.placedNumber == 4) {
                setStatus(FeedBackerViewState.END)
                return
            }
            if (feedBackHistory.value?.size == 1) evaluateFirstFeedback(guessModel)
            else evaluateFeedback(guessModel)
        } else throw RuntimeException(ERROR_CONSTANTS.GUESS_MODEL_NOT_FOUND)

        if(possibleNumbers.size >0)
            guessNumber(possibleNumbers[Random.nextInt(possibleNumbers.size)])
        else
            throw RuntimeException(ERROR_CONSTANTS.WRONG_FEEDBACK)
    }

    private fun evaluateFirstFeedback(guessModel: GuessModel) {
        val guessedNumberArray = Utility.numToArray(guessModel.guessedNumber)
        for (num in 1023..9876) {
            val numArray = Utility.numToArray(num)
            if (numArray.toSet().size == 4) {
                val feedback = Utility.generateFeedBack(guessedNumberArray, numArray, null)
                if(feedback == guessModel.feedBackData)
                    possibleNumbers.add(num)
            }
        }
        Log.d(LogTag.FEEDBACKER_VIEW_MODEL, "Possible number size: ${possibleNumbers.size}")
    }

    private fun evaluateFeedback(guessModel: GuessModel) {
        Log.d(LogTag.FEEDBACKER_VIEW_MODEL, "Possible number size: ${possibleNumbers.size}")
        val guessedNumberArray = Utility.numToArray(guessModel.guessedNumber)
        val numbersToRemove = mutableListOf<Int>()
        for (possibleNum in possibleNumbers) {
            val possibleNumArray = Utility.numToArray(possibleNum)
            val feedback = Utility.generateFeedBack(possibleNumArray, guessedNumberArray, null)
            if (feedback != guessModel.feedBackData)
                numbersToRemove.add(possibleNum)
        }

        possibleNumbers.removeAll(numbersToRemove)
        _removedNumbersHistory[guessModel.guessedNumber] = numbersToRemove
        Log.d(
            LogTag.FEEDBACKER_VIEW_MODEL,
            "Possible number size: ${possibleNumbers.size}")
    }

    fun guessNumber(number: Int) {
        setGuessNumber(number)
        setStatus(FeedBackerViewState.WAITING)
    }

    private fun setGuessNumber(number: Int) {
        Log.d(LogTag.FEEDBACKER_VIEW_MODEL, "GuessNumber added ${guessNum.value}")
        if (usedNumberSet.size < 10) {
            for (i in Utility.numToArray(number)) {
                usedNumberSet.add(i)
            }
        }
        _guessNum.value = number
    }

    fun updateFeedbackHistory() {
        Log.d(LogTag.FEEDBACKER_VIEW_MODEL, "Guess model added for history ${guessNum.value}")
        _feedBackHistory.value?.add(
            GuessModel(
                guessNum.value!!,
                FeedBackData(
                    plus.value!!,
                    minus.value!!
                )
            )
        )
    }

    fun undoFeedBacks(i:Int){
//       if i = 1 we will undo only the last feedback
        val indexToStartRemove= if(i==-1) feedBackHistory.value?.size?.minus(1)!!
        else i
        val moveBackToGuessNumber = _feedBackHistory.value!![indexToStartRemove] .guessedNumber
        val historyToBeRemoved = _feedBackHistory.value?.stream()?.filter{ data ->
            _feedBackHistory.value!!.indexOf(data) >= indexToStartRemove}?.collect(Collectors.toList())
//        if history found
        if(historyToBeRemoved is List<GuessModel>) {
//            each history has removed some guessable numbers. adding them back and removing from history
            historyToBeRemoved.forEach{
                undoPossibleNumbers(it.guessedNumber)
            }
//            removing history of feedback
            _feedBackHistory.value?.removeAll(historyToBeRemoved)
            guessNumber(moveBackToGuessNumber)
        }
    }

    private fun undoPossibleNumbers(guessedNumbers:Int){
        _removedNumbersHistory[guessedNumbers]
            ?.let { it1 -> possibleNumbers.addAll(it1) }
        _removedNumbersHistory.remove(guessedNumbers)
    }

}