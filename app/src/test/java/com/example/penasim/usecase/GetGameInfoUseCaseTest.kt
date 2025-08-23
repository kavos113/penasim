package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test
import java.time.LocalDate

class GetGameInfoUseCaseTest {

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
    fun execute_returnsComposedGameInfo_whenAllDataExists() = runTest {
        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)
        val result = GameResult(10, 5, 2)

        val useCase = GetGameInfoUseCase(
            FakeGameFixtureRepository(listOf(fixture)),
            FakeGameResultRepository(listOf(result)),
            FakeTeamRepository(listOf(home, away))
        )

        val info = useCase.execute(10)

        assertEquals(fixture, info.fixture)
        assertEquals(home, info.homeTeam)
        assertEquals(away, info.awayTeam)
        assertEquals(result, info.result)
    }

    @Test
    fun execute_throws_whenTeamsMissing_orResultMissing() = runTest {
        val league = League.L1
        val home = Team(1, "Home", league)
        val away = Team(2, "Away", league)
        val fixture = GameFixture(10, LocalDate.of(2025, 8, 1), 1, home.id, away.id)
        val result = GameResult(10, 5, 2)

        // Missing home team
        assertFailsWith<IllegalArgumentException> {
            GetGameInfoUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeGameResultRepository(listOf(result)),
                FakeTeamRepository(listOf(away))
            ).execute(10)
        }

        // Missing away team
        assertFailsWith<IllegalArgumentException> {
            GetGameInfoUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeGameResultRepository(listOf(result)),
                FakeTeamRepository(listOf(home))
            ).execute(10)
        }

        // Missing result
        assertFailsWith<IllegalArgumentException> {
            GetGameInfoUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeGameResultRepository(emptyList()),
                FakeTeamRepository(listOf(home, away))
            ).execute(10)
        }
    }
}
