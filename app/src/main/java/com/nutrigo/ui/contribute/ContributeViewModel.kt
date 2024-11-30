package com.nutrigo.ui.contribute

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContributeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Contribute Fragment"
    }
    val text: LiveData<String> = _text
}