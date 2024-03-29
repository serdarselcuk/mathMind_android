package com.src.mathmind.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var guesserButton: ImageButton
    lateinit var feedBackerButton: ImageButton

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.headerGuess
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        guesserButton = binding.guesserButton
        feedBackerButton = binding.feedbackerButton

        guesserButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_guesser)
        }

        feedBackerButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_feedbacker)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}