package com.example.penasim.features.game.ui.after

import com.example.penasim.const.Constants
import com.example.penasim.core.session.InMemorySelectedTeamStore
import com.example.penasim.features.game.application.ExecuteGamesByDate
import com.example.penasim.features.game.domain.GameInfo
import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.game.domain.HomeRun
import com.example.penasim.features.game.domain.InningScore
import com.example.penasim.features.game.domain.PitchingStat
import com.example.penasim.features.game.ui.after.AfterGameViewModel
import com.example.penasim.features.game.usecase.GameInfoUseCase
import com.example.penasim.features.game.usecase.HomeRunUseCase
import com.example.penasim.features.game.usecase.InningScoreUseCase
import com.example.penasim.features.game.usecase.PitchingStatUseCase
import com.example.penasim.features.player.domain.Player
import com.example.penasim.features.player.domain.PlayerInfo
import com.example.penasim.features.player.domain.PlayerPosition
import com.example.penasim.features.player.domain.Position
import com.example.penasim.features.player.domain.TotalBattingStats
import com.example.penasim.features.player.domain.TotalPitchingStats
import com.example.penasim.features.player.usecase.PlayerInfoUseCase
import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import com.example.penasim.features.standing.domain.TeamStanding
import com.example.penasim.features.standing.usecase.RankingUseCase
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class AfterGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val selectedTeamStore = InMemorySelectedTeamStore()
    private val gameScheduleUseCase: GameScheduleUseCase = mock()
    private val executeGamesByDate: ExecuteGamesByDate = mock()
    private val inningScoreUseCase: InningScoreUseCase = mock()
    private val pitchingStatUseCase: PitchingStatUseCase = mock()
    private val homeRunUseCase: HomeRunUseCase = mock()
    private val rankingUseCase: RankingUseCase = mock()
    private val playerInfoUseCase: PlayerInfoUseCase = mock()
    private val gameInfoUseCase: GameInfoUseCase = mock()

    private val homeTeam = Team(0, "Home", League.L1)
    private val awayTeam = Team(1, "Away", League.L1)
    private val date = Constants.START
    private val fixture = GameFixture(1, date, 1, homeTeam.id, awayTeam.id)
    private val schedule = GameSchedule(fixture, homeTeam, awayTeam)

    private fun playerInfo(id: Int, team: Team, position: Position = Position.PITCHER): PlayerInfo {
        val player = Player(id, "P$id", "", team.id, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
        return PlayerInfo(
            player = player,
            positions = listOf(PlayerPosition(id, position, 50)),
            team = team,
            battingStat = TotalBattingStats(id),
            pitchingStat = TotalPitchingStats(id)
        )
    }

    private fun buildViewModel(): AfterGameViewModel {
        return AfterGameViewModel(
            selectedTeamStore,
            gameScheduleUseCase,
            executeGamesByDate,
            inningScoreUseCase,
            pitchingStatUseCase,
            homeRunUseCase,
            rankingUseCase,
            playerInfoUseCase,
            gameInfoUseCase
        )
    }

    @Test
    fun setDate_updatesState() = runTest {
        val vm = buildViewModel()
        val newDate = LocalDate.of(2025, 5, 1)

        vm.setDate(newDate)

        assertEquals(newDate, vm.uiState.value.date)
    }

    @Test
    fun runGame_togglesRunningFlag_andExecutesGames() = runTest {
        whenever(executeGamesByDate.execute(date)).thenReturn(emptyList())
        val vm = buildViewModel()

        vm.runGame()
        advanceUntilIdle()

        verify(executeGamesByDate).execute(date)
        assertFalse(vm.uiState.value.isRunning)
    }

    @Test
    fun skipGame_loadsRankingsAndGames() = runTest {
        whenever(rankingUseCase.getAll()).thenReturn(listOf(TeamStanding(homeTeam, rank = 1), TeamStanding(awayTeam, rank = 2)))
        whenever(gameInfoUseCase.getByDate(date)).thenReturn(listOf(GameInfo(fixture, homeTeam, awayTeam, GameResult(1, 3, 2))))
        val vm = buildViewModel()

        vm.skipGame()
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.rankings.size)
        assertEquals(1, vm.uiState.value.games.size)
    }

    @Test
    fun initData_populatesScoresAndResults_forCurrentTeamSchedule() = runTest {
        whenever(gameScheduleUseCase.getByDate(date)).thenReturn(listOf(schedule))
        whenever(playerInfoUseCase.getByTeamId(homeTeam.id)).thenReturn(listOf(playerInfo(10, homeTeam)))
        whenever(playerInfoUseCase.getByTeamId(awayTeam.id)).thenReturn(listOf(playerInfo(20, awayTeam)))
        whenever(inningScoreUseCase.getByFixtureId(fixture.id)).thenReturn(
            listOf(
                InningScore(fixture.id, homeTeam.id, 1, 2),
                InningScore(fixture.id, awayTeam.id, 1, 1)
            )
        )
        whenever(pitchingStatUseCase.getByFixtureId(fixture.id)).thenReturn(
            listOf(
                PitchingStat(fixture.id, 10, inningPitched = 9, strikeOut = 8, win = true),
                PitchingStat(fixture.id, 20, inningPitched = 8, strikeOut = 5, lose = true)
            )
        )
        whenever(homeRunUseCase.getByFixtureId(fixture.id)).thenReturn(
            listOf(
                HomeRun(fixture.id, 10, inning = 1, count = 1),
                HomeRun(fixture.id, 20, inning = 2, count = 1)
            )
        )
        whenever(rankingUseCase.getAll()).thenReturn(listOf(TeamStanding(homeTeam, rank = 1), TeamStanding(awayTeam, rank = 2)))
        whenever(gameInfoUseCase.getByDate(date)).thenReturn(listOf(GameInfo(fixture, homeTeam, awayTeam, GameResult(1, 2, 1))))
        val vm = buildViewModel()

        vm.initData()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("Home", state.homeTeamName)
        assertEquals("Away", state.awayTeamName)
        assertEquals(1, state.homeScores.size)
        assertEquals(1, state.awayScores.size)
        assertEquals(1, state.homePitcherResults.size)
        assertEquals(1, state.awayPitcherResults.size)
        assertEquals(1, state.homeFielderResults.size)
        assertEquals(1, state.awayFielderResults.size)
        assertEquals(1, state.games.size)
    }
}


