package com.example.penasim.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.domain.GameSchedule
import com.example.penasim.game.ExecuteGameByOne
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InGameViewModel @Inject constructor(
  private val executeGameByOne: ExecuteGameByOne,
  private val playerInfoUseCase: PlayerInfoUseCase,
  private val gameScheduleUseCase: GameScheduleUseCase
) : ViewModel() {
  private val _uiState = MutableStateFlow(InGameInfo())
  val uiState: StateFlow<InGameInfo> = _uiState.asStateFlow()

  private lateinit var schedule: GameSchedule

  init {
    viewModelScope.launch {

    }
  }
}