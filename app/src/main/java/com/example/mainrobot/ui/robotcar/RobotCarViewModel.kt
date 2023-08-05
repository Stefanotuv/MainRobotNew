package com.example.mainrobot.ui.robotcar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RobotCarViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is robotcar Fragment"
    }
    val text: LiveData<String> = _text
}