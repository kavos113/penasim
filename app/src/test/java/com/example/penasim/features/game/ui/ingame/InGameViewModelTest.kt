package com.example.penasim.features.game.ui.ingame

import androidx.compose.ui.graphics.Color
import com.example.penasim.const.Constants
import com.example.penasim.core.session.InMemorySelectedTeamStore
import com.example.penasim.core.ui.model.DisplayFielder
import com.example.penasim.features.command.domain.OrderType
import com.example.penasim.features.command.usecase.DisplayFielderUseCase
import com.example.penasim.features.game.application.ExecuteGameByOne
import com.example.penasim.features.game.application.model.AtBatResultType
import com.example.penasim.features.game.application.model.InGameAtBatResult
import com.example.penasim.features.game.application.model.InGameSnapshot
import com.example.penasim.features.game.engine.BatterState
import com.example.penasim.features.game.engine.PitcherState
import com.example.penasim.features.game.ui.ingame.InGameInfoAssembler
import com.example.penasim.features.game.ui.ingame.InGameViewModel
import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import com.example.penasim.features.player.domain.Position
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class InGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val selectedTeamStore = InMemorySelectedTeamStore()
    private val executeGameByOne: ExecuteGameByOne = mock()
    private val gameScheduleUseCase: GameScheduleUseCase = mock()
    private val displayFielderUseCase: DisplayFielderUseCase = mock()
    private val assembler = InGameInfoAssembler()

    private val homeTeam = Team(0, "Home", League.L1)
    private val awayTeam = Team(1, "Away", League.L1)
    private val date = Constants.START
    private val schedule = GameSchedule(GameFixture(1, date, 1, homeTeam.id, awayTeam.id), homeTeam, awayTeam)
    private val homePlayers = listOf(
        DisplayFielder(10, "HomePitcher", Position.PITCHER, 9, Color.Red),
        DisplayFielder(11, "HomeLeadOff", Position.OUTFIELDER, 1, Color.Blue),
    )
    private val awayPlayers = listOf(
        DisplayFielder(20, "AwayPitcher", Position.PITCHER, 9, Color.Red),
        DisplayFielder(21, "AwayLeadOff", Position.OUTFIELDER, 1, Color.Blue),
    )

    private fun snapshot(
        isHomeBatting: Boolean = false,
        outCount: Int = 1
    ) = InGameSnapshot(
        scores = emptyList(),
        outCount = outCount,
        firstBasePlayerId = 11,
        secondBasePlayerId = null,
        thirdBasePlayerId = null,
        lastResult = InGameAtBatResult(AtBatResultType.SINGLE_HIT, isHit = true, isScored = false),
        isHomeBatting = isHomeBatting,
        homeBatterState = BatterState(11, 1),
        awayBatterState = BatterState(21, 1),
        homePitcherState = PitcherState(10, 50),
        awayPitcherState = PitcherState(20, 50),
    )

    private fun buildViewModel(): InGameViewModel {
        return InGameViewModel(
            selectedTeamStore,
            executeGameByOne,
            gameScheduleUseCase,
            displayFielderUseCase,
            assembler
        )
    }

    @Test
    fun setDate_initializesScheduleAndPlayers() = runTest {
        whenever(gameScheduleUseCase.getByDate(date)).thenReturn(listOf(schedule))
        whenever(displayFielderUseCase.getMainMember(eq(homeTeam), eq(OrderType.NORMAL))).thenReturn(homePlayers)
        whenever(displayFielderUseCase.getMainMember(eq(awayTeam), eq(OrderType.NORMAL))).thenReturn(awayPlayers)
        val vm = buildViewModel()

        vm.setDate(date)
        advanceUntilIdle()

        verify(executeGameByOne).start(homeTeam, date)
        assertEquals("Home", vm.uiState.value.homeTeam.name)
        assertEquals("Away", vm.uiState.value.awayTeam.name)
        assertEquals(10, vm.uiState.value.homeTeam.activePlayerId)
        assertEquals(21, vm.uiState.value.awayTeam.activePlayerId)
    }

    @Test
    fun next_appliesSnapshotToUiState() = runTest {
        whenever(gameScheduleUseCase.getByDate(date)).thenReturn(listOf(schedule))
        whenever(displayFielderUseCase.getMainMember(eq(homeTeam), eq(OrderType.NORMAL))).thenReturn(homePlayers)
        whenever(displayFielderUseCase.getMainMember(eq(awayTeam), eq(OrderType.NORMAL))).thenReturn(awayPlayers)
        whenever(executeGameByOne.next()).thenReturn(true to snapshot(isHomeBatting = false, outCount = 2))
        val vm = buildViewModel()
        vm.setDate(date)
        advanceUntilIdle()

        val finished = vm.next()

        assertTrue(!finished)
        assertEquals(2, vm.uiState.value.outCount)
        assertEquals(11, vm.uiState.value.firstBase?.id)
        assertEquals(21, vm.uiState.value.awayTeam.activePlayerId)
    }

    @Test
    fun skip_advancesUntilFinish_andPostsResult() = runTest {
        whenever(gameScheduleUseCase.getByDate(date)).thenReturn(listOf(schedule))
        whenever(displayFielderUseCase.getMainMember(eq(homeTeam), eq(OrderType.NORMAL))).thenReturn(homePlayers)
        whenever(displayFielderUseCase.getMainMember(eq(awayTeam), eq(OrderType.NORMAL))).thenReturn(awayPlayers)
        whenever(executeGameByOne.next()).thenReturn(
            true to snapshot(outCount = 1),
            false to snapshot(outCount = 3)
        )
        whenever(executeGameByOne.postFinishGame()).thenReturn(emptyList())
        val vm = buildViewModel()
        vm.setDate(date)
        advanceUntilIdle()

        vm.skip()
        advanceUntilIdle()

        verify(executeGameByOne, times(2)).next()
        verify(executeGameByOne).postFinishGame()
        assertEquals(3, vm.uiState.value.outCount)
    }
}


