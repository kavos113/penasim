package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetRankingUseCaseTest {

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
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? = null
    }

    @Test
    fun execute_calculatesRankings_winLossDraw_andGameBack() = runTest {
        val league = League.L1
        val t0 = Team(0, "T0", league)
        val t1 = Team(1, "T1", league)
        val t2 = Team(2, "T2", league)
        val t3 = Team(3, "T3", league)
        val t4 = Team(4, "T4", league)
        val t5 = Team(5, "T5", league)
        val teams = listOf(t0, t1, t2, t3, t4, t5)

        val d1 = LocalDate.of(2025, 4, 1)
        val d2 = LocalDate.of(2025, 4, 2)

        val fixtures = listOf(
            GameFixture(id = 0, date = d1, numberOfGames = 0, homeTeamId = t0.id, awayTeamId = t5.id),
            GameFixture(id = 1, date = d1, numberOfGames = 1, homeTeamId = t1.id, awayTeamId = t3.id),
            GameFixture(id = 2, date = d1, numberOfGames = 2, homeTeamId = t2.id, awayTeamId = t4.id),
            GameFixture(id = 3, date = d2, numberOfGames = 3, homeTeamId = t0.id, awayTeamId = t5.id),
            GameFixture(id = 4, date = d2, numberOfGames = 4, homeTeamId = t1.id, awayTeamId = t2.id),
            GameFixture(id = 5, date = d2, numberOfGames = 5, homeTeamId = t3.id, awayTeamId = t4.id),
        )

        val results = listOf(
            GameResult(fixtureId = 0, homeScore = 5, awayScore = 3),
            GameResult(fixtureId = 1, homeScore = 4, awayScore = 4),
            GameResult(fixtureId = 2, homeScore = 6, awayScore = 2),
            GameResult(fixtureId = 3, homeScore = 7, awayScore = 2),
            GameResult(fixtureId = 4, homeScore = 3, awayScore = 1),
            GameResult(fixtureId = 5, homeScore = 2, awayScore = 2),
        )

        val teamRepo = FakeTeamRepository(teams)
        val fixtureRepo = FakeGameFixtureRepository(fixtures)
        val resultRepo = FakeGameResultRepository(results)
        val useCase = GetRankingUseCase(teamRepo, fixtureRepo, resultRepo)

        val standings = useCase.execute(league)

        assertEquals(6, standings.size)

        val s0 = standings.first { it.team.id == 0 }
        val s1 = standings.first { it.team.id == 1 }
        val s2 = standings.first { it.team.id == 2 }
        val s3 = standings.first { it.team.id == 3 }
        val s4 = standings.first { it.team.id == 4 }
        val s5 = standings.first { it.team.id == 5 }

        assertEquals(2, s0.wins); assertEquals(0, s0.losses); assertEquals(0, s0.draws)
        assertEquals(1, s1.wins); assertEquals(0, s1.losses); assertEquals(1, s1.draws)
        assertEquals(1, s2.wins); assertEquals(1, s2.losses); assertEquals(0, s2.draws)
        assertEquals(0, s3.wins); assertEquals(0, s3.losses); assertEquals(2, s3.draws)
        assertEquals(0, s4.wins); assertEquals(1, s4.losses); assertEquals(1, s4.draws)
        assertEquals(0, s5.wins); assertEquals(2, s5.losses); assertEquals(0, s5.draws)

        // Note: sorting uses wins desc, then losses desc, then draws desc
        assertEquals(1, s0.rank)
        assertEquals(2, s2.rank)
        assertEquals(3, s1.rank)
        assertEquals(4, s5.rank)
        assertEquals(5, s4.rank)
        assertEquals(6, s3.rank)

        fun assertDoubleEquals(expected: Double, actual: Double) = assertEquals(expected, actual, 0.001)
        assertDoubleEquals(0.0, s0.gameBack)
        assertDoubleEquals(0.5, s1.gameBack)
        assertDoubleEquals(1.0, s2.gameBack)
        assertDoubleEquals(1.0, s3.gameBack)
        assertDoubleEquals(1.5, s4.gameBack)
        assertDoubleEquals(2.0, s5.gameBack)
    }
}