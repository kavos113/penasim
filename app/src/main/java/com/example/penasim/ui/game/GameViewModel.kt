package com.example.penasim.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penasim.const.Constants
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.League
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position
import com.example.penasim.domain.isStarting
import com.example.penasim.game.ExecuteGamesByDate
import com.example.penasim.ui.common.color
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.toGameUiInfo
import com.example.penasim.ui.common.toRankingUiInfo
import com.example.penasim.usecase.FielderAppointmentUseCase
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
class GameViewModel @Inject constructor(
    private val executeGamesByDate: ExecuteGamesByDate,
    private val inningScoreUseCase: InningScoreUseCase,
    private val pitchingScoreUseCase: PitchingStatUseCase,
    private val fielderAppointmentUseCase: FielderAppointmentUseCase,
    private val getRankingUseCase: RankingUseCase,
    private val gameScheduleUseCase: GameScheduleUseCase,
    private val playerInfoUseCase: PlayerInfoUseCase,
    private val homeRunUseCase: HomeRunUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var schedule: GameSchedule

    fun setDate(date: LocalDate) {
        _uiState.update { currentState ->
            currentState.copy(date = date)
        }
    }

    init {
        viewModelScope.launch {
            val ranking =
                (getRankingUseCase.getByLeague(League.L1) + getRankingUseCase.getByLeague(League.L2))
                    .sortedBy { it.rank }

            val schedules = gameScheduleUseCase.getByDate(uiState.value.date)
            val s = schedules.find {
                it.homeTeam.id == Constants.TEAM_ID || it.awayTeam.id == Constants.TEAM_ID
            }
            if (s == null) {
                skipGame()
                return@launch
            } else {
                schedule = s
            }

            val homeFielderAppointment = fielderAppointmentUseCase.getByTeam(schedule.homeTeam)
            val awayFielderAppointment = fielderAppointmentUseCase.getByTeam(schedule.awayTeam)

            val homePlayers = playerInfoUseCase.getByTeamId(schedule.homeTeam.id)
            val awayPlayers = playerInfoUseCase.getByTeamId(schedule.awayTeam.id)

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
                    homePlayers = homePlayers,
                    awayPlayers = awayPlayers,
                    beforeGameInfo = BeforeGameInfo(
                        homeTeam = ranking.find { it.team.id == schedule.homeTeam.id }
                            ?: throw IllegalStateException("No ranking found for team ${schedule.homeTeam.id}"),
                        awayTeam = ranking.find { it.team.id == schedule.awayTeam.id }
                            ?: throw IllegalStateException("No ranking found for team ${schedule.awayTeam.id}"),
                        homeStartingPlayers = homeStartingPlayers,
                        awayStartingPlayers = awayStartingPlayers
                    ),
                )
            }
        }
    }

    fun skipGame() {
        viewModelScope.launch {
            val recentGames = executeGamesByDate.execute(uiState.value.date)
            val rankings =
                (getRankingUseCase.getByLeague(League.L1) + getRankingUseCase.getByLeague(League.L2))
                    .sortedBy { it.rank }
                    .map { it.toRankingUiInfo() }

            _uiState.update { currentState ->
                currentState.copy(
                    homePlayers = emptyList(),
                    awayPlayers = emptyList(),
                    beforeGameInfo = BeforeGameInfo(),
                    afterGameInfo = AfterGameInfo(
                        rankings = rankings,
                        games = recentGames.map { it.toGameUiInfo() }
                    )
                )
            }
        }
    }

    fun startGame() {
        viewModelScope.launch {
            val recentGames = executeGamesByDate.execute(uiState.value.date)
            val inningScores = inningScoreUseCase.getByFixtureId(schedule.fixture.id)
            val pitchingStats = pitchingScoreUseCase.getByFixtureId(schedule.fixture.id)
            val ranking =
                (getRankingUseCase.getByLeague(League.L1) + getRankingUseCase.getByLeague(League.L2))
                    .sortedBy { it.rank }
                    .map { it.toRankingUiInfo() }

            val homePitchingStats =
                pitchingStats.filter { uiState.value.homePlayers.any { p -> p.player.id == it.playerId } }
            val awayPitchingStats =
                pitchingStats.filter { uiState.value.awayPlayers.any { p -> p.player.id == it.playerId } }
            val homePitcherResults = homePitchingStats.map {
                it.toPitcherResult(uiState.value.homePlayers.find { p -> p.player.id == it.playerId }!!)
            }
            val awayPitcherResults = awayPitchingStats.map {
                it.toPitcherResult(uiState.value.awayPlayers.find { p -> p.player.id == it.playerId }!!)
            }

            val homeRuns = homeRunUseCase.getByFixtureId(schedule.fixture.id)
            val homeFielderResults = homeRuns
                .filter { uiState.value.homePlayers.any { p -> p.player.id == it.playerId } }
                .groupBy { it.playerId }
                .map { (playerId, homeRuns) ->
                    homeRuns.mapIndexed { index, homeRun ->
                        homeRun.toFielderResult(
                            uiState.value.homePlayers.find { p -> p.player.id == playerId }!!,
                            index
                        )
                    }
                }.flatten()
            val awayFielderResults = homeRuns
                .filter { uiState.value.awayPlayers.any { p -> p.player.id == it.playerId } }
                .groupBy { it.playerId }
                .map { (playerId, homeRuns) ->
                    homeRuns.mapIndexed { index, homeRun ->
                        homeRun.toFielderResult(
                            uiState.value.awayPlayers.find { p -> p.player.id == playerId }!!,
                            index
                        )
                    }
                }.flatten()

            _uiState.update { currentState ->
                currentState.copy(
                    homePlayers = playerInfoUseCase.getByTeamId(schedule.homeTeam.id),
                    awayPlayers = playerInfoUseCase.getByTeamId(schedule.awayTeam.id),
                    afterGameInfo = AfterGameInfo(
                        homeScores = inningScores.filter { it.teamId == schedule.homeTeam.id },
                        awayScores = inningScores.filter { it.teamId == schedule.awayTeam.id },
                        homePitcherResults = homePitcherResults,
                        awayPitcherResults = awayPitcherResults,
                        homeFielderResults = homeFielderResults,
                        awayFielderResults = awayFielderResults,
                        rankings = ranking,
                        games = recentGames.map { it.toGameUiInfo() }
                    )
                )
            }
        }
    }
}