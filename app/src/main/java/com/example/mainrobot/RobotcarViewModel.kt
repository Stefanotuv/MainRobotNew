package com.example.mainrobot
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RobocarViewModel : ViewModel() {
    private val _robocarAddress = MutableLiveData<String>()
    val robocarAddress: LiveData<String> = _robocarAddress

    fun setRobocarAddress(address: String) {
        _robocarAddress.value = address
    }
}
