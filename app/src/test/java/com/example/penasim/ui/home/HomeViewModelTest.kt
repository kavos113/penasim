package com.example.penasim.ui.home

import com.example.penasim.const.Constants
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.TeamStanding
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.RankingUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun update_setsRank_and_isGameDay() = runTest {
        val today = Constants.START
        val teamA = Team(0, "A", League.L1)
        val teamB = Team(1, "B", League.L1)
        val fixture = GameFixture(10, today, 1, teamA.id, teamB.id)

        val rankingUseCase: RankingUseCase = mock()
        val scheduleUseCase: GameScheduleUseCase = mock()

        whenever(rankingUseCase.getByLeague(League.L1)).thenReturn(
            listOf(
                TeamStanding(team = teamA, rank = 1, wins = 1, losses = 0, draws = 0),
                TeamStanding(team = teamB, rank = 2, wins = 0, losses = 1, draws = 0)
            )
        )
        whenever(rankingUseCase.getByLeague(League.L2)).thenReturn(emptyList())
        whenever(scheduleUseCase.getByDate(today)).thenReturn(
            listOf(
                GameSchedule(fixture = fixture, homeTeam = teamA, awayTeam = teamB)
            )
        )

        val vm = HomeViewModel(rankingUseCase, scheduleUseCase)
        vm.setTeamId(teamA.id)
        vm.setCurrentDay(today)
        vm.update()

        val state = vm.uiState.value
        assertEquals(1, state.rank) // teamA has 1 win
        assertTrue(state.isGameDay)
    }
}

