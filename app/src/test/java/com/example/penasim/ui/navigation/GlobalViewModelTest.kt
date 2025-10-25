package com.example.penasim.ui.navigation

import com.example.penasim.const.Constants
import com.example.penasim.domain.GameFixture
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GameInfoUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class GlobalViewModelTest {
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
    fun init_setsCurrentDay_fromMaxFixtureDatePlusOne() = runTest {
        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val d1 = LocalDate.of(2025, 7, 1)
        val d2 = LocalDate.of(2025, 7, 5)
        val fixtures = listOf(
            GameFixture(10, d1, 1, home.id, away.id),
            GameFixture(11, d2, 1, home.id, away.id)
        )
        val results = listOf(
            GameResult(10, 1, 0),
            GameResult(11, 2, 3)
        )

        val vm = GlobalViewModel(
            GameInfoUseCase(
                FakeGameFixtureRepository(fixtures),
                FakeGameResultRepository(results),
                FakeTeamRepository(listOf(home, away))
            )
        )

        val expected = d2.plusDays(1)
        assertEquals(expected, vm.state.value.currentDay)
    }

    @Test
    fun nextDay_incrementsCurrentDay() = runTest {
        val vm = GlobalViewModel(
            GameInfoUseCase(
                FakeGameFixtureRepository(emptyList()),
                FakeGameResultRepository(emptyList()),
                FakeTeamRepository(emptyList())
            )
        )

        val initial = vm.state.value.currentDay
        // when no gameInfos, it should be Constants.START
        assertEquals(Constants.START, initial)

        vm.nextDay()

        assertEquals(initial.plusDays(1), vm.state.value.currentDay)
    }
}

