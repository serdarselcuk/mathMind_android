package com.src.mathmind

import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.src.mathmind.utils.LogTag
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class TestBase {

    private val userName: String = "use_2"
    private val password: String = "Password1."

    private lateinit var idlingResource: IdlingResource
//    private lateinit var appContext: Context

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    open fun setup() {
        activityRule.scenario.onActivity { activity ->
            idlingResource = activity.getIdlingTool()
            // To prove that the test fails, omit this call:
            IdlingRegistry.getInstance().register(idlingResource)
        }

        Log.d(LogTag.UI_TEST, "Setup")
    }

    @After
    fun tearsDown() {
        Log.d(LogTag.UI_TEST, "closing")
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

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

        arrayOf(
            R.id.guessing_number_1,
            R.id.guessing_number_2,
            R.id.guessing_number_3,
            R.id.guessing_number_4
        ).forEachIndexed { index, id ->
            onView(withId(id)).perform(ViewActions.typeText(digits[index]))
        }
//  click on Guess button
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
