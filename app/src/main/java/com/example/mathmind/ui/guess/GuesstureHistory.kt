package com.example.mathmind.ui.guess

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.mathmind.R
import com.example.mathmind.models.GuessModel

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