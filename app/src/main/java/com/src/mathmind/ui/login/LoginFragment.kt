package com.src.mathmind.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentLoginBinding
import com.src.mathmind.models.LoginViewModel
import com.src.mathmind.service.CallService

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginActivity()
    }

    lateinit var logInButton:Button
    lateinit var signOnButton:Button
    lateinit var userName:String
    lateinit var password:String


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
        val loginViewModel =
            ViewModelProvider(this)[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        logInButton = binding.buttonLogin
        signOnButton = binding.buttonSignOn

        logInButton.setOnClickListener {
//            LoginActivity().validateUser(
//                userName, password)
            CallService().calling()
            this.findNavController().navigate(R.id.action_login_to_nav_home)
        }

        signOnButton.setOnClickListener{
            this.findNavController().navigate(R.id.nav_signOn)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }



}