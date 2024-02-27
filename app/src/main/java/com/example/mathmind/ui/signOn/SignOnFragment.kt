package com.example.mathmind.ui.signOn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mathmind.R
import com.example.mathmind.databinding.FragmentSignOnBinding
import com.example.mathmind.service.CallService
import com.example.mathmind.ui.login.LoginFragment
import com.example.mathmind.utils.ERROR_CONSTANTS
import com.example.mathmind.utils.Utility.Companion.validatePassword
import com.example.mathmind.utils.Utility.Companion.checkEmail

class SignOnFragment : Fragment() {
    private var _binding: FragmentSignOnBinding? = null
    private val binding get() = _binding!!
    private lateinit var errorTextInput: TextView
    private lateinit var emailInput: EditText
    private lateinit var userNameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var email: String
    private lateinit var userName:String
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
        passwordInput = binding.passwordInput
        confirmPasswordInput = binding.confirmPasswordInput
        saveButton = binding.saveButton
        cancelButton = binding.cancelButton
        email=""
        password=""
        userName=""

        addEmailFocusListener(emailInput)

        addUserNameFocusListener(userNameInput)

        addPasswordFocusListener(passwordInput)

        addConfirmPasswordFocusListener(confirmPasswordInput)

        addClickListener(saveButton)

        addClickListener(cancelButton)

        return root
    }

    private fun addClickListener(view: Button) {
        when (view.id) {
            R.id.saveButton ->
                view.setOnClickListener {
                    val errors = checkAllEmptyInputs()

                    if (errors.isEmpty()) {
                        errorTextInput.visibility = View.GONE
                        createUser()
                    } else {
                        errorTextInput.visibility = View.VISIBLE
                        errorTextInput.text = errors.joinToString("\n", "Errors")
                    }
                }

            R.id.cancel_button ->
                view.setOnClickListener {
                    emailInput.text.clear()
                    userNameInput.text.clear()
                    passwordInput.text.clear()
                    confirmPasswordInput.text.clear()
                    email = ""
                    userName = ""
                    password = ""
                    confirmPassword = false
                    this.findNavController().navigate(R.id.nav_login)
                }
        }

    }

    private fun addConfirmPasswordFocusListener(confirmPasswordInput: EditText) {

        confirmPasswordInput.onFocusChangeListener =  View.OnFocusChangeListener {  _confirmPasswordInput, hasFocus ->
            val text = ( _confirmPasswordInput as EditText).text.toString()
            val confirmPasswordError: TextView = binding.confirmPasswordErrorView
            if (!hasFocus) {
                confirmPassword = confirmPassword(text, confirmPasswordError)
            }else{

                // Handle the error (invalid email format)
                confirmPassword = false
                confirmPasswordError.text = null
            }
        }
    }

    private fun addPasswordFocusListener(passwordInput: EditText) {
        passwordInput.onFocusChangeListener = View.OnFocusChangeListener { _passwordInput, hasFocus ->
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
            }else {
                password = ""
                passwordError.text= null
            }
        }
    }

    private fun addUserNameFocusListener(userNameInput: EditText) {
        userNameInput.onFocusChangeListener = View.OnFocusChangeListener { _userName, hasFocus ->
            val userNameError: TextView = binding.userNameErrorView
            val text = (_userName as EditText).text.toString()
            if(!hasFocus){

                CallService().validateUserName(text) { userExists ->
                    if (userExists != null) {
                        println("userExists in callback ${userExists}")
                        if (userExists) userNameError.text = ERROR_CONSTANTS.USER_EXISTS
                        else{
                            userName =text
                        }
                    } else {
                        errorTextInput.text = ERROR_CONSTANTS.SERVICE_ERROR
                        // Handle the case where an error occurred
                        println("Service Error occurred ")
                    }

                }
            }
            else {
                userNameError.text = null
                userName = ""
            }

        }
    }

    private fun createUser() {
//        TODO("Not yet implemented")
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
