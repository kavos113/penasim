package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class GetGameSchedulesAllUseCaseTest {

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

    @Test
    fun execute_returnsSchedules_forAllFixtures() = runTest {
        val league = League.L1
        val teams = listOf(Team(1, "A", league), Team(2, "B", league))
        val fixtures = listOf(
            GameFixture(10, LocalDate.now(), 1, 1, 2),
            GameFixture(11, LocalDate.now().plusDays(1), 2, 2, 1)
        )
        val useCase = GetGameSchedulesAllUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(teams)
        )
        val schedules = useCase.execute()
        assertEquals(2, schedules.size)
        assertEquals(1, schedules.first().homeTeam.id)
        assertEquals(2, schedules.first().awayTeam.id)
    }

    @Test
    fun execute_throws_whenTeamMissing() = runTest {
        val fixtures = listOf(GameFixture(10, LocalDate.now(), 1, 1, 2))
        val useCase = GetGameSchedulesAllUseCase(
            FakeGameFixtureRepository(fixtures),
            FakeTeamRepository(emptyList())
        )
        try {
            useCase.execute()
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }
}
