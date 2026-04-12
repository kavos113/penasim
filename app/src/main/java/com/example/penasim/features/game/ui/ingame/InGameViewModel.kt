package com.example.penasim.features.game.ui.ingame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.core.session.SelectedTeamStore
import com.example.penasim.features.game.application.ExecuteGameByOne
import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.command.domain.OrderType
import com.example.penasim.features.player.domain.Position
import com.example.penasim.features.command.usecase.DisplayFielderUseCase
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InGameViewModel @Inject constructor(
  private val selectedTeamStore: SelectedTeamStore,
  private val executeGameByOne: ExecuteGameByOne,
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val displayFielderUseCase: DisplayFielderUseCase,
  private val inGameInfoAssembler: InGameInfoAssembler
) : ViewModel() {
  private val _uiState = MutableStateFlow(InGameInfo())
  val uiState: StateFlow<InGameInfo> = _uiState.asStateFlow()

  private lateinit var schedule: GameSchedule
  private var isInitialized = false

  fun setDate(date: LocalDate) {
    _uiState.update { currentState ->
      currentState.copy(
        date = date
      )
    }

    if (!isInitialized) {
      initialize()
      isInitialized = true
    }
  }

  private fun initialize() {
    viewModelScope.launch {
      val schedules = gameScheduleUseCase.getByDate(uiState.value.date)
      val currentTeamId = selectedTeamStore.currentTeamId()
      schedule = schedules.find { it.awayTeam.id == currentTeamId || it.homeTeam.id == currentTeamId }
        ?: throw IllegalArgumentException("unknown schedule")

      val homePlayers = displayFielderUseCase.getMainMember(schedule.homeTeam, OrderType.NORMAL)
      val awayPlayers = displayFielderUseCase.getMainMember(schedule.awayTeam, OrderType.NORMAL)

      executeGameByOne.start(schedule.homeTeam, uiState.value.date)

      _uiState.update { currentState ->
        currentState.copy(
          homeTeam = InGameTeamInfo(
            name = schedule.homeTeam.name,
            players = homePlayers,
            activePlayerId = homePlayers.find { it.position == Position.PITCHER }?.id ?: 0
          ),
          awayTeam = InGameTeamInfo(
            name = schedule.awayTeam.name,
            players = awayPlayers,
            activePlayerId = awayPlayers.find { it.number == 1 }?.id ?: 0,
            activeNumber = 1
          )
        )
      }
    }
  }

  // return true if finished
  fun next(): Boolean {
    val (flag, result) = executeGameByOne.next()
    _uiState.update { currentState ->
      inGameInfoAssembler.applySnapshot(currentState, result, schedule)
    }

    if (!flag) {
      viewModelScope.launch {
        executeGameByOne.postFinishGame()
      }
      return true
    }
    return false
  }

  fun skip() {
    while (true) {
      val (flag, result) = executeGameByOne.next()
      _uiState.update { currentState ->
        inGameInfoAssembler.applySnapshot(currentState, result, schedule)
      }

      if (!flag) {
        break
      }
    }
    viewModelScope.launch {
      executeGameByOne.postFinishGame()
    }
  }
}
