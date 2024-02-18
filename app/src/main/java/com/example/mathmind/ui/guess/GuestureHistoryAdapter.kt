package com.example.mathmind.ui.guess

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mathmind.R
import com.example.mathmind.models.GuessModel

class GuestureHistoryAdapter(private val context: Context, private val guessList: List<GuessModel>) : BaseAdapter() {

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
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.gesture, parent, false)
        }else{
            convertView.minimumHeight = 100
        }

        val currentItem = getItem(position) as GuessModel

        val guessedNumberTextView: TextView = convertView?.findViewById(R.id.guessed_number) ?: TextView(context).apply {
            text = "" }
        val placedNumberTextView: TextView = convertView?.findViewById(R.id.placed_number) ?: TextView(context).apply {
            text = "" }
        val notPlacedNumberTextView: TextView = convertView?.findViewById(R.id.not_placed_number) ?: TextView(context).apply {
            text = "" }

        guessedNumberTextView.text = currentItem.guessedNumber.toString()
        placedNumberTextView.text = currentItem.placedNumber.toString()
        notPlacedNumberTextView.text = currentItem.notPlacedNumber.toString()

        return convertView
    }
}