package com.src.mathmind.ui.guesser

import ShowDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.src.mathmind.MainActivity
import com.src.mathmind.R
import com.src.mathmind.databinding.FragmentGuesserBinding
import com.src.mathmind.databinding.ProgressBarBinding
import com.src.mathmind.models.GuessModel
import com.src.mathmind.utils.ERROR_CONSTANTS
import com.src.mathmind.utils.LogTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuesserFragment : Fragment() {

    private var _binding: FragmentGuesserBinding? = null

    private lateinit var scoreBoard: TextView
    private lateinit var turnCountBoard: TextView
    private lateinit var digitCell1: EditText
    private lateinit var digitCell2: EditText
    private lateinit var digitCell3: EditText
    private lateinit var digitCell4: EditText
    private lateinit var guessButton: Button
    private lateinit var guesserHeader: TextView
    private lateinit var progressBar: ProgressBarBinding
    private lateinit var guessedListView: ListView
    private lateinit var guessNumberCells: Array<EditText>
    private lateinit var userName: String
    private var highlightingActive = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        } else {
            throw IllegalStateException("GuesserFragment must be attached to MainActivity")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val guessViewModel =
            ViewModelProvider(this)[GuessViewModel::class.java]

        _binding = FragmentGuesserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPreferences = context?.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        userName = sharedPreferences?.getString("userName", null) ?: "User"
        progressBar = binding.progressBar
        scoreBoard = binding.scoreTextField
        turnCountBoard = binding.turnCountBoard
        guessedListView = binding.guestureHistoryList
        guessButton = binding.guessingButton
        digitCell1 = binding.guessingNumber1
        digitCell2 = binding.guessingNumber2
        digitCell3 = binding.guessingNumber3
        digitCell4 = binding.guessingNumber4
        guesserHeader = binding.guestureHistoryHeader
        mainActivity.setShowSignOutVisible(true)

        val guessHistoryAdapter: GuestureHistoryAdapter? = context?.let { cont ->
            guessViewModel.guessed_number_list.value?.let { guessNumList ->
                GuestureHistoryAdapter(cont, guessNumList)
            }
        }

        guessedListView.adapter = guessHistoryAdapter

        guessNumberCells = arrayOf(digitCell1, digitCell2, digitCell3, digitCell4)

        for (editText in guessNumberCells) {
            assignTextChangeListener(editText, root)
            focusListenerForDigitCells(editText)
        }

        guessButton.setOnClickListener {
            progressBar.progressBar.visibility = View.VISIBLE
            makeListElementsColorDefault()
//            numberArray will be null in case validation fails
            val numberArray = guessViewModel.validateCells(
                guessNumberCells
            )

            if (numberArray != null) { // validate cell can return null if any duplicated num
                lifecycleScope.launch {
                    when (val result = guessViewModel.startEvaluation(numberArray)) {
                        is Int -> {
                            guessViewModel.getGuessCount().run { updateTurn(this) }
                            guessViewModel.score.value.let {
                                if (it != null) {
                                    updateScore(it.getPoint())
                                }
                            }
                            guessHistoryAdapter?.notifyDataSetChanged()
                        }

                        is GuessModel -> highlightValues(result)
                        null -> {
                            if (!guessViewModel.saveScore(mainActivity.callService(), userName)) {
                                showErrorDialog()
                            }
                            endGame(guessViewModel.score.value!!.getPoint())
                        }
                    }
                }
                if (!highlightingActive) for (cell in guessNumberCells) cell.text.clear()
                else highlightingActive = false
                digitCell1.requestFocus()
            }
            progressBar.progressBar.visibility = View.GONE
        }
        updateScore(0)
        updateTurn(0)
        return root
    }


    private fun highlightValues(data: GuessModel) {
        highlightingActive = true

        // Highlight the entire list
        highLightListFromHistory(data, R.color.red_highlight, true)

        // Define a list to store the deferred results of each highlight coroutine
        val highlightTasks = mutableListOf<Deferred<Unit>>()

        // Launch a coroutine for each digit cell highlighting
        guessNumberCells.forEach { digitCell ->
            digitCell.let { cell ->
                val highlightTask = lifecycleScope.async {
                    highlightElement(cell, R.color.red_highlight, true)
                }
                highlightTasks.add(highlightTask)
            }
        }

        lifecycleScope.launch {
            // Increment the IdlingResource count before starting the asynchronous operation
            mainActivity.getIdlingTool().setIdleState(false)

            // Launch the coroutine on a background thread
            CoroutineScope(Dispatchers.Default).launch {
                highlightTasks.awaitAll()

                // After all highlighting is done, clear the text content of digit cells
                clearDigitCells()

                // Decrement the IdlingResource count after the asynchronous operation is complete
                mainActivity.getIdlingTool().setIdleState(true)
            }
        }
    }

    private fun clearDigitCells() {
        guessNumberCells.forEach { it.text.clear() }
    }


    private suspend fun highlightElement(
        obj: View,
        color: Int = R.color.red_highlight,
        flash: Boolean = true,
        repeatCount: Int = 5,
        timeOut: Long = 100,
    ) {
        val highlightingColor: Int? = context?.let { ContextCompat.getColor(it, color) }
        val background = obj.background

        // Determine the actual repeat count based on whether flashing is enabled
        val actualRepeatCount = if (flash && background is Drawable) repeatCount else 1

        withContext(Dispatchers.Main) {
            // Repeat the highlighting for the specified number of times
            repeat(actualRepeatCount) {
                // Highlight with the specified color
                if (highlightingColor is Int) obj.setBackgroundColor(highlightingColor)
                // Delay for a specified time
                delay(timeOut)
                // Restore the original background color
                obj.background = background
                // Delay for a specified time
                delay(timeOut)
            }
        }
    }


    private fun makeListElementsColorDefault() {
        val obj: ListView? = view?.findViewById(R.id.guesture_history_list)
        val childCount: Int? = obj?.childCount
        if (childCount is Int && childCount > 0) {
            for (i in 0 until childCount) {
                val obj2: TextView? = obj.getChildAt(i)?.findViewById(R.id.guessed_number)
                val color = context?.let { ContextCompat.getColor(it, R.color.white) }
                color?.let {
                    obj2?.setBackgroundColor(it)
                }
            }
        }
    }

    private fun highLightListFromHistory(
        data: GuessModel,
        color: Int = R.color.red_highlight,
        flash: Boolean,
    ) {
        val element: View? = findListElementByText(
            R.id.guesture_history_list,
            R.id.guessed_number,
            data.guessedNumber.toString()
        )
        if (element != null) {
            lifecycleScope.launch {
                highlightElement(element, color, flash)
            }
        }

    }

    private fun findListElementByText(listViewId: Int, childViewId: Int, text: String): View? {
        val obj: ListView? = view?.findViewById(listViewId)
        val childCount: Int? = obj?.childCount
        if (childCount is Int && childCount > 0) {
            for (i in 0 until childCount) {
                val obj_2: TextView? = obj.getChildAt(i)?.findViewById(childViewId)
                if (obj_2?.text?.equals(text) == true) {
                    return obj_2
                }
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun assignTextChangeListener(textInput: EditText, rootView: View) {

        textInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    val currentFocus = rootView.id
                    guessNumberCells.forEach {
                        if (it.id != currentFocus && it.text.isNullOrBlank()) {
                            it.requestFocus()
                            println("focus on number cell: ${it.id} and it found in root ${rootView.findFocus().id}")
                            return
                        }
                    }
                }
            }
        })
    }

    private fun focusListenerForDigitCells(view: View) {
        view.setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus && it is EditText) {
                println("focus on ${it.id} and cleaning cell")
                it.text.clear()
            }
        }

    }

    private fun endGame(point: Int) {

        context?.let {
            ShowDialog().create(
                it,
                getString(R.string.game_end),
                getString(R.string.you_win,point),
                getString(android.R.string.ok),
                null,
                onPositiveClick = {
                    Log.d(LogTag.GUESSER_FRAGMENT, "Navigating to home")
                    findNavController().navigate(R.id.action_nav_guesser_to_nav_home)
                }
            )
        }
        mainActivity.updateScores()
        println("game end: $point")
    }

    private fun showErrorDialog() {

        context?.let {
            val showDialog = ShowDialog()
            showDialog.create(
                it,
                ERROR_CONSTANTS.ERROR_HEADER,
                ERROR_CONSTANTS.SCORE_SAVE_FAILURE,
                getString(android.R.string.ok)
            )
        }
    }

    private fun updateScore(point: Int) {
        scoreBoard.text = context?.getString(R.string.user_score, userName, point)
    }

    private fun updateTurn(count: Int) {
        turnCountBoard.text = context?.getString(R.string.turn_count, count)
    }

}
