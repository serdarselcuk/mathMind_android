package com.src.mathmind

import android.provider.ContactsContract.Data
import com.src.mathmind.models.GuessModel
import com.src.mathmind.utils.Utility
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun feedBack_plus_isCorrect() {
        val feedback = Utility.generateFeedBack(listOf(1,2,3,4), listOf(1,2,4,0))
        assert(feedback[0]==2)
    }

    @Test
    fun feedBack_minus_isCorrect() {
        val feedback = Utility.generateFeedBack(listOf(1,2,3,4), listOf(1,2,4,0))
        assert(feedback[1]==1)
    }

    @Test
    fun numArray_isCorrect(){
        val a = Utility.numToArray(2345)
        assert(a == listOf(2,3,4,5))
    }

    @Test
    fun arrayToNum_isCorrect(){
        val a = Utility.arrayToNum(intArrayOf(1,2,3,4))
        assert(a== 1234)
    }

    @Test()
    fun email_isWrong_AT_sign_missed(){
        try {
            Utility.checkEmail("test.com")
        }catch (e:Exception ){
            assert(e is IllegalArgumentException)
        }

    }

    @Test()
    fun email_isCorrect(){
        val testEmailValue = "test@email.com"

        val email = Utility.checkEmail(testEmailValue)
        assert(email == testEmailValue)
    }

    @Test
    fun evaluateNumber_feedback_isCorrect_for_matching_numbers(){
        val guessmodel = GuessModel(1234, 0,0)
        val feedBack = Utility.evaluateNumber(1234, guessmodel)
        assert(feedBack == mutableListOf(4,0))
    }

    @Test
    fun evaluateNumber_data_isCorrect_for_matching_numbers(){
        val guessmodel = GuessModel(1234, 0,0)
        Utility.evaluateNumber(1234, guessmodel)
        assert(guessmodel.placedNumber == 4 && guessmodel.notPlacedNumber == 0)
    }

    @Test
    fun evaluateNumber_feedback_isCorrect_for_NONmatching_numbers(){
        val guessmodel = GuessModel(1234, 0,0)
        val feedBack = Utility.evaluateNumber(4237, guessmodel)
        assert(feedBack == mutableListOf(2,1))
    }

    @Test
    fun evaluateNumber_data_isCorrect_for_NONmatching_numbers(){
        val guessmodel = GuessModel(1234, 0,0)
        Utility.evaluateNumber(4237, guessmodel)
        assert(guessmodel.placedNumber == 2 && guessmodel.notPlacedNumber == 1)
    }
}