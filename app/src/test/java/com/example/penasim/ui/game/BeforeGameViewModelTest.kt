package com.example.penasim.ui.game

import androidx.compose.ui.graphics.Color
import com.example.penasim.const.Constants
import com.example.penasim.core.session.InMemorySelectedTeamStore
import com.example.penasim.core.ui.model.DisplayFielder
import com.example.penasim.features.command.domain.OrderType
import com.example.penasim.features.command.usecase.DisplayFielderUseCase
import com.example.penasim.features.game.ui.before.BeforeGameViewModel
import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.schedule.usecase.GameScheduleUseCase
import com.example.penasim.features.player.domain.Position
import com.example.penasim.features.standing.domain.TeamStanding
import com.example.penasim.features.standing.usecase.RankingUseCase
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class BeforeGameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val rankingUseCase: RankingUseCase = mock()
    private val gameScheduleUseCase: GameScheduleUseCase = mock()
    private val displayFielderUseCase: DisplayFielderUseCase = mock()
    private val selectedTeamStore = InMemorySelectedTeamStore()

    private suspend fun buildEmptyViewModel(): BeforeGameViewModel {
        whenever(rankingUseCase.getAll()).thenReturn(emptyList())
        whenever(gameScheduleUseCase.getByDate(any())).thenReturn(emptyList())
        return BeforeGameViewModel(selectedTeamStore, rankingUseCase, gameScheduleUseCase, displayFielderUseCase)
    }

    private fun mkDisplayFielder(id: Int, name: String, position: Position, number: Int) =
        DisplayFielder(id, name, position, number, Color.Gray)

    @Test
    fun setDate_updatesDateInState() = runTest {
        val vm = buildEmptyViewModel()
        val newDate = LocalDate.of(2025, 5, 1)

        vm.setDate(newDate)

        assertEquals(newDate, vm.uiState.value.date)
    }

    @Test
    fun init_defaultDateIsConstantsStart() = runTest {
        val vm = buildEmptyViewModel()
        advanceUntilIdle()
        assertEquals(Constants.START, vm.uiState.value.date)
    }

    @Test
    fun init_withNoSchedulesForDate_keepsDefaultState() = runTest {
        val vm = buildEmptyViewModel()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.homeStartingPlayers.isEmpty())
        assertTrue(vm.uiState.value.awayStartingPlayers.isEmpty())
        assertEquals(0, vm.uiState.value.homeTeam.rank)
        assertEquals(0, vm.uiState.value.awayTeam.rank)
    }

    @Test
    fun init_withScheduleNotMatchingTeamId_keepsDefaultState() = runTest {
        val teamA = Team(10, "A", League.L1)
        val teamB = Team(11, "B", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, teamA.id, teamB.id)
        val schedule = GameSchedule(fixture, teamA, teamB)

        whenever(rankingUseCase.getAll()).thenReturn(emptyList())
        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(listOf(schedule))

        val vm = BeforeGameViewModel(selectedTeamStore, rankingUseCase, gameScheduleUseCase, displayFielderUseCase)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.homeStartingPlayers.isEmpty())
        assertTrue(vm.uiState.value.awayStartingPlayers.isEmpty())
    }

    @Test
    fun init_withMatchingSchedule_loadsRankingsAndStartingPlayers() = runTest {
        val homeTeam = Team(0, "Home", League.L1)
        val awayTeam = Team(1, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)
        val schedule = GameSchedule(fixture, homeTeam, awayTeam)

        val homeRanking = TeamStanding(homeTeam, rank = 1, wins = 0, losses = 0)
        val awayRanking = TeamStanding(awayTeam, rank = 2, wins = 0, losses = 0)
        val homeStarting = listOf(
            mkDisplayFielder(10, "HP1", Position.OUTFIELDER, 1),
            mkDisplayFielder(11, "HP2", Position.CATCHER, 2),
            mkDisplayFielder(12, "HPitcher", Position.PITCHER, 3),
        )
        val awayStarting = listOf(
            mkDisplayFielder(20, "AP1", Position.OUTFIELDER, 1),
            mkDisplayFielder(21, "AP2", Position.CATCHER, 2),
            mkDisplayFielder(22, "APitcher", Position.PITCHER, 3),
        )

        whenever(rankingUseCase.getAll()).thenReturn(listOf(homeRanking, awayRanking))
        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(listOf(schedule))
        whenever(displayFielderUseCase.getStartingMember(eq(homeTeam), eq(OrderType.NORMAL))).thenReturn(homeStarting)
        whenever(displayFielderUseCase.getStartingMember(eq(awayTeam), eq(OrderType.NORMAL))).thenReturn(awayStarting)

        val vm = BeforeGameViewModel(selectedTeamStore, rankingUseCase, gameScheduleUseCase, displayFielderUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(homeTeam.id, state.homeTeam.team.id)
        assertEquals(awayTeam.id, state.awayTeam.team.id)
        assertEquals(3, state.homeStartingPlayers.size)
        assertEquals(3, state.awayStartingPlayers.size)
    }

    @Test
    fun init_withMatchingSchedule_rankingsReflectWinsLosses() = runTest {
        val homeTeam = Team(0, "Home", League.L1)
        val awayTeam = Team(1, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)
        val schedule = GameSchedule(fixture, homeTeam, awayTeam)

        val homeRanking = TeamStanding(homeTeam, rank = 1, wins = 1, losses = 0)
        val awayRanking = TeamStanding(awayTeam, rank = 2, wins = 0, losses = 1)

        whenever(rankingUseCase.getAll()).thenReturn(listOf(homeRanking, awayRanking))
        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(listOf(schedule))
        whenever(displayFielderUseCase.getStartingMember(any(), any())).thenReturn(emptyList())

        val vm = BeforeGameViewModel(selectedTeamStore, rankingUseCase, gameScheduleUseCase, displayFielderUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(1, state.homeTeam.rank)
        assertEquals(1, state.homeTeam.wins)
        assertEquals(0, state.homeTeam.losses)
        assertEquals(2, state.awayTeam.rank)
        assertEquals(0, state.awayTeam.wins)
        assertEquals(1, state.awayTeam.losses)
    }

    @Test
    fun init_awayTeamMatchesTeamId_loadsCorrectly() = runTest {
        selectedTeamStore.setTeamId(2)
        val homeTeam = Team(1, "Home", League.L1)
        val awayTeam = Team(2, "Away", League.L1)
        val fixture = GameFixture(1, Constants.START, 0, homeTeam.id, awayTeam.id)
        val schedule = GameSchedule(fixture, homeTeam, awayTeam)

        val homeRanking = TeamStanding(homeTeam, rank = 1)
        val awayRanking = TeamStanding(awayTeam, rank = 2)

        whenever(rankingUseCase.getAll()).thenReturn(listOf(homeRanking, awayRanking))
        whenever(gameScheduleUseCase.getByDate(Constants.START)).thenReturn(listOf(schedule))
        whenever(displayFielderUseCase.getStartingMember(any(), any())).thenReturn(emptyList())

        val vm = BeforeGameViewModel(selectedTeamStore, rankingUseCase, gameScheduleUseCase, displayFielderUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(homeTeam.id, state.homeTeam.team.id)
        assertEquals(awayTeam.id, state.awayTeam.team.id)
    }

    @Test
    fun setDate_multipleTimes_retainsLastValue() = runTest {
        val vm = buildEmptyViewModel()

        vm.setDate(LocalDate.of(2025, 4, 1))
        vm.setDate(LocalDate.of(2025, 5, 15))
        vm.setDate(LocalDate.of(2025, 6, 30))

        assertEquals(LocalDate.of(2025, 6, 30), vm.uiState.value.date)
    }
}
