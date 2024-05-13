package com.src.mathmind.tests

import android.util.Log
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isFocused
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.src.mathmind.TestBase
import com.src.mathmind.R
import com.src.mathmind.Utils.IsIntegerInRangeMatcher
import com.src.mathmind.utils.LogTag
import org.hamcrest.core.AllOf
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class GuesserTests : TestBase() {

    override fun setup() {
        super.setup()
        login()
        landOnGuesser()
    }

    @Test
    fun user_able_to_get_feedback() {
        Log.d(LogTag.UI_TEST, "user able to get feedback")

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


    @Test
    fun user_able_to_see_guest_number_history() {
        Log.d(LogTag.UI_TEST, "user able to see guest number history")

        guessNumber("1234")
        validateDisplayedWithText(withId(R.id.guesture_history_list), "1234")
    }

    @Test
    fun user_enters_number_starting_with_0() {
        Log.d(LogTag.UI_TEST, "user enters number starting with 0")
        activityRule.scenario.onActivity { it.navController.currentDestination?.hierarchy }

        guessNumber("0123")// focus on 0 and removed
        onView(withId(R.id.guessing_number_1))
            .check(matches(isFocused()))
            .check(matches(withText("")))
    }

    @Test
    fun user_enters_number_contains_repeating_digits() {
        Log.d(LogTag.UI_TEST, "user enters number contains repeating digits")
        activityRule.scenario.onActivity { it.navController.currentDestination?.hierarchy }

        guessNumber("1123")// focus on 2nd 1 and removed
        onView(withId(R.id.guessing_number_2))
            .check(matches(isFocused()))
            .check(matches(withText("")))
    }


    @Test
    fun user_enters_same_number_while_guessing() {
        Log.d(LogTag.UI_TEST, "user enters same number while guessing")
        activityRule.scenario.onActivity { it.navController.currentDestination?.hierarchy }

        guessNumber("1234")// focus on 1st cell and all removed
        guessNumber("1234")
        onView(withId(R.id.guessing_number_1))
            .check(matches(isFocused()))

        arrayOf(
            withId(R.id.guessing_number_1),
            withId(R.id.guessing_number_2),
            withId(R.id.guessing_number_3),
            withId(R.id.guessing_number_4)
        ).forEach {
            onView(it).check(matches(withText("")))
        }

    }
}




