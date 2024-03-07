package com.src.mathmind.ui.feedbacker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.src.mathmind.databinding.FragmentFeedbackerBinding
import com.src.mathmind.models.FeedBackerViewModel

class FeedBackerFragment : Fragment() {

    private var _binding: FragmentFeedbackerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val feedBackerViewModel =
                ViewModelProvider(this).get(FeedBackerViewModel::class.java)

        _binding = FragmentFeedbackerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFeedbaker
        feedBackerViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}