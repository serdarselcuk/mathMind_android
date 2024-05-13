package com.src.mathmind.ui.feedbacker

import ShowDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.src.mathmind.MainActivity
import com.src.mathmind.R
import com.src.mathmind.databinding.DigitCellsBinding
import com.src.mathmind.databinding.FragmentFeedbackerBinding
import com.src.mathmind.databinding.ProgressBarBinding
import com.src.mathmind.ui.guesser.GuestureHistoryAdapter
import com.src.mathmind.utils.ERROR_CONSTANTS
import com.src.mathmind.utils.LogTag
import com.src.mathmind.utils.RandomGenerator
import com.src.mathmind.utils.Utility
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import kotlin.math.min


class FeedBackerFragment : Fragment() {

    private var _binding: FragmentFeedbackerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var plusButton: Button
    private lateinit var minusButton: Button
    private lateinit var plusNumberTextView: TextView
    private lateinit var minusNumberTextView: TextView
    private lateinit var guessedNumberTextView: DigitCellsBinding
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
            Log.d(LogTag.FEEDBACKER_OBSERVER,"Status getting Observed: $phase")
            when (phase) {
                FeedBackerViewState.NEW -> {
                    doneButton.text = getString(R.string.ready)
                    doneButton.isActivated = true
                }

                FeedBackerViewState.EVALUATING -> {
                    progressBar.progressBar.visibility = View.VISIBLE
                    doneButton.text = getString(R.string.wait)
                    doneButton.isActivated = false
                    plusNumberTextView.text = "0"
                    minusNumberTextView.text = "0"
                    guessHistoryAdapter.notifyDataSetChanged()
                }

                FeedBackerViewState.WAITING -> {
                    progressBar.progressBar.visibility = View.GONE
                    feedBackerViewModel.guessNum.value?.let { fillDigitCells(it) }
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
        plusNumberTextView = binding.plusCount
        minusNumberTextView = binding.minusCount
        guessedNumberTextView = binding.containerDigitCells
        feedbackHistoryView = binding.feedbackHistoryList
        doneButton = binding.doneButton
        guessHistoryAdapter = feedBackerViewModel.feedBackHistory.value?.let {
            GuestureHistoryAdapter(requireContext(), it)
        }!!

        feedbackHistoryView.adapter = guessHistoryAdapter

        feedbackHistoryView.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                ShowDialog().create(
                    requireContext(),
                    getString(R.string.wrong_feedback),
                    getString(R.string.do_you_want_to_move_back_to_this_feedback),
                    getString(R.string.undo),
                    getString(android.R.string.cancel),
                    onPositiveClick = {
                        feedBackerViewModel.undoFeedBacks(position)
                        guessHistoryAdapter.notifyDataSetChanged()
                    },
                    onNegativeClick = null
                )
            }

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
            Log.d(LogTag.FEEDBACKER_FRAGMENT, "button click captured ${it.id}")
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
                                    RandomGenerator().generateRandomUniqueDigits(4)
                                )
                                updateGuessNumberView(feedBackerViewModel.guessNum.value)
                            }
                        }

                        is FeedBackerViewState.WAITING -> {
                            feedBackerViewModel.updateFeedbackHistory()
                            feedBackerViewModel.setStatus(FeedBackerViewState.EVALUATING)
                            lifecycleScope.launch {
                                try {
                                    feedBackerViewModel.guessNumber()
                                }catch (e: RuntimeException){
                                    e.localizedMessage?.let { it1 ->
                                        ShowDialog().create(
                                            requireContext(),
                                            ERROR_CONSTANTS.ERROR_HEADER,
                                            it1,
                                            getString(android.R.string.ok),
                                            null,
                                            onPositiveClick = {
                                                feedBackerViewModel.undoFeedBacks(-1)
                                                guessHistoryAdapter.notifyDataSetChanged()
                                                feedBackerViewModel.setStatus(FeedBackerViewState.WAITING)
                                            },
                                            null
                                        )
                                    }
                                }
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
        if (num is Int) fillDigitCells(num)
        else throw NullPointerException("Guess number could not be found")
    }

    private fun updateEvaluation() {
        minusNumberTextView.text = getString(R.string.minus_sign_with_digit, feedBackerViewModel.minus.value?:0)
        plusNumberTextView.text = getString(R.string.plus_sign_with_digit, feedBackerViewModel.plus.value?:0)
    }

    override fun onDestroyView() {
        Log.d(LogTag.FEEDBACKER_FRAGMENT, "Destroying view")
        super.onDestroyView()
        _binding = null
    }

    private fun endGame() {
        context?.let {
            ShowDialog().create(
                it,
                getString(R.string.game_end),
                getString(R.string.i_win),
                getString(android.R.string.ok),
                onPositiveClick = {
                    findNavController().navigate(
                        R.id.action_nav_feedbacker_to_nav_home
                    )
                }
            )
        }
        Log.d(LogTag.FEEDBACKER_FRAGMENT,"...game end")
    }

    private fun fillDigitCells(num: Int){
        val numArray = Utility.numToArray(num)
        guessedNumberTextView.guessingNumber1.text = numArray[0].toString()
        guessedNumberTextView.guessingNumber2.text = numArray[1].toString()
        guessedNumberTextView.guessingNumber3.text = numArray[2].toString()
        guessedNumberTextView.guessingNumber4.text = numArray[3].toString()
    }

}