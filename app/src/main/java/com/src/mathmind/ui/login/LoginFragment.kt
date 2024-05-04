package com.src.mathmind.ui.login

import ShowDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.src.mathmind.MainActivity
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentLoginBinding
import com.src.mathmind.databinding.ProgressBarBinding
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

        val sharedPreferences = context?.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        logInButton.setOnClickListener {
            val userName: String? = viewModel.setUserName(userNameField).value
            val password: String? = viewModel.setPassword(passwordField).value

            if (!userName.isNullOrEmpty() && !password.isNullOrEmpty()) {
                progressBar.progressBar.visibility = View.VISIBLE
                viewModel.validateUser(mainActivity.callService(), sharedPreferences)
            }
        }

        signOnButton.setOnClickListener {
            navigateTo(R.id.nav_signOn)
        }

        viewModel.loginViewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewState.ValidationSuccess -> {
                    viewModel.userName.value?.let { mainActivity.updateUserName(it) }
                    navigateTo(R.id.action_login_to_nav_home)

                }

                is LoginViewState.ValidationError -> {
                    progressBar.progressBar.visibility = View.GONE
                    progressBar.overlayView.visibility = View.GONE
                    context?.let {
                        ShowDialog().create(
                            it,
                            ERROR_CONSTANTS.VALIDATION_FAILED,
                            state.message,
                            getString(android.R.string.ok)
                        )
                    }
                }

                is LoginViewState.Error -> {
                    context?.let {
                        ShowDialog().create(
                            it,
                            ERROR_CONSTANTS.ERROR_HEADER,
                            state.message,
                            getString(android.R.string.ok)
                        )
                    }
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