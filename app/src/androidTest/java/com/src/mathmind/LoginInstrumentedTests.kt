package com.src.mathmind


import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.src.mathmind.utils.LogTag
import com.src.mathmind.utils.ERROR_CONSTANTS
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginInstrumentedTests : EspressoBase() {

    private lateinit var idlingResource: IdlingResource
//    private lateinit var appContext: Context

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
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


    @Test
    fun user_able_to_see_guest_number_history() {
        Log.d(LogTag.UI_TEST, "user able to see guest number history")
        // Context of the app under test.
        login()
        landOnGuesser()
        guessNumber("1234")
        validateDisplayedWithText(withId(R.id.guesture_history_list), "1234")
    }

    @Test
    fun user_able_to_see_guessed_number_on_feedbacker() {
        Log.d(LogTag.UI_TEST, "user able to see guessed number on feedbacker")
        // Context of the app under test.
        login()
        landOnFeedBacker()
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
    fun user_enters_wrong_userName() {
        Log.d(LogTag.UI_TEST, "user enters wrong userName")
        // Context of the app under test.
        onView(withId(R.id.textInputUserName))
            .perform(typeText("userName"))
        onView(withId(R.id.editTextTextPassword))
            .perform(typeText("password"))
        onView(withId(R.id.buttonLogin))
            .perform(click())


        // Check if the dialog box is displayed
        onView(withText(ERROR_CONSTANTS.VALIDATION_FAILED))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(isDisplayed()))

        // Validate text in the dialog box
        onView(withText(ERROR_CONSTANTS.CREDENTIALS_VALIDATION_FAILURE))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(isDisplayed()))

    }

    @Test
    fun user_not_enters_userName_on_login() {
        Log.d(LogTag.UI_TEST, "user not enters userName on login")
        // Context of the app under test.

        onView(withId(R.id.editTextTextPassword))
            .perform(typeText("password"))
        onView(withId(R.id.buttonLogin))
            .perform(click())

        onView(withId(R.id.textInputUserName))
            .check(ViewAssertions.matches(hasErrorText(ERROR_CONSTANTS.PROVIDE_USER_NAME)))

    }

    @Test
    fun user_not_enters_password_on_login() {
        Log.d(LogTag.UI_TEST, "user not enters password on login")
        // Context of the app under test.

        onView(withId(R.id.textInputUserName))
            .perform(typeText("USERnAME"))
        onView(withId(R.id.buttonLogin))
            .perform(click())

        onView(withId(R.id.editTextTextPassword))
            .check(ViewAssertions.matches(hasErrorText(ERROR_CONSTANTS.PROVIDE_PASSWORD)))

    }

    @Test
    fun user_get_email_entry_error_while_signing_on() {
        Log.d(LogTag.UI_TEST, "user get email entry error while signing on")
        // Context of the app under test.
        onView(withId(R.id.buttonSignOn)).perform(click())

        onView(withId(R.id.emailInput)).perform(click())
        onView(withId(R.id.userNameInput)).perform(click())

        onView(withId(R.id.emailErrorView))
            .check(ViewAssertions.matches(withText((ERROR_CONSTANTS.EMAIL_EMPTY))))

    }
}


