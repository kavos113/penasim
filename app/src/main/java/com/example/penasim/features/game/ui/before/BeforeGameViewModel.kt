package com.example.penasim.features.game.ui.before

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.core.session.SelectedTeamStore
import com.example.penasim.features.command.domain.OrderType
import com.example.penasim.features.command.usecase.DisplayFielderUseCase
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import com.example.penasim.features.standing.usecase.RankingUseCase
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
  private val selectedTeamStore: SelectedTeamStore,
  private val rankingUseCase: RankingUseCase,
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val displayFielderUseCase: DisplayFielderUseCase
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
      val currentTeamId = selectedTeamStore.currentTeamId()

      val schedules = gameScheduleUseCase.getByDate(uiState.value.date)
      val mySchedule = schedules.find {
        it.homeTeam.id == currentTeamId || it.awayTeam.id == currentTeamId
      }?: return@launch

      val homeStartingPlayers = displayFielderUseCase.getStartingMember(mySchedule.homeTeam, OrderType.NORMAL)
      val awayStartingPlayers = displayFielderUseCase.getStartingMember(mySchedule.awayTeam, OrderType.NORMAL)

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
