package com.example.penasim.ui.home

import com.example.penasim.const.Constants
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GameScheduleUseCase
import com.example.penasim.usecase.RankingUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeTeamRepository(private val teams: List<Team>) : TeamRepository {
        override suspend fun getTeam(id: Int): Team? = teams.find { it.id == id }
        override suspend fun getTeamsByLeague(league: League): List<Team> = teams.filter { it.league == league }
        override suspend fun getAllTeams(): List<Team> = teams
    }

    private class FakeGameFixtureRepository(private val fixtures: List<GameFixture>) : GameFixtureRepository {
        override suspend fun getGameFixture(id: Int): GameFixture? = fixtures.find { it.id == id }
        override suspend fun getGameFixturesByDate(date: LocalDate): List<GameFixture> = fixtures.filter { it.date == date }
        override suspend fun getGameFixturesByTeam(team: Team): List<GameFixture> = fixtures.filter { it.homeTeamId == team.id || it.awayTeamId == team.id }
        override suspend fun getAllGameFixtures(): List<GameFixture> = fixtures
    }

    private class FakeGameResultRepository(private val results: List<GameResult>) : GameResultRepository {
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = results.find { it.fixtureId == fixtureId }
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = results.filter { it.fixtureId in fixtureIds }
        override suspend fun getAllGames(): List<GameResult> = results
        override suspend fun deleteAllGames() {}
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? = null
    }

    @Test
    fun update_setsRank_and_isGameDay() = runTest {
        val today = Constants.START
        val l1 = League.L1
        val teamA = Team(0, "A", l1)
        val teamB = Team(1, "B", l1)
        val teams = listOf(teamA, teamB)
        val fixtures = listOf(
            GameFixture(10, today, 1, teamA.id, teamB.id)
        )
        val results = listOf(
            GameResult(10, 3, 1) // teamA win
        )

        val teamRepo = FakeTeamRepository(teams)
        val fixtureRepo = FakeGameFixtureRepository(fixtures)
        val resultRepo = FakeGameResultRepository(results)

        val rankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val scheduleUseCase = GameScheduleUseCase(fixtureRepo, teamRepo)

        val vm = HomeViewModel(rankingUseCase, scheduleUseCase)
        vm.setTeamId(teamA.id)
        vm.setCurrentDay(today)
        vm.update()

        val state = vm.uiState.value
        assertEquals(1, state.rank) // teamA has 1 win
        assertTrue(state.isGameDay)
    }
}

