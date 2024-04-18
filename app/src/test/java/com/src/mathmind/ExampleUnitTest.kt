package com.src.mathmind

import com.src.mathmind.models.GuessModel
import com.src.mathmind.models.ScoreModel
import com.src.mathmind.utils.Utility
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

private const val SCM_LENGTH_EXPECTATION = 32

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun feedBack_plus_isCorrect() {
        val feedback = Utility.generateFeedBack(listOf(1, 2, 3, 4), listOf(1, 2, 4, 0), null)
        Assert.assertEquals(feedback[0], 2)
    }

    @Test
    fun feedBack_minus_isCorrect() {
        val feedback = Utility.generateFeedBack(listOf(1, 2, 3, 4), listOf(1, 2, 4, 0), null)
        Assert.assertEquals(feedback[1], 1)
    }

    @Test
    fun numArray_isCorrect() {
        val a = Utility.numToArray(2345)
        Assert.assertEquals(a, listOf(2, 3, 4, 5))
    }

    @Test
    fun arrayToNum_isCorrect() {
        val a = Utility.arrayToNum(intArrayOf(1, 2, 3, 4))
        Assert.assertEquals(a, 1234)
    }

    @Test()
    fun email_isWrong_AT_sign_missed() {

        Assert.assertThrows(IllegalArgumentException::class.java) {
            Utility.checkEmail("test.com")
        }
    }

    @Test()
    fun email_isCorrect() {
        val testEmailValue = "test@email.com"

        val email = Utility.checkEmail(testEmailValue)
        Assert.assertEquals(email, testEmailValue)
    }

    @Test
    fun evaluateNumber_feedback_isCorrect_for_matching_numbers() {
        val guessmodel = GuessModel(1234, 0, 0)
        val feedBack = Utility.evaluateNumber(1234, guessmodel)
        Assert.assertEquals(feedBack, mutableListOf(4, 0))
    }

    @Test
    fun evaluateNumber_data_isCorrect_for_matching_numbers() {
        val guessmodel = GuessModel(1234, 0, 0)
        Utility.evaluateNumber(1234, guessmodel)
        Assert.assertTrue(guessmodel.placedNumber == 4 && guessmodel.notPlacedNumber == 0)
    }

    @Test
    fun evaluateNumber_feedback_isCorrect_for_NONmatching_numbers() {
        val guessmodel = GuessModel(1234, 0, 0)
        val feedBack = Utility.evaluateNumber(4237, guessmodel)
        Assert.assertEquals(feedBack, mutableListOf(2, 1))
    }

    @Test
    fun evaluateNumber_data_isCorrect_for_NONmatching_numbers() {
        val guessmodel = GuessModel(1234, 0, 0)
        Utility.evaluateNumber(4237, guessmodel)
        Assert.assertTrue(guessmodel.placedNumber == 2 && guessmodel.notPlacedNumber == 1)
    }

    @Test
    fun returninguserNameLenght_11() {
        val scoreModel = ScoreModel("userTst", 123, LocalDate.now())
        Assert.assertEquals(SCM_LENGTH_EXPECTATION, scoreModel.toString().length)

    }

    @Test
    fun returninguserNameLenght_13() {
        val scoreModel = ScoreModel("a".repeat(13), 123, LocalDate.now())
        Assert.assertEquals(SCM_LENGTH_EXPECTATION, scoreModel.toString().length)

    }

    @Test
    fun returninguserNameLenght_12() {
        val scoreModel = ScoreModel("a".repeat(12), 123, LocalDate.now())
        Assert.assertEquals(SCM_LENGTH_EXPECTATION, scoreModel.toString().length)

    }

    @Test
    fun returninguserNameLenght__() {
        val scoreModel = ScoreModel("", 123, LocalDate.now())
        Assert.assertEquals(SCM_LENGTH_EXPECTATION, scoreModel.toString().length)

    }
}