package com.src.mathmind.ui.guesser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.src.mathmind.R
import com.src.mathmind.models.GuessModel

class GuestureHistoryAdapter(
    private val context: Context,
    private val guessList: List<GuessModel>,
) : BaseAdapter() {

    override fun getCount(): Int {
        return guessList.size
    }

    override fun getItem(position: Int): Any {
        return guessList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView1 = convertView
        if (convertView1 == null) {
            val inflater = LayoutInflater.from(context)
            convertView1 = inflater.inflate(R.layout.gesture, parent, false)
        } else {
            convertView1.minimumHeight = 100
        }

        val currentItem = getItem(position) as GuessModel

        val guessedNumberTextView: TextView =
            convertView1?.findViewById(R.id.guessed_number) ?: TextView(context).apply {
                text = ""
            }
        val placedNumberTextView: TextView =
            convertView1?.findViewById(R.id.placed_number) ?: TextView(context).apply {
                text = ""
            }
        val notPlacedNumberTextView: TextView =
            convertView1?.findViewById(R.id.not_placed_number) ?: TextView(context).apply {
                text = ""
            }

        guessedNumberTextView.text = "${currentItem.guessedNumber}"
        placedNumberTextView.text =
            context.getString(R.string.plus_sign_with_digit, currentItem.feedBackData.placedNumber)
        notPlacedNumberTextView.text =
            context.getString(R.string.plus_sign_with_digit, currentItem.feedBackData.nonPlacedNumber)

        return convertView1
    }
}