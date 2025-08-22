package com.example.penasim.usecase

import com.example.penasim.domain.Game
import com.example.penasim.domain.GameRepository
import com.example.penasim.domain.League
import com.example.penasim.domain.Schedule
import com.example.penasim.domain.Team
import com.example.penasim.domain.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRankingUseCaseTest {

    private class FakeTeamRepository(private val teams: List<Team>) : TeamRepository {
        override suspend fun getTeam(id: Int): Team? = teams.find { it.id == id }
        override suspend fun getTeamsByLeague(league: League): List<Team> = teams.filter { it.league == league }
        override suspend fun getAllTeams(): List<Team> = teams
    }

    private class FakeGameRepository(allGames: List<Game>) : GameRepository {
        private val gamesByTeam: Map<Team, List<Game>>

        init {
            val map = mutableMapOf<Team, MutableList<Game>>()
            for (g in allGames) {
                map.getOrPut(g.homeTeam) { mutableListOf() }.add(g)
                map.getOrPut(g.awayTeam) { mutableListOf() }.add(g)
            }
            gamesByTeam = map
        }

        override suspend fun getGame(id: Int): Game? = null
        override suspend fun getGamesByDate(date: Schedule): List<Game> = emptyList()
        override suspend fun getGamesByTeam(team: Team): List<Game> = gamesByTeam[team] ?: emptyList()
        override suspend fun getFinishedGamesByTeam(team: Team): List<Game> = (gamesByTeam[team] ?: emptyList()).filter { it.isFinished }
        override suspend fun getGamesByLeague(league: League): List<Game> = emptyList()
        override suspend fun getAllGames(): List<Game> = gamesByTeam.values.flatten()
        override suspend fun recordGameResult(gameId: Int, homeScore: Int, awayScore: Int): Boolean = false
    }

    @Test
    fun execute_calculatesRankings_winLossDraw_andGameBack() = runTest {
        // Prepare teams in one league
        val league = League.L1
        val t0 = Team(0, "T0", league)
        val t1 = Team(1, "T1", league)
        val t2 = Team(2, "T2", league)
        val t3 = Team(3, "T3", league)
        val t4 = Team(4, "T4", league)
        val t5 = Team(5, "T5", league)
        val teams = listOf(t0, t1, t2, t3, t4, t5)

        // Two dates (schedules). Use empty games list to avoid circular references.
        val d1 = Schedule(1, 4, 1, 2, emptyList())
        val d2 = Schedule(2, 4, 2, 3, emptyList())

        // Construct games to match the distribution after two rounds used in PennantManagerTest:
        // t0: 2-0-0, t1: 1-0-1, t2: 1-1-0, t3: 0-0-2, t4: 0-1-1, t5: 0-2-0
        val games = listOf(
            // Round 1
            Game(id = 1, date = d1, homeTeam = t0, awayTeam = t5, homeScore = 5, awayScore = 3, isFinished = true), // t0 W, t5 L
            Game(id = 2, date = d1, homeTeam = t1, awayTeam = t3, homeScore = 4, awayScore = 4, isFinished = true), // draw
            Game(id = 3, date = d1, homeTeam = t2, awayTeam = t4, homeScore = 6, awayScore = 2, isFinished = true), // t2 W, t4 L
            // Round 2
            Game(id = 4, date = d2, homeTeam = t0, awayTeam = t5, homeScore = 7, awayScore = 2, isFinished = true), // t0 W, t5 L
            Game(id = 5, date = d2, homeTeam = t1, awayTeam = t2, homeScore = 3, awayScore = 1, isFinished = true), // t1 W, t2 L
            Game(id = 6, date = d2, homeTeam = t3, awayTeam = t4, homeScore = 2, awayScore = 2, isFinished = true), // draw
        )

        val teamRepo = FakeTeamRepository(teams)
        val gameRepo = FakeGameRepository(games)
        val useCase = GetRankingUseCase(teamRepo, gameRepo)

        val standings = useCase.execute(league)

        // Ensure we have standings for all teams
        assertEquals(6, standings.size)

        // Find each team's standing
        val s0 = standings.first { it.team.id == 0 }
        val s1 = standings.first { it.team.id == 1 }
        val s2 = standings.first { it.team.id == 2 }
        val s3 = standings.first { it.team.id == 3 }
        val s4 = standings.first { it.team.id == 4 }
        val s5 = standings.first { it.team.id == 5 }

        // Wins/Losses/Draws
        assertEquals(2, s0.wins); assertEquals(0, s0.losses); assertEquals(0, s0.draws)
        assertEquals(1, s1.wins); assertEquals(0, s1.losses); assertEquals(1, s1.draws)
        assertEquals(1, s2.wins); assertEquals(1, s2.losses); assertEquals(0, s2.draws)
        assertEquals(0, s3.wins); assertEquals(0, s3.losses); assertEquals(2, s3.draws)
        assertEquals(0, s4.wins); assertEquals(1, s4.losses); assertEquals(1, s4.draws)
        assertEquals(0, s5.wins); assertEquals(2, s5.losses); assertEquals(0, s5.draws)

        // Rank assertions based on current sorting (wins desc, then losses desc, then draws desc)
        assertEquals(1, s0.rank) // 2-0-0
        assertEquals(2, s2.rank) // 1-1-0 has more losses than 1-0-1, so ranks above due to losses desc
        assertEquals(3, s1.rank) // 1-0-1
        assertEquals(4, s5.rank) // 0-2-0
        assertEquals(5, s4.rank) // 0-1-1
        assertEquals(6, s3.rank) // 0-0-2

        // Game back values relative to leader should match PennantManagerTest results:
        // s0: 0.0, s1: 0.5, s2: 1.0, s3: 1.0, s4: 1.5, s5: 2.0
        fun assertDoubleEquals(expected: Double, actual: Double) =
            assertEquals(expected, actual, 0.001)

        assertDoubleEquals(0.0, s0.gameBack)
        assertDoubleEquals(0.5, s1.gameBack)
        assertDoubleEquals(1.0, s2.gameBack)
        assertDoubleEquals(1.0, s3.gameBack)
        assertDoubleEquals(1.5, s4.gameBack)
        assertDoubleEquals(2.0, s5.gameBack)
    }
}