package com.src.mathmind

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun user_able_to_see_guest_number_history() {
        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        login()
        landOnGuesser()
        guessNumber("1234")

        val element = withId(R.id.guesture_history_list)
        validateDisplayedWithText(element, "1234")

    }

    @Test
    fun user_able_to_get_feedback() {
        login()
        landOnGuesser()
        guessNumber("5678")

        onView(
            AllOf
                .allOf(
                    withId(R.id.guesture_history_list),
                    hasDescendant(
                        AllOf.allOf(
                            withId(R.id.not_placed_number),
                            withText(IsIntegerInRangeMatcher(0, 4))
                        )
                    ),
                    hasDescendant(
                        AllOf.allOf(
                            withId(R.id.placed_number),
                            withText(IsIntegerInRangeMatcher(0, 4))
                        )
                    )
                )
        ).check(matches(isDisplayed()))

    }

    private fun validateDisplayedWithText(onView: Matcher<View?>, text: String): ViewInteraction {
        return onView(onView)
            .check(
                matches(
                    hasDescendant(
                        AllOf.allOf(
                            isDisplayed(),
                            withText(text)
                        )
                    )
                )
            )

    }

    private fun guessNumber(number: String) {
        val digits = number.map { it.toString() }
        require(digits.size == 4) { "Number must be a 4-digit." }

        val numberIds = arrayOf(
            R.id.guessing_number_1,
            R.id.guessing_number_2,
            R.id.guessing_number_3,
            R.id.guessing_number_4
        )

        numberIds.forEachIndexed { index, id ->
            onView(withId(id)).perform(typeText(digits[index]))
        }

        onView(withId(R.id.guessing_button))
            .perform(click())
    }

    private fun login() {
        onView(withId(R.id.buttonLogin))
            .perform(click())
    }

    private fun landOnGuesser() {
        onView(withId(R.id.guesser_button))
            .perform(click())
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


