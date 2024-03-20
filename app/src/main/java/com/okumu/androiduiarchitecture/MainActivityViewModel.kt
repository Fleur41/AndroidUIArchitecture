package com.okumu.androiduiarchitecture

import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

class MainActivityViewModel : ViewModel() {
    val guessedNumber: MutableLiveData<String> = MutableLiveData("")
    val userGuess:LiveData<String> = MutableLiveData("Your guess is: ${guessedNumber.value}")

    private val _diceScore: MutableLiveData<Int> = MutableLiveData(0)
    val diceScore: LiveData<String> = MutableLiveData("Dice roll was: ${_diceScore.value}")

    private val _totalScore: MutableLiveData<Int> = MutableLiveData(0)
    val totalScore: LiveData<Int> = _totalScore

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _hasUserWon: MutableLiveData<Boolean?> = MutableLiveData(null)
    val hasUserWon: LiveData<Boolean?> = _hasUserWon

    //roll dice
    private fun rollDice(): Int{
        val num = (Math.random() * 6) + 1
        val rounded = ceil(num)
        return rounded.toInt()
    }

    //play game
    fun playGame(){
        println("Play game has been called")
        //launch background activity to indicate/simulate loading
        viewModelScope.launch {
            _loading.postValue ( true)
            //show short delay (3s)
            delay(3000)
            _diceScore.postValue(rollDice())
            //check for winning
            if(guessedNumber.value?.toInt() == _diceScore.value){
                // user has won
                val currentScore = _totalScore.value ?: 0
                _totalScore.postValue(currentScore + 1)
                _hasUserWon.postValue(true)
            } else{
                //user has lost
                _hasUserWon.postValue(false)
            }

            //stop loading
            _loading.postValue(false)
        }
    }
}

// Custom UI Binding
@BindingAdapter("hasUserWon")
fun TextView.bindHasWon(won: Boolean?){
    won?.let{ hasWon ->
        if (hasWon){
            visibility = View.VISIBLE
        } else{
            View.GONE
        }
    }
}

@BindingAdapter("hasUserLost")
fun TextView.bindHasLost(lost: Boolean?){
    lost?.let{ hasLost ->
        if(hasLost){
            visibility = View.VISIBLE
        } else{
            View.GONE
        }
    }
}

@BindingAdapter("isLoading")
fun ProgressBar.isLoading(loading:Boolean){
    visibility = if(loading){
        android.view.View.VISIBLE
    } else {
        android.view.View.GONE
    }
}
