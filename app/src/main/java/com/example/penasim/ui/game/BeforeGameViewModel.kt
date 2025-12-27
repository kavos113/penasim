package com.example.penasim.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position
import com.example.penasim.domain.isStarting
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.color
import com.example.penasim.usecase.FielderAppointmentUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.PlayerInfoUseCase
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
  private val fielderAppointmentUseCase: FielderAppointmentUseCase,
  private val playerInfoUseCase: PlayerInfoUseCase
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

      val homeFielderAppointment = fielderAppointmentUseCase.getByTeam(mySchedule.homeTeam)
      val awayFielderAppointment = fielderAppointmentUseCase.getByTeam(mySchedule.awayTeam)

      val homePlayers = playerInfoUseCase.getByTeamId(mySchedule.homeTeam.id)
      val awayPlayers = playerInfoUseCase.getByTeamId(mySchedule.awayTeam.id)

      val homeStartingPlayers = homeFielderAppointment
        .filter { it.orderType == OrderType.NORMAL && it.position.isStarting() }
        .map {
          DisplayFielder(
            id = it.playerId,
            displayName = homePlayers.find { player -> player.player.id == it.playerId }?.player?.firstName
              ?: "Unknown Player",
            position = it.position,
            number = it.number,
            color = homePlayers.find { player -> player.player.id == it.playerId }?.primaryPosition?.color()
              ?: Position.OUTFIELDER.color()
          )
        }
        .sortedBy { it.number }

      val awayStartingPlayers = awayFielderAppointment
        .filter { it.orderType == OrderType.NORMAL && it.position.isStarting() }
        .map {
          DisplayFielder(
            id = it.playerId,
            displayName = awayPlayers.find { player -> player.player.id == it.playerId }?.player?.firstName
              ?: "Unknown Player",
            position = it.position,
            number = it.number,
            color = awayPlayers.find { player -> player.player.id == it.playerId }?.primaryPosition?.color()
              ?: Position.OUTFIELDER.color()
          )
        }

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