package com.src.mathmind.ui.signOn

import ShowDialog
import android.accounts.AuthenticatorException
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.src.mathmind.MainActivity
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentSignOnBinding
import com.src.mathmind.databinding.ProgressBarBinding
import com.src.mathmind.utils.ERROR_CONSTANTS
import com.src.mathmind.utils.LogTag
import com.src.mathmind.utils.Utility.Companion.checkEmail
import kotlinx.coroutines.launch

class SignOnFragment : Fragment() {
    private var _binding: FragmentSignOnBinding? = null
    private val binding get() = _binding!!
    private lateinit var signOnViewModel: SignOnViewModel
    private lateinit var errorTextInput: TextView
    private lateinit var emailInput: EditText
    private lateinit var userNameInput: EditText
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var progressBar: ProgressBarBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignOnBinding.inflate(inflater, container, false)
        val root: View = binding.root
        signOnViewModel =
            ViewModelProvider(this)[SignOnViewModel::class.java]
        progressBar = binding.progressBar
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        } else {
            throw IllegalStateException("LoginFragment must be attached to MainActivity")
        }
    }

    private fun addClickListener(view: Button) {
        view.requestFocus()
        when (view.id) {
            R.id.saveButton -> view.setOnClickListener {
                lifecycleScope.launch {
                    val errors = signOnViewModel.checkAllEmptyInputs()

                    if (errors.isEmpty()) {
                        errorTextInput.visibility = View.GONE
                        progressBar.progressBar.visibility = View.VISIBLE
//
                        try {

                            val hashedCredentials = signOnViewModel.getHashedPassword()
                            signOnViewModel.createUser(
                                mainActivity,
                                hashedCredentials,
                            )

                            context?.let { context ->
                                ShowDialog().create(
                                    context,
                                    getString(R.string.user_saved),
                                    getString(R.string.please_login_with),
                                    onPositiveClick = {
                                        findNavController().navigate(R.id.action_signOn_to_nav_login)
                                    }
                                )
                            }

                        } catch (e: Error) {
                            errorTextInput.text = e.message
                            View.VISIBLE
                        } catch (e2: AuthenticatorException) {
                            errorTextInput.text = e2.message
                            View.VISIBLE
                        } catch (e3: Exception) {
                            context?.let { it2 ->
                                ShowDialog().create(
                                    it2,
                                    getString(R.string.user_failed),
                                    getString(R.string.please_try_later),
                                    onPositiveClick = {
                                        findNavController().navigate(R.id.action_signOn_to_nav_login)
                                    }
                                )
                            }
                        } finally {
                            progressBar.progressBar.visibility = View.GONE
                        }
                    } else {
                        errorTextInput.visibility = View.VISIBLE
                        errorTextInput.text = errors.joinToString("\n", "Errors\n")
                    }
                }
            }

            R.id.cancel_button -> view.setOnClickListener {
                lifecycleScope.launch {
                    emailInput.text.clear()
                    userNameInput.text.clear()
                    passwordInput.text.clear()
                    confirmPasswordInput.text.clear()
                    signOnViewModel.clearValues()
                }
                this.findNavController().navigate(R.id.nav_login)
            }
        }
    }

    private fun addConfirmPasswordFocusListener(confirmPasswordInput: EditText) {

        confirmPasswordInput.onFocusChangeListener = View
            .OnFocusChangeListener { _confirmPasswordInput, hasFocus ->
                lifecycleScope.launch {

                    val confirmPasswordError: TextView = binding.confirmPasswordErrorView
                    val passwordError: TextView = binding.passwordErrorView

                    if (!hasFocus) {
                        val passwordConfirmationInput =
                            (_confirmPasswordInput as EditText).text.toString()
                        val confirmation =
                            signOnViewModel.confirmPassword(passwordConfirmationInput)
                        if (confirmation == null) {
                            passwordError.text = ERROR_CONSTANTS.ENTER_PASSWORD
                            passwordError.visibility = View.VISIBLE
                        } else if (!confirmation) {
                            confirmPasswordError.text = ERROR_CONSTANTS.PASSWORDS_NOT_MATCHING
                            confirmPasswordError.visibility = View.VISIBLE
                        } else {
                            signOnViewModel.setConfirmPassword(true)
                            passwordError.visibility = View.GONE
                            confirmPasswordError.visibility = View.GONE
                        }
                    } else {
                        signOnViewModel.setConfirmPassword(false)
                    }
                }
            }
    }

    private fun addPasswordFocusListener(passwordInput: EditText) {
        passwordInput.onFocusChangeListener =
            View.OnFocusChangeListener { _passwordInput, hasFocus ->
                lifecycleScope.launch {
                    val passwordText = (_passwordInput as EditText).text.toString()
                    val passwordError: TextView = binding.passwordErrorView
                    if (!hasFocus) {
                        val validationErrors = signOnViewModel.validatePassword(passwordText)
                        if (!validationErrors.isNullOrEmpty()) passwordError.text = validationErrors
                    } else {
                        signOnViewModel.setPassword(getString(R.string.empty_string))
                        passwordError.text = null
                    }
                }
            }
    }

    private fun addNameFocusListener(nameInput: EditText, nameError: TextView) {

        nameInput.onFocusChangeListener = View.OnFocusChangeListener { _name, hasFocus ->
            lifecycleScope.launch {
                val text = (_name as EditText).text.toString()

                if (!hasFocus) {
                    try {
                        require(text.isNotEmpty()) { ERROR_CONSTANTS.REQUIRED }
                        signOnViewModel.setName(text, nameInput.id)
                    } catch (e: IllegalArgumentException) {
                        nameError.text = e.message
                    }
                } else {
                    signOnViewModel.setName(
                        getString(R.string.empty_string),
                        nameInput.id
                    )
                    nameError.text = null
                }
            }
        }
    }

    private fun addUserNameFocusListener(userNameInput: EditText) {
        userNameInput.onFocusChangeListener = View.OnFocusChangeListener { userNameField, hasFocus ->
            lifecycleScope.launch {
                val userNameError: TextView = binding.userNameErrorView
                val userNameFieldText = (userNameField as EditText).text.toString()
                if (!hasFocus) {
                    val userNameValidationError = signOnViewModel.validateUserName(mainActivity.callService(), userNameFieldText)
                    if (!userNameValidationError.isNullOrEmpty()) {
                        userNameError.text = userNameValidationError
                        userNameError.visibility = View.VISIBLE
                    }
                } else {
                    userNameError.text = null
                    signOnViewModel.setUserName(getString(R.string.empty_string))
                    userNameError.visibility = View.GONE
                }
            }
        }
    }

    private fun addEmailFocusListener(emailInput: EditText) {

        emailInput.onFocusChangeListener = View.OnFocusChangeListener { _emailInput, hasFocus ->
            lifecycleScope.launch {
                val text = (_emailInput as EditText).text.toString()
                val emailError: TextView = binding.emailErrorView
                if (!hasFocus) {
                    try {
                        // Try to validate the email
                        signOnViewModel.setEMail(checkEmail(text))

                    } catch (e: IllegalArgumentException) {
                        // Populate the error message in the EditText
                        emailError.text = if (text.isNotEmpty()) e.message
                        else ERROR_CONSTANTS.EMAIL_EMPTY
                        signOnViewModel.setEMail(getString(R.string.empty_string))
                        // Handle the error (invalid email format)
                        Log.e(LogTag.SIGN_ON_VIEW, e.stackTraceToString())
                    }
                } else {
                    signOnViewModel.setEMail(getString(R.string.empty_string))
                    emailError.text = null
                }
            }
        }
    }

}
