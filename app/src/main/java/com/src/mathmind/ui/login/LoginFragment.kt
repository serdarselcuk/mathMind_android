package com.src.mathmind.ui.login

import android.app.AlertDialog
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
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    lateinit var userNameField: TextView
    lateinit var passwordField: TextView
    lateinit var logInButton: Button
    lateinit var signOnButton: Button
    lateinit var userName: String
    lateinit var password: String


    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        logInButton = binding.buttonLogin
        signOnButton = binding.buttonSignOn
        userNameField = binding.textInputUserName
        passwordField = binding.editTextTextPassword

        logInButton.setOnClickListener {
            userName = userNameField.text.toString()
            password = passwordField.text.toString()
            if (userName.isBlank()) {
                userNameField.error = "Provide your user name"
            } else if (password.isBlank()) {
                passwordField.error = "Please enter password"
            } else {
                viewModel.validateUser(userName, password)

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
//                    showDialog("Validation failed", state.message)
                    navigateTo(R.id.action_login_to_nav_home)
                }

                is LoginViewState.Error -> {
//                    showDialog("Error", state.message)
                    navigateTo(R.id.action_login_to_nav_home)
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
//        inflater.inflate(R.layout.fragment_login, container, false)
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
}