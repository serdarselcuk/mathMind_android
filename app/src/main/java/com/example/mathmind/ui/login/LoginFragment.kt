package com.example.mathmind.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mathmind.R
import com.example.mathmind.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

//    companion object {
//        fun newInstance() = LoginFragment()
//    }

    lateinit var logInButton:Button;
    lateinit var signOnButton:Button;

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
            ViewModelProvider(this).get(LoginViewModel::class.java)

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        logInButton = binding.buttonLogin
        signOnButton = binding.buttonSignOn

        logInButton.setOnClickListener {
            this.findNavController().navigate(R.id.action_login_to_nav_home)
        }

        signOnButton.setOnClickListener{
            signOnButton.text = "clicked"

        }

//        val textView: TextView = binding

//        guessViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        return root
//        inflater.inflate(R.layout.fragment_login, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}