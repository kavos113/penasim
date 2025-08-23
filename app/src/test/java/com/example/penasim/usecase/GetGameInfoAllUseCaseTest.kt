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

class GetGameInfoAllUseCaseTest {

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
    fun execute_returnsGameInfos_forAllFixtures() = runTest {
        val league = League.L1
        val teams = listOf(
            Team(1, "A", league), Team(2, "B", league), Team(3, "C", league), Team(4, "D", league)
        )
        val fixtures = listOf(
            GameFixture(10, LocalDate.of(2025, 7, 1), 1, 1, 2),
            GameFixture(11, LocalDate.of(2025, 7, 2), 2, 3, 4)
        )
        val results = listOf(
            GameResult(10, 3, 1),
            GameResult(11, 0, 0)
        )
        val useCase = GetGameInfoAllUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(teams)
        )

        val infos = useCase.execute()
        assertEquals(2, infos.size)
        assertEquals(teams[0], infos[0].homeTeam)
        assertEquals(teams[1], infos[0].awayTeam)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun execute_throws_whenTeamOrFixtureMissing() = runTest {
        val league = League.L1
        val teams = listOf(Team(1, "A", league)) // team 2 missing
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))

        // missing team
        assertFailsWith<IllegalArgumentException> {
            GetGameInfoAllUseCase(
                FakeGameFixtureRepository(fixtures),
                FakeGameResultRepository(results),
                FakeTeamRepository(teams)
            ).execute()
        }

        // missing fixture
        assertFailsWith<IllegalArgumentException> {
            GetGameInfoAllUseCase(
                FakeGameFixtureRepository(emptyList()),
                FakeGameResultRepository(results),
                FakeTeamRepository(listOf(Team(1, "A", league), Team(2, "B", league)))
            ).execute()
        }
    }
}
