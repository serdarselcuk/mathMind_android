package com.example.mathmind.ui.signOn

import android.accounts.AuthenticatorException
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mathmind.R
import com.example.mathmind.databinding.FragmentSignOnBinding
import com.example.mathmind.models.UserModel
import com.example.mathmind.service.CallService
import com.example.mathmind.utils.ERROR_CONSTANTS
import com.example.mathmind.utils.Utility.Companion.validatePassword
import com.example.mathmind.utils.Utility.Companion.checkEmail
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.navigation.findNavController
import com.example.mathmind.models.Password
import com.example.mathmind.utils.PasswordHasher
import com.example.mathmind.utils.RandomGenerator
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignOnFragment : Fragment() {
    private var _binding: FragmentSignOnBinding? = null
    private val binding get() = _binding!!
    private lateinit var errorTextInput: TextView
    private lateinit var emailInput: EditText
    private lateinit var userNameInput: EditText
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var email: String
    private lateinit var userName:String
    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit var password: String
    private var confirmPassword: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignOnBinding.inflate(inflater, container, false)
        val root: View = binding.root
        errorTextInput = binding.errorView
        emailInput = binding.emailInput
        userNameInput = binding.userNameInput
        firstNameInput = binding.firstNameInput
        lastNameInput = binding.lastNameInput
        passwordInput = binding.passwordInput
        confirmPasswordInput = binding.confirmPasswordInput
        saveButton = binding.saveButton
        cancelButton = binding.cancelButton
        val firstNameErrorInput = binding.firstNameErrorView
        val lastNameErrorInput = binding.lastNameErrorView

        email=""
        password=""
        userName=""
        firstName=""
        lastName=""

        addEmailFocusListener(emailInput)

        addUserNameFocusListener(userNameInput)

        addNameFocusListener(firstNameInput, firstNameErrorInput)

        addNameFocusListener(lastNameInput, lastNameErrorInput)

        addPasswordFocusListener(passwordInput)

        addConfirmPasswordFocusListener(confirmPasswordInput)

        addClickListener(saveButton)

        addClickListener(cancelButton)

        return root
    }

    private fun addClickListener(view: Button) {
        view.requestFocus()
        when (view.id) {
            R.id.saveButton ->
                view.setOnClickListener {
                    lifecycleScope.launch {
                        val errors =
                            checkAllEmptyInputs()

                        if (errors.isEmpty()) {
                            errorTextInput.visibility = View.GONE
//                           .beginTransaction()
//                                .add(android.R.id.content, loadingFragment)
//                                .commit()
                            try {
                                val hashKey = RandomGenerator().generateSalt()
                                val hashed_password = PasswordHasher
                                    .hashPassword(password,hashKey)?: throw AuthenticatorException(ERROR_CONSTANTS.SERVICE_ERROR)
                                createUser(LocalDate.now(), hashKey, hashed_password)
                                showDialog(
                                    getString(R.string.user_saved),
                                    getString(R.string.please_login_with))
                            }catch (e:Error){
                                errorTextInput.text = e.message
                                View.VISIBLE
                            }catch (e2:AuthenticatorException){
                                errorTextInput.text = e2.message
                                View.VISIBLE

                            }catch (e3: Exception){
                                showDialog(
                                    getString(R.string.user_failed),
                                    getString(R.string.please_try_later))
                                View.VISIBLE
                            }
                        } else {
                            errorTextInput.visibility = View.VISIBLE
                            errorTextInput.text = errors.joinToString("\n", "Errors\n")
                        }
                    }
                }

            R.id.cancel_button ->
                view.setOnClickListener {
                    lifecycleScope.launch {
                        emailInput.text.clear()
                        userNameInput.text.clear()
                        passwordInput.text.clear()
                        confirmPasswordInput.text.clear()
                        email = ""
                        userName = ""
                        password = ""
                        confirmPassword = false
                    }
                    this.findNavController().navigate(R.id.nav_login)
                }
        }
    }

