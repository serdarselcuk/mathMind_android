package com.src.mathmind.tests

import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.src.mathmind.EspressoBase
import com.src.mathmind.R
import com.src.mathmind.utils.ERROR_CONSTANTS
import com.src.mathmind.utils.LogTag
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Test

class SignOnTests : EspressoBase() {

    override fun setup(){
        super.setup()
        Espresso.onView(ViewMatchers.withId(R.id.buttonSignOn)).perform(ViewActions.click())
    }

    @Test
    fun user_gets_email_entry_error_while_signing_on() {
        Log.d(LogTag.UI_TEST, "user gets email entry error while signing on")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.emailInput)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.userNameInput)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.emailErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(ERROR_CONSTANTS.EMAIL_EMPTY)))
    }

    @Test
    fun user_enters_existing_userName_gets_error_text_while_signing_on() {
        Log.d(LogTag.UI_TEST, "user enters existing userName gets error text while signing on")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.userNameInput))
            .perform(ViewActions.click()).perform(ViewActions.typeText("use_2"))
        Espresso.onView(ViewMatchers.withId(R.id.firstNameInput)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.userNameErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(ERROR_CONSTANTS.USER_EXISTS)))
    }

    @Test
    fun user_gets_firstName_entry_error_while_signing_on() {
        Log.d(LogTag.UI_TEST, "user gets firstName entry error while signing on")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.firstNameInput)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.lastNameInput)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.firstNameErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(ERROR_CONSTANTS.REQUIRED)))
    }

    @Test
    fun user_gets_error_list_when_clicks_on_save_without_filling_any() {
        Log.d(LogTag.UI_TEST, "user gets error list when clicks on save without filling any")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.errorView))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        allOf(
                            containsString(ERROR_CONSTANTS.EMAIL_EMPTY),
                            containsString(ERROR_CONSTANTS.PASSWORD_EMPTY),
                            containsString(ERROR_CONSTANTS.CONFIRM_PASSWORD),
                            containsString(ERROR_CONSTANTS.USERNAME_EMPTY),
                            containsString(ERROR_CONSTANTS.EMAIL_EMPTY)
                        )
                    )
                )
            )
    }

    @Test
    fun user_enters_wrong_password_to_confirm() {
        Log.d(LogTag.UI_TEST, "user enters wrong password to confirm")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("Password1."))
        Espresso.onView(ViewMatchers.withId(R.id.confirmPasswordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("Password2."))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.confirmPasswordErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(ERROR_CONSTANTS.PASSWORDS_NOT_MATCHING)))
    }


    @Test
    fun user_enters_wrong_password_without_special_char() {
        Log.d(LogTag.UI_TEST, "user enters wrong password to confirm")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("Password1"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.passwordErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(containsString( ERROR_CONSTANTS.SPECIAL))))
    }

    @Test
    fun user_enters_wrong_password_with_white_space() {
        Log.d(LogTag.UI_TEST, "user enters wrong password with white space")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("Password1. "))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.passwordErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(containsString( ERROR_CONSTANTS.WHITESPACE))))
    }

    @Test
    fun user_enters_wrong_password_with_out_capital_case() {
        Log.d(LogTag.UI_TEST, "user enters wrong password with out capital case")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("123456a."))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.passwordErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(containsString( ERROR_CONSTANTS.UPPER))))
    }

    @Test
    fun user_enters_wrong_password_with_out_lower_case() {
        Log.d(LogTag.UI_TEST, "user enters wrong password with out lower letter")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("123456A."))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.passwordErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(containsString( ERROR_CONSTANTS.LOWER))))
    }

    @Test
    fun user_enters_wrong_password_with_out_digits() {
        Log.d(LogTag.UI_TEST, "user enters wrong password with out capital digits")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("Password."))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.passwordErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(containsString( ERROR_CONSTANTS.DIGIT))))
    }

    @Test
    fun user_enters_short_length_key() {
        Log.d(LogTag.UI_TEST, "user enters short password")
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText("Pass1."))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.passwordErrorView))
            .check(ViewAssertions.matches(ViewMatchers.withText(containsString( ERROR_CONSTANTS.LEAST_LENGTH))))
    }

}