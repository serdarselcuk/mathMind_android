package com.src.mathmind.ui.feedbacker

import ShowDialog
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.src.mathmind.MainActivity
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentFeedbackerBinding
import com.src.mathmind.databinding.ProgressBarBinding
import com.src.mathmind.utils.LogTag
import com.src.mathmind.ui.guesser.GuestureHistoryAdapter
import com.src.mathmind.utils.RandomGenerator
import kotlinx.coroutines.launch

class FeedBackerFragment : Fragment() {

    private var _binding: FragmentFeedbackerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var plusButton: Button
    private lateinit var minusButton: Button
    private lateinit var plusNumberTestView: TextView
    private lateinit var minusNumberTestView: TextView
    private lateinit var guessedNumberTestView: TextView
    private lateinit var feedbackHistoryView: ListView
    private lateinit var doneButton: Button
    private lateinit var feedBackerViewModel: FeedBackerViewModel
    private lateinit var guessHistoryAdapter: GuestureHistoryAdapter
    private lateinit var progressBar: ProgressBarBinding
    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        } else {
            throw IllegalStateException("FeedbackerFragment must be attached to MainActivity")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedBackerViewModel.feedBackStatus.observe(viewLifecycleOwner) { phase ->
            Log.d("FEEDBACKER_OBSERVER", "Status getting Observed: $phase")
            when (phase) {
                FeedBackerViewState.NEW -> {
                    doneButton.text = getString(R.string.ready)
                    doneButton.isActivated = true
                }

                FeedBackerViewState.EVALUATING -> {
                    progressBar.progressBar.visibility = View.VISIBLE
                    doneButton.text = getString(R.string.wait)
                    guessedNumberTestView.text = "- - - -"
                    doneButton.isActivated = false
                    plusNumberTestView.text = ""
                    minusNumberTestView.text = ""
                    guessHistoryAdapter.notifyDataSetChanged()
                }

                FeedBackerViewState.WAITING -> {
                    progressBar.progressBar.visibility = View.GONE
                    guessedNumberTestView.text = feedBackerViewModel.guessNum.value.toString()
                    doneButton.text = getString(R.string.done)
                    doneButton.isActivated = true
                }

                FeedBackerViewState.END -> {
                    endGame()
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(LogTag.FEEDBACKER_FRAGMENT, "Feedbacker created")
        feedBackerViewModel =
            ViewModelProvider(this)[FeedBackerViewModel::class.java]

        _binding = FragmentFeedbackerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mainActivity.setShowSignOutVisible(true)
        progressBar = binding.progressBar
        plusButton = binding.plusButton
        minusButton = binding.minusButton
        plusNumberTestView = binding.plusCount
        minusNumberTestView = binding.minusCount
        guessedNumberTestView = binding.feedbackerNumberView
        feedbackHistoryView = binding.feedbackHistoryList
        doneButton = binding.doneButton
        guessHistoryAdapter = feedBackerViewModel.feedBackHistory.value?.let {
            GuestureHistoryAdapter(requireContext(), it)
        }!!

        feedbackHistoryView.adapter = guessHistoryAdapter

        setClickListeners(plusButton)
        setClickListeners(minusButton)
        setClickListeners(doneButton)

        if (feedBackerViewModel.guessNum.value == 0) {

            ShowDialog().create(
                requireContext(),
                getString(R.string.get_ready),
                getString(R.string.hold_a_number),
                getString(android.R.string.ok)
            )
        }

        return root
    }

    private fun setClickListeners(button: Button) {
        button.setOnClickListener {
            Log.d(LogTag.FEEDBACKER_FRAGMENT, "button click captured${it.id}")
            when (it.id) {
                R.id.plus_button -> {
                    lifecycleScope.launch {
                        when (feedBackerViewModel.getStatus()) {
                            is FeedBackerViewState.WAITING -> {
                                feedBackerViewModel.increasePlus()
                                updateEvaluation()
                            }

                            else -> {
                                Log.d(
                                    LogTag.FEEDBACKER_FRAGMENT,
                                    getString(R.string.wait_for_number_to_evaluate)
                                )
                            }
                        }
                    }
                }

                R.id.minus_button -> {
                    lifecycleScope.launch {
                        when (feedBackerViewModel.getStatus()) {
                            is FeedBackerViewState.WAITING -> {
                                feedBackerViewModel.increaseMinus()
                                updateEvaluation()
                            }

                            else -> {
                                Log.d(
                                    LogTag.FEEDBACKER_FRAGMENT,
                                    getString(R.string.wait_for_number_to_evaluate)
                                )
                            }
                        }
                    }
                }

                R.id.done_button -> {

                    when (feedBackerViewModel.getStatus()) {
                        is FeedBackerViewState.NEW -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                feedBackerViewModel.guessNumber(
                                    RandomGenerator().generateRandomUniqueDigits(
                                        4
                                    )
                                )
                                updateGuessNumberView(feedBackerViewModel.guessNum.value)
                            }
                        }

                        is FeedBackerViewState.WAITING -> {
                            feedBackerViewModel.updateFeedbackHistory()
                            feedBackerViewModel.setStatus(FeedBackerViewState.EVALUATING)
                            lifecycleScope.launch {
                                feedBackerViewModel.guessNumber()
                                updateGuessNumberView(feedBackerViewModel.guessNum.value)
                                feedBackerViewModel.resetFeedback()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateGuessNumberView(num: Int?) {
        if (num is Int) guessedNumberTestView.text = num.toString()
        else throw NullPointerException("Guess number could not be found")
    }

    private fun updateEvaluation() {
        minusNumberTestView.text = feedBackerViewModel.minus.value.toString()
        plusNumberTestView.text = feedBackerViewModel.plus.value.toString()
    }


    override fun onDestroyView() {
        Log.d(LogTag.FEEDBACKER_FRAGMENT, "Destroying")
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(title: String, message: String, navigate: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.OK_BUTTON)) { dialog, _ ->
                dialog.dismiss()
                view?.findNavController()?.navigate(navigate)
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun endGame() {
        showDialog(
            getString(R.string.game_end),
            getString(R.string.you_win),
            R.id.action_nav_feedbacker_to_nav_home
        )
        println("game end")
    }

}