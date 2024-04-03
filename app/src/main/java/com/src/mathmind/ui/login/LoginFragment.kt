package com.src.mathmind.ui.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.src.mathmind.MainActivity
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentLoginBinding
import com.src.mathmind.databinding.ProgressBarBinding
import com.src.mathmind.models.LoginViewState
import com.src.mathmind.utils.ERROR_CONSTANTS


class LoginFragment : Fragment() {

    private lateinit var userNameField: TextView
    private lateinit var passwordField: TextView
    private lateinit var logInButton: Button
    private lateinit var signOnButton: Button
    private lateinit var userName: String
    private lateinit var password: String
    private lateinit var mainActivity: MainActivity


    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private lateinit var progressBar: ProgressBarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]


        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        progressBar = binding.progressBar

        logInButton = binding.buttonLogin
        signOnButton = binding.buttonSignOn
        userNameField = binding.textInputUserName
        passwordField = binding.editTextTextPassword

        logInButton.setOnClickListener {
            userName = userNameField.text.toString()
            password = passwordField.text.toString()
            if (userName.isBlank()) {
                userNameField.error = ERROR_CONSTANTS.PROVIDE_USER_NAME
            } else if (password.isBlank()) {
                passwordField.error = ERROR_CONSTANTS.PROVIDE_PASSWORD
            } else {
                progressBar.progressBar.visibility = View.VISIBLE
                progressBar.overlayView.visibility = View.VISIBLE
                viewModel.validateUser(userName, password, mainActivity.getIdlingTool())
            }
        }

        signOnButton.setOnClickListener {
            navigateTo(R.id.nav_signOn)
        }

        viewModel.loginViewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewState.ValidationSuccess -> {
                    navigateTo(R.id.action_login_to_nav_home)
                }

                is LoginViewState.ValidationError -> {
                    progressBar.progressBar.visibility = View.GONE
                    progressBar.overlayView.visibility = View.GONE
                    showDialog(ERROR_CONSTANTS.VALIDATION_FAILED, state.message)
                }

                is LoginViewState.Error -> {
                    showDialog(ERROR_CONSTANTS.ERROR_HEADER, state.message)
                }
            }
        }

        val loginNameInput: EditText = binding.textInputUserName
        assignListener(loginNameInput)

        val passwordText: EditText = binding.editTextTextPassword
        assignListener(loginNameInput)

        loginNameInput.isFocused

        loginNameInput.doAfterTextChanged { userName = it.toString() }
        passwordText.doAfterTextChanged { password = it.toString() }


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

    private fun assignListener(textInput: EditText){
        textInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!s.isNullOrBlank()) {
                    println(s)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    println(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) {
                    println(s)
                }
            }
        })
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.OK_BUTTON)) { dialog, _ ->
                dialog.dismiss()
                // Navigate to the login screen
            }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        userNameField.text = ""
        passwordField.text = ""
        userName = ""
        password = ""
        super.onDestroy()
    }

    private fun navigateTo(id: Int) {
        onDestroy()
        findNavController().navigate(id)
    }

//    @VisibleForTesting
//    fun getIdlingResource(): IdlingResource {
//        if (IdlingTool == null) {
//            mIdlingResource = SimpleIdlingResource()
//        }
//        return mIdlingResource
//    }
}