//    private suspend fun savePassword(userId: Int):String? {
//        val hashKey = RandomGenerator().generateSalt()
//        val hashed = PasswordHasher
//            .hashPassword(password,hashKey)
//        return if(hashed is String) {
//            suspendCoroutine { continuation ->
//                CallService().savePassword(
//                    Password(
//                        hashed,
//                        hashKey,
//                        userId
//                    )
//                ) { callback ->
//                    println(callback.toString())
//                    continuation.resume(callback)
//                }
//            }
//        } else throw Error("hashing error")
//    }

    private fun addConfirmPasswordFocusListener(confirmPasswordInput: EditText) {

        confirmPasswordInput.onFocusChangeListener =  View.OnFocusChangeListener {  _confirmPasswordInput, hasFocus ->
            lifecycleScope.launch {
                val text = (_confirmPasswordInput as EditText).text.toString()
                val confirmPasswordError: TextView = binding.confirmPasswordErrorView
                if (!hasFocus) {
                    confirmPassword = confirmPassword(text, confirmPasswordError)
                } else {

                    // Handle the error (invalid email format)
                    confirmPassword = false
                    confirmPasswordError.text = null
                }
            }
        }
    }

    private fun addPasswordFocusListener(passwordInput: EditText) {
        passwordInput.onFocusChangeListener = View.OnFocusChangeListener { _passwordInput, hasFocus ->
            lifecycleScope.launch {
                val text = (_passwordInput as EditText).text.toString()
                val passwordError: TextView = binding.passwordErrorView
                // Handle the error (invalid email format)
                if (!hasFocus) {
                    try {
                        // Try to validate the email
                        password = validatePassword(text)

                    } catch (e: IllegalArgumentException) {
                        password = ""

                        e.printStackTrace()
                        // Populate the error message in the EditText
                        passwordError.text = e.message
                    }
                } else {
                    password = getString(R.string.empty_string)
                    passwordError.text = null
                }
            }
        }
    }

    private fun addNameFocusListener(nameInput: EditText, nameError: TextView){

        nameInput.onFocusChangeListener = View.OnFocusChangeListener { _name, hasFocus ->
            lifecycleScope.launch {
                val text = (_name as EditText).text.toString()

                if (!hasFocus) {
                    try {
                        require(text.isNotEmpty()) { ERROR_CONSTANTS.REQUIRED }
                        when (nameInput.id) {
                            R.id.firstNameInput -> firstName = text
                            R.id.lastNameInput -> lastName = text
                        }
                    } catch (e: IllegalArgumentException) {
                        nameError.text = e.message
                    }
                } else {
                    when (nameInput.id) {
                        R.id.firstNameInput -> firstName = getString(R.string.empty_string)
                        R.id.lastNameInput -> lastName = getString(R.string.empty_string)
                    }
                    nameError.text = null
                }
            }
        }
    }

    private fun addUserNameFocusListener(userNameInput: EditText) {
        userNameInput.onFocusChangeListener = View.OnFocusChangeListener { _userName, hasFocus ->
            lifecycleScope.launch {
                val userNameError: TextView = binding.userNameErrorView
                val text = (_userName as EditText).text.toString()
                if (!hasFocus) {
                    CallService().validateUserName(text) { userExists ->
                        if (userExists != null) {
                            println("userExists in callback ${userExists}")
                            if (userExists) userNameError.text = ERROR_CONSTANTS.USER_EXISTS
                            else {
                                userName = text
                            }
                        } else {
                            errorTextInput.text = ERROR_CONSTANTS.SERVICE_ERROR
                            // Handle the case where an error occurred
                            println(ERROR_CONSTANTS.SERVICE_ERROR)
                        }
                    }
                } else {
                    userNameError.text = null
                    userName = getString(R.string.empty_string)
                }
            }

        }
    }

    private fun createUser(date: LocalDate, hash: String, hashedPassword: String) {
        // Define the desired date format
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern(getString(R.string.date_pattern))

        return CallService().saveUser(
            UserModel(
                null,
                userName,
                date.format(formatter),
                firstName,
                lastName,
                email,
                hashedPassword,
                hash
            )
        ) { response ->
            if (response != null) {
                println("in callback $response")
                response.toString()
            } else {
                // Handle the case where an error occurred
                println("Service Error occurred. User not saved. Response: $response")
                throw Error(ERROR_CONSTANTS.SERVICE_ERROR)
            }
        }
    }


    private fun checkAllEmptyInputs() : MutableList<String>{

        val errorList = mutableListOf<String>()

        if(userName.isEmpty() )  {
            errorList.add(ERROR_CONSTANTS.USERNAME_EMPTY)
        }

        if(email.isEmpty() ) {
            errorList.add(ERROR_CONSTANTS.EMAIL_EMPTY)
        }

        if(password.isEmpty() )  {
            errorList.add(ERROR_CONSTANTS.PASSWORD_EMPTY)
        }

        if(!confirmPassword )  {
            errorList.add(ERROR_CONSTANTS.CONFIRM_PASSWORD)
        }

       return errorList
    }

    private fun confirmPassword(string: String, errorView:TextView):Boolean{
        if (password.isBlank()) {
            passwordInput.requestFocus()
            errorView.text = ERROR_CONSTANTS.ENTER_PASSWORD
        } else if (string != password) {
            errorView.text = ERROR_CONSTANTS.PASSWORDS_NOT_MATCHING
        } else {
            return true
        }
        return false
    }

    private fun addEmailFocusListener(emailInput:EditText) {

        emailInput.onFocusChangeListener = View.OnFocusChangeListener { _emailInput, hasFocus ->
            lifecycleScope.launch {
                val text = (_emailInput as EditText).text.toString()
                val emailError: TextView = binding.emailErrorView
                if (!hasFocus) {
                    try {
                        // Try to validate the email
                        email = checkEmail(text)

                    } catch (e: IllegalArgumentException) {
                        email = ""
                        // Handle the error (invalid email format)
                        e.printStackTrace()
                        // Populate the error message in the EditText
                        emailError.text = e.message
                    }
                } else {
                    email = ""
                    emailError.text = null
                }
            }
        }
    }

    private fun showDialog(title:String, message:String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.OK_BUTTON)) { dialog, _ ->
                dialog.dismiss()
                // Navigate to the login screen
                view?.findNavController()?.navigate(R.id.nav_login)
            }
        val dialog = builder.create()
        dialog.show()
    }


}
