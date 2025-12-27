package com.example.penasim.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.game.ExecuteGamesByDate
import com.example.penasim.ui.common.toGameUiInfo
import com.example.penasim.ui.common.toRankingUiInfo
import com.example.penasim.usecase.GameInfoUseCase
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.HomeRunUseCase
import com.example.penasim.usecase.InningScoreUseCase
import com.example.penasim.usecase.PitchingStatUseCase
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
class AfterGameViewModel @Inject constructor(
  private val gameScheduleUseCase: GameScheduleUseCase,
  private val executeGamesByDate: ExecuteGamesByDate,
  private val inningScoreUseCase: InningScoreUseCase,
  private val pitchingScoreUseCase: PitchingStatUseCase,
  private val homeRunUseCase: HomeRunUseCase,
  private val rankingUseCase: RankingUseCase,
  private val playerInfoUseCase: PlayerInfoUseCase,
  private val gameInfoUseCase: GameInfoUseCase
) : ViewModel() {
  private val _uiState = MutableStateFlow(AfterGameInfo())
  val uiState: StateFlow<AfterGameInfo> = _uiState.asStateFlow()

  fun setDate(date: LocalDate) {
    _uiState.update { currentState ->
      currentState.copy(date = date)
    }
  }

  fun runGame() {
    viewModelScope.launch {
      _uiState.update { it.copy(isRunning = true) }
      executeGamesByDate.execute(uiState.value.date)
      _uiState.update { it.copy(isRunning = false) }
    }
  }

  fun skipGame() {
    viewModelScope.launch {
      val ranking = rankingUseCase.getAll()
        .map { it.toRankingUiInfo() }
      val recentGames = gameInfoUseCase.getByDate(uiState.value.date)

      _uiState.update { currentState ->
        currentState.copy(
          rankings = ranking,
          games = recentGames.map { it.toGameUiInfo() }
        )
      }
    }
  }

  fun initData() {
    viewModelScope.launch {
      val schedules = gameScheduleUseCase.getByDate(uiState.value.date)
      val mySchedule = schedules.find {
        it.homeTeam.id == Constants.TEAM_ID || it.awayTeam.id == Constants.TEAM_ID
      }?: return@launch

      val homePlayers = playerInfoUseCase.getByTeamId(mySchedule.homeTeam.id)
      val awayPlayers = playerInfoUseCase.getByTeamId(mySchedule.awayTeam.id)

      val inningScores = inningScoreUseCase.getByFixtureId(mySchedule.fixture.id)
      val pitchingStats = pitchingScoreUseCase.getByFixtureId(mySchedule.fixture.id)
      val ranking = rankingUseCase.getAll()
        .map { it.toRankingUiInfo() }

      val homePitchingStats =
        pitchingStats.filter { homePlayers.any { p -> p.player.id == it.playerId } }
      val awayPitchingStats =
        pitchingStats.filter { awayPlayers.any { p -> p.player.id == it.playerId } }
      val homePitcherResults = homePitchingStats.map {
        it.toPitcherResult(homePlayers.find { p -> p.player.id == it.playerId }!!)
      }
      val awayPitcherResults = awayPitchingStats.map {
        it.toPitcherResult(awayPlayers.find { p -> p.player.id == it.playerId }!!)
      }

      val homeRuns = homeRunUseCase.getByFixtureId(mySchedule.fixture.id)
      val homeFielderResults = homeRuns
        .filter { homePlayers.any { p -> p.player.id == it.playerId } }
        .groupBy { it.playerId }
        .map { (playerId, homeRuns) ->
          homeRuns.mapIndexed { index, homeRun ->
            homeRun.toFielderResult(
              homePlayers.find { p -> p.player.id == playerId }!!,
              index
            )
          }
        }.flatten()
      val awayFielderResults = homeRuns
        .filter { awayPlayers.any { p -> p.player.id == it.playerId } }
        .groupBy { it.playerId }
        .map { (playerId, homeRuns) ->
          homeRuns.mapIndexed { index, homeRun ->
            homeRun.toFielderResult(
              awayPlayers.find { p -> p.player.id == playerId }!!,
              index
            )
          }
        }.flatten()

      val recentGames = gameInfoUseCase.getByDate(uiState.value.date)

      _uiState.update { currentState ->
        currentState.copy(
          homeScores = inningScores.filter { it.teamId == mySchedule.homeTeam.id },
          awayScores = inningScores.filter { it.teamId == mySchedule.awayTeam.id },
          homePitcherResults = homePitcherResults,
          awayPitcherResults = awayPitcherResults,
          homeFielderResults = homeFielderResults,
          awayFielderResults = awayFielderResults,
          rankings = ranking,
          games = recentGames.map { it.toGameUiInfo() }
        )
      }
    }
  }
}