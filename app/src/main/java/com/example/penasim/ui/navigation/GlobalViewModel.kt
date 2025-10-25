package com.example.penasim.ui.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(): ViewModel() {
    private val _state = MutableStateFlow(GlobalState())
    val state: StateFlow<GlobalState> = _state.asStateFlow()

    fun nextDay() {
        println("Next Day Called: ${_state.value.currentDay}")
        _state.update { currentState ->
            currentState.copy(
                currentDay = currentState.currentDay.plusDays(1)
            )
        }
    }
}