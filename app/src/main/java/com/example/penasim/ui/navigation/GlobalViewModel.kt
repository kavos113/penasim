package com.example.penasim.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GlobalViewModel {
    private val _state = MutableStateFlow(GlobalSate())
    val state: StateFlow<GlobalSate> = _state.asStateFlow()

    fun nextDay() {
        _state.update { currentState ->
            currentState.copy(
                currentDate = currentState.currentDate.plusDays(1),
            )
        }
    }
}