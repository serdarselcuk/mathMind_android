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

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        progressBar = binding.progressBar
        logInButton = binding.buttonLogin
        signOnButton = binding.buttonSignOn
        userNameField = binding.textInputUserName
        passwordField = binding.editTextTextPassword

        logInButton.setOnClickListener {
            val userName: String? = viewModel.setUserName(userNameField).value
            val password: String? = viewModel.setPassword(passwordField).value
            if (
                !userName.isNullOrEmpty() && !password.isNullOrEmpty()
            ) {
                progressBar.progressBar.visibility = View.VISIBLE
                viewModel.validateUser(mainActivity.getIdlingTool())
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

                is LoginViewState.LoggedOut -> {}
            }
        }
        userNameField.isFocused

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
        viewModel.clear()
        super.onDestroy()
    }

    private fun navigateTo(id: Int) {
        onDestroy()
        findNavController().navigate(id)
    }

}