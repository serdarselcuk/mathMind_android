package com.src.mathmind

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.core.AllOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


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
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Espresso
            .onView(ViewMatchers.withId(R.id.buttonLogin))
            .perform(ViewActions.click())
        Espresso
            .onView(ViewMatchers.withId(R.id.guesser_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.guessing_number_1))
            .perform(ViewActions.typeText("1"))
        Espresso.onView(ViewMatchers.withId(R.id.guessing_number_2))
            .perform(ViewActions.typeText("2"))
        Espresso.onView(ViewMatchers.withId(R.id.guessing_number_3))
            .perform(ViewActions.typeText("3"))
        Espresso.onView(ViewMatchers.withId(R.id.guessing_number_4))
            .perform(ViewActions.typeText("4"))

        Espresso
            .onView(ViewMatchers.withId(R.id.guessing_button))
            .perform(ViewActions.click())

        Espresso
            .onView(ViewMatchers.withId(R.id.guesture_history_list))
            .check(ViewAssertions.matches(
                ViewMatchers.hasDescendant(
                    AllOf.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText("1234")
                ))

            ))
    }
}