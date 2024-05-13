package com.src.mathmind.tests


import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.src.mathmind.TestBase
import com.src.mathmind.R
import com.src.mathmind.utils.LogTag
import com.src.mathmind.utils.ERROR_CONSTANTS
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginTests : TestBase() {

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
            .perform(typeText("UserName"))
        onView(withId(R.id.buttonLogin))
            .perform(click())

        onView(withId(R.id.editTextTextPassword))
            .check(ViewAssertions.matches(hasErrorText(ERROR_CONSTANTS.PROVIDE_PASSWORD)))

    }

}