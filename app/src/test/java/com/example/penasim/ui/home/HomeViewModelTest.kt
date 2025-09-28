package com.example.penasim.ui.home

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.testing.MainDispatcherRule
import com.example.penasim.usecase.GetGameInfoAllUseCase
import com.example.penasim.usecase.GetRankingUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class HomeViewModelTest {

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

    private class FakeGameResultRepository(private val results: List<GameResult>) : GameResultRepository {
        override suspend fun getGameByFixtureId(fixtureId: Int): GameResult? = results.find { it.fixtureId == fixtureId }
        override suspend fun getGamesByFixtureIds(fixtureIds: List<Int>): List<GameResult> = results.filter { it.fixtureId in fixtureIds }
        override suspend fun getAllGames(): List<GameResult> = results
        override suspend fun deleteAllGames() {}
        override suspend fun createGame(fixtureId: Int, homeScore: Int, awayScore: Int): GameResult? = null
    }

    @Test
    fun init_updatesRank_andCurrentDay_fromUseCases() = runTest {
        val myTeam = Team(id = 0, name = "Mine", league = League.L1)
        val other = Team(id = 1, name = "Other", league = League.L1)

        val standingsL1 = listOf(
            TeamStanding(team = myTeam, rank = 1, wins = 10, losses = 5),
            TeamStanding(team = other, rank = 2, wins = 9, losses = 6)
        )

        val lastPlayed = LocalDate.of(2025, 4, 10)
        val nextDay = lastPlayed.plusDays(1)
        val fixtures = listOf(
            GameFixture(100, lastPlayed, 1, myTeam.id, other.id)
        )
        val infos = listOf(
            GameResult(fixtureId = fixtures[0].id, homeScore = 3, awayScore = 2)
        )

        val rankingUseCase = GetRankingUseCase(
            teamRepository = FakeTeamRepository(listOf(myTeam, other)),
            gameFixtureRepository = FakeGameFixtureRepository(fixtures),
            gameResultRepository = FakeGameResultRepository(infos)
        )

        val gameInfoAllUseCase = GetGameInfoAllUseCase(
            gameFixtureRepository = FakeGameFixtureRepository(fixtures),
            gameResultRepository = FakeGameResultRepository(infos),
            teamRepository = FakeTeamRepository(listOf(myTeam, other))
        )

        val vm = HomeViewModel(
            getRankingUseCase = rankingUseCase,
            getGameInfoAllUseCase = gameInfoAllUseCase
        )

        val state = vm.uiState.value
        assertEquals(1, state.rank)
        assertEquals(nextDay, state.currentDay)
    }
}