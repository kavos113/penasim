package com.example.penasim.ui.game

import androidx.compose.ui.graphics.Color
import com.example.penasim.const.Constants
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameInfo
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.League
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.game.BaseState
import com.example.penasim.game.BatterState
import com.example.penasim.game.ExecuteGameByOne
import com.example.penasim.game.LastResult
import com.example.penasim.game.PitcherState
import com.example.penasim.game.Result
import com.example.penasim.game.ScoreData
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.GetDisplayFielder
import com.example.penasim.usecase.GameScheduleUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class InGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val homeTeam = Team(Constants.TEAM_ID, "Home", League.L1)
    private val awayTeam = Team(1, "Away", League.L1)

    private val homePlayers = listOf(
        DisplayFielder(100, "HP1", Position.CATCHER, 1, Color.Red),
        DisplayFielder(101, "HP2", Position.FIRST_BASEMAN, 2, Color.Red),
        DisplayFielder(109, "HP10", Position.PITCHER, 9, Color.Red),
    )
    private val awayPlayers = listOf(
        DisplayFielder(200, "AP1", Position.CATCHER, 1, Color.Blue),
        DisplayFielder(201, "AP2", Position.FIRST_BASEMAN, 2, Color.Blue),
        DisplayFielder(209, "AP10", Position.PITCHER, 9, Color.Blue),
    )

    private val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)
    private val schedule = GameSchedule(fixture, homeTeam, awayTeam)

    private val ongoingScoreData = ScoreData(
        scores = emptyList(),
        outCount = 1,
        baseState = BaseState(),
        lastResult = LastResult(Result.OUT, false, false),
        isHomeBatting = false,
        homeBatterState = BatterState(playerId = 100, battingOrder = 1),
        awayBatterState = BatterState(playerId = 200, battingOrder = 1),
        homePitcherState = PitcherState(playerId = 109, stamina = 100),
        awayPitcherState = PitcherState(playerId = 209, stamina = 100),
    )

    private val finalScoreData = ScoreData(
        scores = listOf(
            InningScore(1, homeTeam.id, 1, 2),
            InningScore(1, awayTeam.id, 1, 1),
        ),
        outCount = 3,
        baseState = BaseState(),
        lastResult = LastResult(Result.OUT, false, false),
        isHomeBatting = true,
        homeBatterState = BatterState(playerId = 100, battingOrder = 5),
        awayBatterState = BatterState(playerId = 200, battingOrder = 3),
        homePitcherState = PitcherState(playerId = 109, stamina = 50),
        awayPitcherState = PitcherState(playerId = 209, stamina = 50),
    )

    private val executeGameByOne: ExecuteGameByOne = mock()
    private val gameScheduleUseCase: GameScheduleUseCase = mock()
    private val getDisplayFielder: GetDisplayFielder = mock()

    private fun buildViewModel(): InGameViewModel {
        return InGameViewModel(executeGameByOne, gameScheduleUseCase, getDisplayFielder)
    }

    private suspend fun stubInitialization(
        date: LocalDate = Constants.START,
        schedules: List<GameSchedule> = listOf(schedule),
        homePlayersList: List<DisplayFielder> = homePlayers,
        awayPlayersList: List<DisplayFielder> = awayPlayers,
    ) {
        whenever(gameScheduleUseCase.getByDate(date)).thenReturn(schedules)
        whenever(getDisplayFielder.getMainMember(eq(schedules.first { it.homeTeam.id == Constants.TEAM_ID || it.awayTeam.id == Constants.TEAM_ID }.homeTeam), eq(OrderType.NORMAL))).thenReturn(homePlayersList)
        whenever(getDisplayFielder.getMainMember(eq(schedules.first { it.homeTeam.id == Constants.TEAM_ID || it.awayTeam.id == Constants.TEAM_ID }.awayTeam), eq(OrderType.NORMAL))).thenReturn(awayPlayersList)
    }

    @Test
    fun defaultState_hasConstantsStartDate() {
        val vm = buildViewModel()
        assertEquals(Constants.START, vm.uiState.value.date)
    }

    @Test
    fun setDate_updatesDateAndInitializesGame() = runTest {
        stubInitialization()
        val vm = buildViewModel()

        vm.setDate(Constants.START)

        val state = vm.uiState.value
        assertEquals(Constants.START, state.date)
        assertEquals("Home", state.homeTeam.name)
        assertEquals("Away", state.awayTeam.name)
        assertTrue(state.homeTeam.players.isNotEmpty())
        assertTrue(state.awayTeam.players.isNotEmpty())
    }

    @Test
    fun setDate_secondCallDoesNotReinitialize() = runTest {
        stubInitialization()
        val vm = buildViewModel()

        vm.setDate(Constants.START)
        val stateAfterFirst = vm.uiState.value

        val newDate = LocalDate.of(2025, 5, 1)
        vm.setDate(newDate)

        val stateAfterSecond = vm.uiState.value
        assertEquals(newDate, stateAfterSecond.date)
        assertEquals(stateAfterFirst.homeTeam.name, stateAfterSecond.homeTeam.name)
        assertEquals(stateAfterFirst.awayTeam.name, stateAfterSecond.awayTeam.name)
    }

    @Test
    fun next_advancesGameAndReturnsState() = runTest {
        stubInitialization()
        whenever(executeGameByOne.next()).thenReturn(Pair(true, ongoingScoreData))

        val vm = buildViewModel()
        vm.setDate(Constants.START)

        val finished = vm.next()
        assertFalse(finished)
    }

    @Test
    fun skip_completesEntireGame() = runTest {
        stubInitialization()
        whenever(executeGameByOne.next())
            .thenReturn(Pair(true, ongoingScoreData))
            .thenReturn(Pair(true, ongoingScoreData))
            .thenReturn(Pair(false, finalScoreData))
        whenever(executeGameByOne.postFinishGame()).thenReturn(emptyList<GameInfo>())

        val vm = buildViewModel()
        vm.setDate(Constants.START)
        vm.skip()

        val state = vm.uiState.value
        assertTrue(state.homeTeam.inningScores.isNotEmpty())
        assertTrue(state.awayTeam.inningScores.isNotEmpty())
    }

    @Test
    fun next_repeatedUntilFinished_completesGame() = runTest {
        stubInitialization()
        whenever(executeGameByOne.next())
            .thenReturn(Pair(true, ongoingScoreData))
            .thenReturn(Pair(true, ongoingScoreData))
            .thenReturn(Pair(true, ongoingScoreData))
            .thenReturn(Pair(false, finalScoreData))
        whenever(executeGameByOne.postFinishGame()).thenReturn(emptyList<GameInfo>())

        val vm = buildViewModel()
        vm.setDate(Constants.START)

        var steps = 0
        while (!vm.next()) {
            steps++
            if (steps > 10000) break
        }

        assertTrue("Game should finish within 10000 steps", steps <= 10000)
        val state = vm.uiState.value
        assertTrue(state.homeTeam.inningScores.isNotEmpty())
        assertTrue(state.awayTeam.inningScores.isNotEmpty())
    }

    @Test
    fun setDate_awayTeamMatchesTeamId_initializesCorrectly() = runTest {
        val awayMatchTeam = Team(Constants.TEAM_ID, "Away", League.L1)
        val homeMatchTeam = Team(1, "Home", League.L1)
        val matchFixture = GameFixture(1, Constants.START, 0, homeMatchTeam.id, awayMatchTeam.id)
        val matchSchedule = GameSchedule(matchFixture, homeMatchTeam, awayMatchTeam)

        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(listOf(matchSchedule))
        whenever(getDisplayFielder.getMainMember(eq(homeMatchTeam), eq(OrderType.NORMAL))).thenReturn(homePlayers)
        whenever(getDisplayFielder.getMainMember(eq(awayMatchTeam), eq(OrderType.NORMAL))).thenReturn(awayPlayers)

        val vm = buildViewModel()
        vm.setDate(Constants.START)

        val state = vm.uiState.value
        assertEquals("Home", state.homeTeam.name)
        assertEquals("Away", state.awayTeam.name)
    }

    @Test
    fun outCount_initiallyZero() {
        val vm = buildViewModel()
        assertEquals(0, vm.uiState.value.outCount)
    }

    @Test
    fun bases_initiallyNull() {
        val vm = buildViewModel()
        assertNull(vm.uiState.value.firstBase)
        assertNull(vm.uiState.value.secondBase)
        assertNull(vm.uiState.value.thirdBase)
    }
}
