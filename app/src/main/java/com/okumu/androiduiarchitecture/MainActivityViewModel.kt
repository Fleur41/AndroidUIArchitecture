package com.okumu.androiduiarchitecture

import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

class MainActivityViewModel : ViewModel() {
    val guessedNumber: MutableLiveData<String> = MutableLiveData("")

    private val _userGuess: MutableLiveData<Int> = MutableLiveData(0)
    val userGuess:LiveData<String> = guessedNumber.map {guess ->
        "Your guess is: $guess"
    }


    private val _diceScore: MutableLiveData<Int> = MutableLiveData(0)
    val diceScore: LiveData<String> = _diceScore.map {score ->
        "Dice roll was: $score"
    }

    private val _totalScore: MutableLiveData<Int> = MutableLiveData(0)
    val totalScore: LiveData<Int> = _totalScore

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _hasUserWon: MutableLiveData<Boolean?> = MutableLiveData(null)
    val hasUserWon: LiveData<Boolean?> = _hasUserWon

    //roll dice
    private fun rollDice(): Int{
        val num = (Math.random() * 6)
        val rounded = ceil(num)
        return rounded.toInt()
    }

    //play user
    fun playUser(){
        //assign new value to user's guess from textfield
        //_userGuess.value = guessedNumber.value?.toInt()
        val guessString = guessedNumber.value
        if (!guessString.isNullOrEmpty()) {
            val guess = guessString.toIntOrNull()
            if (guess != null && guess in 1..6) {
                _userGuess.value = guess
                playGame(context)
            } else {
                showToast(context, "Please enter a number between 1 and 6")
            }
        } else {
            showToast(context, "Please enter a number")
        }
    }

    //play dice
    fun playDice(){
        //roll dice and playGame
        playGame()
    }

    //play game
    private fun playGame(){
        println("Play game has been called")
        //launch background activity to indicate/simulate loading
        viewModelScope.launch {

            //Start loading
            _loading.value =  true
            //show short delay (3s)
            delay(3000)
            _diceScore.value = (rollDice())
            //check for winning
            if(_userGuess.value == _diceScore.value){
                // user has won
                val currentScore = _totalScore.value ?: 0
                _totalScore.value = currentScore + 1
                _hasUserWon.value = true
            } else{
                //user has lost
                _hasUserWon.value = false
            }

            //stop loading
            _loading.value = false
        }
    }

    //Function to display a toast message
    private  fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
fun TextView.bindHasLost(won: Boolean?){
    won?.let{ hasWon ->
        visibility = if(!hasWon){
            View.VISIBLE
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
