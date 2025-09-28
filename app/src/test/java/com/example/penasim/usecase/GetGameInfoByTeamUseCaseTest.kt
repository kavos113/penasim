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

class GetGameInfoByTeamUseCaseTest {

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
    fun execute_returnsGameInfos_forTeam() = runTest {
        val league = League.L1
        val team = Team(1, "A", league)
        val opponent = Team(2, "B", league)
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now(), 2, 2, 1)
        )
        val results = listOf(GameResult(10, 1, 0), GameResult(11, 0, 2))

        val useCase = GetGameInfoByTeamUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(listOf(team, opponent))
        )

        val infos = useCase.execute(team)
        assertEquals(2, infos.size)
        assertEquals(10, infos[0].fixture.id)
        assertEquals(11, infos[1].fixture.id)
    }

    @Test
    fun execute_throws_whenTeamEntityMissing() = runTest {
        val team = Team(1, "A", League.L1)
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val results = listOf(GameResult(10, 1, 1))
        val useCase = GetGameInfoByTeamUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeGameResultRepository(results),
            FakeTeamRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { useCase.execute(team) }
    }
}
