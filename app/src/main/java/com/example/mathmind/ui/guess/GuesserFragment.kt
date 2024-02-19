package com.example.mathmind.ui.guess

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.mathmind.R
import com.example.mathmind.databinding.FragmentGuesserBinding
import com.example.mathmind.models.GuessModel
import com.example.mathmind.models.GuessViewModel
import com.example.mathmind.utils.RandomGenerator
import com.example.mathmind.utils.Utility.Companion.arrayToNum
import com.example.mathmind.utils.Utility.Companion.highlightElement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GuesserFragment : Fragment() {

    private var _binding: FragmentGuesserBinding? = null

    lateinit var digit_cell_1: EditText
    lateinit var digit_cell_2: EditText
    lateinit var digit_cell_3: EditText
    lateinit var digit_cell_4: EditText
    lateinit var guess_button: Button
    lateinit var guesser_header: TextView
    lateinit var guessNumberCells: Array<EditText>
    var guessed_number_list: MutableList<GuessModel> = mutableListOf()
    lateinit var guessed_list_view: ListView
    val numberKept = RandomGenerator().generateRandom4uniqueDigit(4)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val guessViewModel =
            ViewModelProvider(this).get(GuessViewModel::class.java)

        _binding = FragmentGuesserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        guessed_list_view = binding.guestureHistoryList
        guess_button = binding.guessingButton
        digit_cell_1 = binding.guessingNumber1
        digit_cell_2 = binding.guessingNumber2
        digit_cell_3 = binding.guessingNumber3
        digit_cell_4 = binding.guessingNumber4
        guesser_header = binding.guestureHistoryHeader
        val guessHistoryAdapter = GuestureHistoryAdapter(requireContext(), guessed_number_list)
        guessed_list_view.adapter = guessHistoryAdapter

        guessNumberCells = arrayOf(
            digit_cell_1,
            digit_cell_2,
            digit_cell_3,
            digit_cell_4
        )

        guessNumberCells.forEach {
            assignTextChangeListener(it, root)
            focusListenerForDigitCells(it)
        }

        guess_button.setOnClickListener {
            makeListElementsColorDefault()
            val numberArray = validateCells(guessNumberCells)

            if (numberArray != null)  // validate cell can return null if any duplicated num
            {
                val data = GuessModel(
                    arrayToNum(numberArray), // guessed number
                    0,//correctly placed number
                    0 // numbers on wrong place
                )
                evaluateNumber(data) // decide about feedback
                if (guessed_number_list.contains(data)) {
                    lifecycleScope.launch {
                        highLightListFromHistory(data, R.color.red_highlight, true)
                    }
                    lifecycleScope.launch {
                        highlightElement(digit_cell_1 ,R.color.red_highlight, requireContext(),true)
                    }
                    lifecycleScope.launch {
                        highlightElement(digit_cell_2 ,R.color.red_highlight, requireContext(),true)
                    }
                    lifecycleScope.launch {
                        highlightElement(digit_cell_3 ,R.color.red_highlight, requireContext(),true)
                    }
                    lifecycleScope.launch {
                        highlightElement(digit_cell_4 ,R.color.red_highlight, requireContext(),true)
                    }
                } else if (data.placedNumber < 4) {
                    guessed_number_list.add(data)
                    lifecycleScope.launch {
                        updateGuessCount() //might be async
                        guessHistoryAdapter.notifyDataSetChanged()
                    }
                } else {
                    endGame()
                }
            }


//        val textView: TextView = binding.guestureHistoryList
//        guessViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        }
        return root
    }

    private fun makeListElementsColorDefault() {
        val obj: ListView? = view?.findViewById(R.id.guesture_history_list)
        val childCount: Int? = obj?.childCount
            if (childCount is Int && childCount > 0) {
                for (i in 0 until childCount) {
                    val obj_2 : TextView? = obj?.getChildAt(i)?.findViewById(R.id.guessed_number)
                    val color = context?.let { ContextCompat.getColor(it, R.color.white) }
                    color?.let { obj_2?.setBackgroundColor(it)
                }
            }
        }
    }

    private fun updateGuessCount(){
        if(guessed_number_list.size>0){
            val countText = resources.getString(R.string.numbers_count, guessed_number_list.size)
            val headerText = resources.getString(R.string.guessed_numbers_list_header)
            val updatedHeader = "$headerText $countText"
            guesser_header.text = updatedHeader
        }

    }

    private suspend fun highLightListFromHistory(data: GuessModel, color:Int=R.color.red_highlight, flash:Boolean=false) {

        val element: View? = findListElementByText(
            R.id.guesture_history_list,
            R.id.guessed_number,
            data.guessedNumber.toString()
        )
        if(element is View){
            lifecycleScope.launch{
                highlightElement( element, color,  requireContext(),flash )
            }
        }

    }

    private fun findListElementByText(listViewId: Int, childViewId:Int, text:String):View?{
        val obj: ListView? = view?.findViewById(listViewId)
        val childCount: Int? = obj?.childCount
        if (childCount is Int && childCount > 0) {
            for (i in 0 until childCount) {
                val obj_2 : TextView? = obj.getChildAt(i)?.findViewById(childViewId)
                if (obj_2?.text?.equals(text) == true){
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                lifecycleScope.launch{
                    highlightElement(textInput, R.color.yellow_highlight,  requireContext(),false )
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    var currentFocus = rootView.id
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
//                setFocussedCell(it.id)
                it.setText("")
            }
        }

    }

    private fun validateCells(numCellArray: Array<EditText>): IntArray? {
        val iterateCells: Iterator<EditText> = numCellArray.iterator()
        val guessed_number = IntArray(4)
        var i = 0
        while (iterateCells.hasNext()) {
            val cell: EditText = iterateCells.next()
            val number = if(cell.text!=null) cell.text.toString().toInt() else -1
            if (number < 0 || guessed_number.contains(number)) {
                cell.requestFocus()
                return null
            } else {
                guessed_number[i++] = number
            }
        }
        println(" guessed_number is ${guessed_number.contentToString()}")
        return guessed_number
    }

    private fun evaluateNumber(data: GuessModel) {

        if (data.guessedNumber == numberKept) {
            data.placedNumber = 4
            return
        } else {
            val keptNumArray =
                numberKept.toString().toCharArray().map { it.toString().toInt() }.toIntArray()

            var tempNumber = data.guessedNumber
            var divisor = 1000
            var index = 0
            while (divisor > 0) {
                val digit = tempNumber / divisor
                if (keptNumArray[index++] == digit) {
                    data.placedNumber++
                } else if (keptNumArray.contains(digit)) {
                    data.notPlacedNumber++
                }
                tempNumber %= divisor
                divisor /= 10
            }
        }
    }

    private fun endGame() {
        println("game end")
//        TODO(end of the game)
    }

}
