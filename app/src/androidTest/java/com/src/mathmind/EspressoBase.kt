package com.src.mathmind

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf

open class EspressoBase {

    private val userName:String = "use_2"
    private val password:String = "Password1."

    fun validateDisplayedWithText(onView: Matcher<View?>, text: String): ViewInteraction {
        return onView(onView)
            .check(
                ViewAssertions.matches(
                    hasDescendant(
                        AllOf.allOf(
                            isDisplayed(),
                            withText(text)
                        )
                    )
                )
            )
    }

    fun guessNumber(number: String) {
        val digits = number.map { it.toString() }
        require(digits.size == 4) { "Number must be a 4-digit." }

        val numberIds = arrayOf(
            R.id.guessing_number_1,
            R.id.guessing_number_2,
            R.id.guessing_number_3,
            R.id.guessing_number_4
        )

        numberIds.forEachIndexed { index, id ->
            onView(withId(id)).perform(ViewActions.typeText(digits[index]))
        }

        onView(withId(R.id.guessing_button))
            .perform(ViewActions.click())
    }

    fun login() {
        onView(withId(R.id.textInputUserName))
            .perform(ViewActions.typeText(userName))
        onView(withId(R.id.editTextTextPassword))
            .perform(ViewActions.typeText(password))
        onView(withId(R.id.buttonLogin))
            .perform(ViewActions.click())
    }

    fun landOnGuesser() {
        onView(withId(R.id.guesser_button))
            .perform(ViewActions.click())
    }

    fun landOnFeedBacker() {
        onView(withId(R.id.feedbackerButton))
            .perform(ViewActions.click())
            onView(withText("OK")).perform(ViewActions.click())
    }
}


class IsIntegerInRangeMatcher(private val min: Int, private val max: Int) :
    TypeSafeMatcher<String>() {

    override fun matchesSafely(item: String?): Boolean {
        return item?.toIntOrNull() in min..max
    }

    override fun describeTo(description: Description?) {
        description?.appendText("an integer between $min and $max")
    }
}