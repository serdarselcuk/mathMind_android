package com.example.mathmind.ui.signOn

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
                            createUser(LocalDate.now())
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
                    password = ""
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
                        R.id.firstNameInput -> firstName = ""
                        R.id.lastNameInput -> lastName = ""
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
                            println("Service Error occurred ")
                        }
                    }
                } else {
                    userNameError.text = null
                    userName = ""
                }
            }

        }
    }

    private fun createUser(date: LocalDate) {
        // Define the desired date format
        lifecycleScope.launch {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val user = UserModel(
                null,
                userName,
                date.format(formatter),
                firstName,
                lastName,
                email
            )
            CallService().saveUser(user) { _user ->
                if (_user != null) {
                    println("success userExists in callback ${_user}")
                } else {
                    errorTextInput.text = ERROR_CONSTANTS.SERVICE_ERROR
                    // Handle the case where an error occurred
                    println("Service Error occurred. User not saved \n $_user")
                } }
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

}
