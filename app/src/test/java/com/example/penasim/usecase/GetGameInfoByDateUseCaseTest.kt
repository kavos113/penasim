package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class GetGameInfoByDateUseCaseTest {

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
    fun execute_returnsGameInfos_forDate() = runTest {
        val league = League.L1
        val teams = listOf(Team(1, "A", league), Team(2, "B", league))
        val date = LocalDate.of(2025, 7, 1)
        val fixtures = listOf(
            GameFixture(10, date, 1, 1, 2),
            GameFixture(11, date.plusDays(1), 2, 1, 2)
        )
        val results = listOf(
            GameResult(10, 2, 0),
            GameResult(11, 1, 1)
        )
        val useCase = GetGameInfoByDateUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(teams)
        )

        val infos = useCase.execute(date)
        assertEquals(1, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(results[0], infos[0].result)
    }

    @Test
    fun execute_throws_whenTeamOrFixtureMissing() = runTest {
        val date = LocalDate.now()
        val fixtures = listOf(GameFixture(10, date, 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))

        // missing team
        assertThrows(IllegalArgumentException::class.java) {
            runTest {
                GetGameInfoByDateUseCase(
                    FakeGameFixtureRepository(fixtures),
                    FakeGameResultRepository(results),
                    FakeTeamRepository(emptyList())
                ).execute(date)
            }
        }

        // missing fixture
        assertThrows(IllegalArgumentException::class.java) {
            runTest {
                GetGameInfoByDateUseCase(
                    FakeGameFixtureRepository(emptyList()),
                    FakeGameResultRepository(results),
                    FakeTeamRepository(listOf(Team(1, "A", League.L1), Team(2, "B", League.L1)))
                ).execute(date)
            }
        }
    }
}
