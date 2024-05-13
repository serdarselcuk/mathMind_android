package com.src.mathmind.tests

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.src.mathmind.TestBase
import com.src.mathmind.R
import com.src.mathmind.utils.LogTag
import org.junit.Test

class FeedBackerTests : TestBase() {

    override fun setup() {
        super.setup()
        login()
        landOnFeedBacker()
    }

    @Test
    fun user_able_to_see_guessed_number_on_feedbacker() {
        Log.d(LogTag.UI_TEST, "user able to see guessed number on feedbacker")

        onView(withId(R.id.done_button)).perform(click())
        onView(withId(R.id.plus_button)).perform(click())
        onView(withId(R.id.minus_button)).perform(click())
// plus number increased 1
        onView(withId(R.id.plus_count))
            .check(ViewAssertions.matches(withText(("1"))))
// minus number increased 1
        onView(withId(R.id.minus_count))
            .check(ViewAssertions.matches(withText(("1"))))
    }

    @Test
    fun plus_count_and_minus_count_is_not_getting_higher_then() {
        Log.d(LogTag.UI_TEST, "plus count and minus count is not getting higher then 4")
        onView(withId(R.id.done_button)).perform(click())

        onView(withId(R.id.plus_button))
            .perform(click())
            .perform(click())
            .perform(click())
            .perform(click())
        onView(withId(R.id.minus_button))
            .perform(click())
            .perform(click())
            .perform(click())
// plus number increased 1
        onView(withId(R.id.plus_count))
            .check(ViewAssertions.matches(withText(("1"))))
// minus number increased 1
        onView(withId(R.id.minus_count))
            .check(ViewAssertions.matches(withText(("3"))))
    }
}