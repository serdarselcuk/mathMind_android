package com.example.mathmind.ui.guess

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mathmind.databinding.FragmentGuesserBinding

class GuesserFragment : Fragment() {

    private var _binding: FragmentGuesserBinding? = null

    lateinit var digit_cell_1: EditText
    lateinit var digit_cell_2: EditText
    lateinit var digit_cell_3: EditText
    lateinit var digit_cell_4: EditText
    lateinit var guess_button: Button
    lateinit var guessNumberCells: Array<EditText>
    lateinit var guessed_number: Array<Int?>
    private var focussedCell: Int = -1
    lateinit var guessed_list:ListView


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val guessViewModel =
            ViewModelProvider(this).get(GuessViewModel::class.java)

        _binding = FragmentGuesserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        digit_cell_1 = binding.guessingNumber1
        digit_cell_2 = binding.guessingNumber2
        digit_cell_3 = binding.guessingNumber3
        digit_cell_4 = binding.guessingNumber4

        guessNumberCells = arrayOf(
            digit_cell_1,
            digit_cell_2,
            digit_cell_3,
            digit_cell_4
        )

        guessNumberCells.forEach {
            assignTextChangeListener(it)
            focusListenerForDigitCells(it)
        }

        guess_button = binding.guessingButton

        guess_button.setOnClickListener {
            guessed_number = evaluateCells(guessNumberCells)
        }

        guessed_list = binding.guestureHistoryList

//        val textView: TextView = binding.guestureHistoryList
//        guessViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun assignTextChangeListener(textInput: EditText) {
        textInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                textInput.setBackgroundColor(Color.YELLOW)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               if(s?.length == 1){
                   var currentFocus = focussedCell
                   guessNumberCells.forEach {
                       if (guessNumberCells.indexOf(it) != focussedCell && it.text.isNullOrBlank() ) {
                           it.requestFocus()
                           return
                       }
                   }
                   if(currentFocus == focussedCell)  guess_button.requestFocus()
               }
            }
        })
    }

    fun focusListenerForDigitCells(view: View) {

        view.setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus && it is EditText) {
                println("focus on ${ it.id }")
                setFocussedCell(it.id)
                it.setText("")
            }
        }
    }

    private fun setFocussedCell(id: Int) {
        guessNumberCells.forEach {
            if(it.id == id) focussedCell = guessNumberCells.indexOf(it)
        }
    }

    fun evaluateCells(array:Array<EditText>):Array<Int?>{
        var iterateCells: Iterator<EditText> = array.iterator()
        var guessed_number: Array<Int?> = arrayOfNulls(4)
        var i = 0
        while (iterateCells.hasNext()) {
            var cell: EditText = iterateCells.next()
            var text = cell.text.toString()
            if (text.isBlank() || guessed_number.contains(text.toInt())) {
                cell.requestFocus()
                return arrayOfNulls(4)
            } else {
                guessed_number[i++] = text.toInt()
            }
        }
        println(" guessed_number is ${guessed_number.contentToString()}")
        return guessed_number
    }
}