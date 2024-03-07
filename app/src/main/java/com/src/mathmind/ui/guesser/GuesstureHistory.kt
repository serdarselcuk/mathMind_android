package com.src.mathmind.ui.guesser

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.src.mathmind.R
import com.src.mathmind.models.GuessModel

class GuesstureHistory: Fragment() {

    // ... (other code)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val guessList =  mutableListOf<GuessModel>()

        val guessListAdapter = GuestureHistoryAdapter(requireContext(), guessList)

        val listView: ListView = view.findViewById(R.id.guesture_history_list)
        listView.adapter = guessListAdapter
    }

}