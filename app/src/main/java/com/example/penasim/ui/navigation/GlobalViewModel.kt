package com.example.penasim.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.usecase.GetGameInfoAllUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val getGameInfoAllUseCase: GetGameInfoAllUseCase
): ViewModel() {
    private val _state = MutableStateFlow(GlobalState())
    val state: StateFlow<GlobalState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val gameInfos = getGameInfoAllUseCase.execute()
            val currentDay = gameInfos.maxOfOrNull { it.fixture.date }?.plusDays(1)
                ?: Constants.START

            _state.update { currentState ->
                currentState.copy(
                    currentDay = currentDay
                )
            }
        }
    }

    fun nextDay() {
        println("Next Day Called: ${_state.value.currentDay}")
        _state.update { currentState ->
            currentState.copy(
                currentDay = currentState.currentDay.plusDays(1)
            )
        }
    }
}