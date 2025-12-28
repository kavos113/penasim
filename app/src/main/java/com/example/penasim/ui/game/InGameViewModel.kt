package com.example.penasim.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.OrderType
import com.example.penasim.game.ExecuteGameByOne
import com.example.penasim.ui.common.GetDisplayFielder
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InGameViewModel @Inject constructor(
  private val executeGameByOne: ExecuteGameByOne,
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val getDisplayFielder: GetDisplayFielder
) : ViewModel() {
  private val _uiState = MutableStateFlow(InGameInfo())
  val uiState: StateFlow<InGameInfo> = _uiState.asStateFlow()

  private lateinit var schedule: GameSchedule

  fun setDate(date: LocalDate) {
    _uiState.update { currentState ->
      currentState.copy(
        date = date
      )
    }
  }

  init {
    viewModelScope.launch {
      val schedules = gameScheduleUseCase.getByDate(uiState.value.date)
      schedule = schedules.find { it.awayTeam.id == Constants.TEAM_ID || it.homeTeam.id == Constants.TEAM_ID } ?: throw IllegalArgumentException("unknown schedule")

      val homePlayers = getDisplayFielder.getMainMember(schedule.homeTeam, OrderType.NORMAL)
      val awayPlayers = getDisplayFielder.getMainMember(schedule.awayTeam, OrderType.NORMAL)

      executeGameByOne.start(schedule.homeTeam, uiState.value.date)

      _uiState.update { currentState ->
        currentState.copy(
          homeTeam = InGameTeamInfo(
            players = homePlayers,
          ),
          awayTeam = InGameTeamInfo(
            players = awayPlayers,
            activePlayerId = awayPlayers.find { it.number == 1 }?.id,
            activeNumber = 1
          )
        )
      }
    }
  }

  // return true if finished
  fun next(): Boolean {
    if (!executeGameByOne.next()) {
      viewModelScope.launch {
        executeGameByOne.postFinishGame()
      }
      return true
    }
    return false
  }
}