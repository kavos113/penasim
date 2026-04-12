package com.example.penasim.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.domain.OrderType
import com.example.penasim.ui.common.GetDisplayFielder
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.RankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BeforeGameViewModel @Inject constructor(
  private val rankingUseCase: RankingUseCase,
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val getDisplayFielder: GetDisplayFielder
) : ViewModel() {
  private val _uiState = MutableStateFlow(BeforeGameInfo())
  val uiState: StateFlow<BeforeGameInfo> = _uiState.asStateFlow()

  fun setDate(date: LocalDate) {
    _uiState.update { currentState ->
      currentState.copy(date = date)
    }
  }

  init {
    viewModelScope.launch {
      val rankings = rankingUseCase.getAll()

      val schedules = gameScheduleUseCase.getByDate(uiState.value.date)
      val mySchedule = schedules.find {
        it.homeTeam.id == Constants.TEAM_ID || it.awayTeam.id == Constants.TEAM_ID
      }?: return@launch

      val homeStartingPlayers = getDisplayFielder.getStartingMember(mySchedule.homeTeam, OrderType.NORMAL)
      val awayStartingPlayers = getDisplayFielder.getStartingMember(mySchedule.awayTeam, OrderType.NORMAL)

      _uiState.update { currentState ->
        currentState.copy(
          homeTeam = rankings.find { it.team.id == mySchedule.homeTeam.id }
            ?: throw IllegalStateException("No ranking found for team ${mySchedule.homeTeam.id}"),
          awayTeam = rankings.find { it.team.id == mySchedule.awayTeam.id }
            ?: throw IllegalStateException("No ranking found for team ${mySchedule.awayTeam.id}"),
          homeStartingPlayers = homeStartingPlayers,
          awayStartingPlayers = awayStartingPlayers
        )
      }
    }
  }
}