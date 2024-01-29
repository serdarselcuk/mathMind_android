package com.example.mathmind.ui.feedbacker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeedBackerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Feedbacker Fragment"
    }
    val text: LiveData<String> = _text
}