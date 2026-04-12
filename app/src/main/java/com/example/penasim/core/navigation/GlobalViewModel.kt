package com.example.penasim.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.core.session.SelectedTeamStore
import com.example.penasim.features.game.usecase.CurrentDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
  private val currentDayUseCase: CurrentDayUseCase,
  private val selectedTeamStore: SelectedTeamStore
) : ViewModel() {
  private val _state = MutableStateFlow(
    GlobalState(
      currentDay = Constants.START,
      teamId = selectedTeamStore.currentTeamId()
    )
  )
  val state: StateFlow<GlobalState> = _state.asStateFlow()

  init {
    viewModelScope.launch {
      val currentDay = currentDayUseCase.getCurrentDay()

      _state.update { currentState ->
        currentState.copy(
          currentDay = currentDay
        )
      }
    }
  }

  fun nextDay() {
    _state.update { currentState ->
      currentState.copy(
        currentDay = currentState.currentDay.plusDays(1)
      )
    }
  }
}
