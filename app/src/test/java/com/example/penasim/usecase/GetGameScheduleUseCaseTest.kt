package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class GetGameScheduleUseCaseTest {

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
    fun execute_returnsSchedule_whenDataExists() = runTest {
        val league = League.L1
        val home = Team(1, "H", league)
        val away = Team(2, "A", league)
        val fixture = GameFixture(10, LocalDate.now(), 1, 1, 2)
        val useCase = GetGameScheduleUseCase(
            FakeGameFixtureRepository(listOf(fixture)),
            FakeTeamRepository(listOf(home, away))
        )
        val schedule = useCase.execute(10)
        assertEquals(fixture, schedule.fixture)
        assertEquals(home, schedule.homeTeam)
        assertEquals(away, schedule.awayTeam)
    }

    @Test
    fun execute_throws_whenMissing() = runTest {
        val league = League.L1
        val home = Team(1, "H", league)
        val away = Team(2, "A", league)
        val fixture = GameFixture(10, LocalDate.now(), 1, 1, 2)

        // Missing fixture
        try {
            GetGameScheduleUseCase(
                FakeGameFixtureRepository(emptyList()),
                FakeTeamRepository(listOf(home, away))
            ).execute(10)
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
        // Missing home team
        try {
            GetGameScheduleUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeTeamRepository(listOf(away))
            ).execute(10)
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
        // Missing away team
        try {
            GetGameScheduleUseCase(
                FakeGameFixtureRepository(listOf(fixture)),
                FakeTeamRepository(listOf(home))
            ).execute(10)
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }
}
