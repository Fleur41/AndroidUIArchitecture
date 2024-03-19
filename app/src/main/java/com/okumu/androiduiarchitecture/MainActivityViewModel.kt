package com.okumu.androiduiarchitecture

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    val guessedNumber: MutableLiveData<String> = MutableLiveData("")
}