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
import com.example.penasim.ui.command.color
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.toRankingUiInfo
import com.example.penasim.usecase.GetFielderAppointmentByTeamUseCase
import com.example.penasim.usecase.GetGameInfoAllUseCase
import com.example.penasim.usecase.GetGameSchedulesByDateUseCase
import com.example.penasim.usecase.GetHomeRunUseCase
import com.example.penasim.usecase.GetInningScoreUseCase
import com.example.penasim.usecase.GetPitchingStatUseCase
import com.example.penasim.usecase.GetPlayerInfosByTeamUseCase
import com.example.penasim.usecase.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val executeGamesByDate: ExecuteGamesByDate,
    private val getInningScoreUseCase: GetInningScoreUseCase,
    private val getPitchingStatUseCase: GetPitchingStatUseCase,
    private val getRankingUseCase: GetRankingUseCase,
    private val getGameInfoAllUseCase: GetGameInfoAllUseCase,
    private val getGameSchedulesByDateUseCase: GetGameSchedulesByDateUseCase,
    private val getFielderAppointmentByTeamUseCase: GetFielderAppointmentByTeamUseCase,
    private val getPlayerInfosByTeamUseCase: GetPlayerInfosByTeamUseCase,
    private val getHomeRunUseCase: GetHomeRunUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var schedule: GameSchedule

    init {
        viewModelScope.launch {
            val gameInfos = getGameInfoAllUseCase.execute()
            val date = gameInfos.maxOfOrNull { it.fixture.date }?.plusDays(1)
                ?: Constants.START

            val ranking =
                (getRankingUseCase.execute(League.L1) + getRankingUseCase.execute(League.L2))
                    .sortedBy { it.rank }

            val schedules = getGameSchedulesByDateUseCase.execute(date)
            // TODO: 試合が無い日の処理
            schedule = schedules.find {
                it.homeTeam.id == Constants.TEAM_ID || it.awayTeam.id == Constants.TEAM_ID
            } ?: throw IllegalStateException("No game scheduled for my team on $date")

            val homeFielderAppointment =
                getFielderAppointmentByTeamUseCase.execute(schedule.homeTeam)
            val awayFielderAppointment =
                getFielderAppointmentByTeamUseCase.execute(schedule.awayTeam)

            val homePlayers = getPlayerInfosByTeamUseCase.execute(schedule.homeTeam.id)
            val awayPlayers = getPlayerInfosByTeamUseCase.execute(schedule.awayTeam.id)

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
                    date = date,
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

    fun startGame() {
        viewModelScope.launch {
            executeGamesByDate.execute(uiState.value.date)
            val inningScores = getInningScoreUseCase.executeByFixtureId(schedule.fixture.id)
            val pitchingStats = getPitchingStatUseCase.executeByFixtureId(schedule.fixture.id)
            val ranking =
                (getRankingUseCase.execute(League.L1) + getRankingUseCase.execute(League.L2))
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

            val homeRuns = getHomeRunUseCase.execute(schedule.fixture.id)
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
                    homePlayers = getPlayerInfosByTeamUseCase.execute(schedule.homeTeam.id),
                    awayPlayers = getPlayerInfosByTeamUseCase.execute(schedule.awayTeam.id),
                    afterGameInfo = AfterGameInfo(
                        homeScores = inningScores.filter { it.teamId == schedule.homeTeam.id },
                        awayScores = inningScores.filter { it.teamId == schedule.awayTeam.id },
                        homePitcherResults = homePitcherResults,
                        awayPitcherResults = awayPitcherResults,
                        homeFielderResults = homeFielderResults,
                        awayFielderResults = awayFielderResults,
                        rankings = ranking
                    )
                )
            }
        }
    }
}