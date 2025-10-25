package com.example.penasim.ui.calender

import com.example.penasim.const.Constants
import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.ExecuteRandomGamesByDateUseCase
import com.example.penasim.usecase.GetGameInfoAllUseCase
import com.example.penasim.usecase.GetGameSchedulesAllUseCase
import com.example.penasim.usecase.RankingUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CalendarViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

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

    private class FakeGameResultRepository(
        private val initial: List<GameResult> = emptyList(),
        private val createBehavior: (fixtureId: Int, home: Int, away: Int) -> GameResult? = { _, _, _ -> null }
    ) : GameResultRepository {
        private val results = initial.toMutableList()
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = results.find { it.fixtureId == fixtureId }
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = results.filter { it.fixtureId in fixtureIds }
        override suspend fun getAllGames(): List<GameResult> = results
        override suspend fun deleteAllGames() { results.clear() }
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? =
            createBehavior(fixtureId, homeScore, awayScore)?.also { results.add(it) }
    }

    @Test
    fun init_buildsGames_andRankings_andCurrentDay() = runTest {
        val start = Constants.START
        val day2 = start.plusDays(1)

        val l = League.L1
        val tA = Team(10, "A", l)
        val tB = Team(11, "B", l)
        val tC = Team(12, "C", l)
        val tD = Team(13, "D", l)

        val fixtures = listOf(
            GameFixture(1, start, 1, tA.id, tB.id),
            GameFixture(2, day2, 1, tC.id, tD.id)
        )

        val playedResults = listOf(
            GameResult(1, 2, 1) // only first day played
        )

        val teamRepo = FakeTeamRepository(listOf(tA, tB, tC, tD))
        val fixtureRepo = FakeGameFixtureRepository(fixtures)
        val resultRepo = FakeGameResultRepository(playedResults)

        val vm = CalendarViewModel(
            getGameSchedulesAllUseCase = GetGameSchedulesAllUseCase(fixtureRepo, teamRepo),
            getGameInfoAllUseCase = GetGameInfoAllUseCase(fixtureRepo, resultRepo, teamRepo),
            getRankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo),
            executeGamesByDateUseCase = ExecuteRandomGamesByDateUseCase(resultRepo, fixtureRepo, teamRepo)
        )

        val state = vm.uiState.value
        // There should be an entry for start and day2
        assertEquals(1, state.games[start]?.size ?: 0)
        assertEquals(1, state.games[day2]?.size ?: 0)
        // Current day is max gameInfos date + 1 = start + 1
        assertEquals(day2, state.currentDay)
        // Rankings should be present (size depends on mapping; just ensure not empty)
        assert(state.rankings.isNotEmpty())
    }

    @Test
    fun nextGame_executesGamesForCurrentDay_andUpdatesState() = runTest {
        val start = Constants.START
        val day2 = start.plusDays(1)

        val l = League.L1
        val tA = Team(10, "A", l)
        val tB = Team(11, "B", l)
        val tC = Team(12, "C", l)
        val tD = Team(13, "D", l)

        val fixtures = listOf(
            GameFixture(1, start, 1, tA.id, tB.id),
            GameFixture(2, day2, 1, tC.id, tD.id)
        )

        val playedResults = listOf(
            GameResult(1, 2, 1)
        )

        val teamRepo = FakeTeamRepository(listOf(tA, tB, tC, tD))
        val fixtureRepo = FakeGameFixtureRepository(fixtures)
        // Create behavior ensures deterministic result for day2 fixture id 2
        val resultRepo = FakeGameResultRepository(playedResults) { f, h, a -> if (f == 2) GameResult(f, 5, 3) else null }

        val vm = CalendarViewModel(
            getGameSchedulesAllUseCase = GetGameSchedulesAllUseCase(fixtureRepo, teamRepo),
            getGameInfoAllUseCase = GetGameInfoAllUseCase(fixtureRepo, resultRepo, teamRepo),
            getRankingUseCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo),
            executeGamesByDateUseCase = ExecuteRandomGamesByDateUseCase(resultRepo, fixtureRepo, teamRepo)
        )

        // Initial current day should be day2 (start + 1), so nextGame will process day2
        vm.nextGame()
        val state = vm.uiState.value
        assertEquals(day2, state.currentDay)
        assertEquals(1, state.games[day2]?.size ?: 0)
    }
}