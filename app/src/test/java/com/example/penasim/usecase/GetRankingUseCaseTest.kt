package com.example.penasim.usecase

import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.standing.domain.TeamStanding
import com.example.penasim.features.standing.usecase.RankingUseCase
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.schedule.domain.repository.GameFixtureRepository
import com.example.penasim.features.game.domain.repository.GameResultRepository
import com.example.penasim.features.team.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class GetRankingUseCaseTest {

    private fun assertDoubleEquals(expected: Double, actual: Double) =
        assertEquals(expected, actual, 0.001)

    @Test
    fun execute_calculatesRankings_winLossDraw_andGameBack() = runTest {
        val teamRepo: TeamRepository = mock()
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()

        val league = League.L1
        val t0 = Team(0, "T0", league)
        val t1 = Team(1, "T1", league)
        val t2 = Team(2, "T2", league)
        val t3 = Team(3, "T3", league)
        val t4 = Team(4, "T4", league)
        val t5 = Team(5, "T5", league)

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

        whenever(teamRepo.getTeamsByLeague(league)).thenReturn(listOf(t0, t1, t2, t3, t4, t5))

        // Per-team fixture mocks
        whenever(fixtureRepo.getGameFixturesByTeam(t0)).thenReturn(listOf(fixtures[0], fixtures[3]))
        whenever(fixtureRepo.getGameFixturesByTeam(t1)).thenReturn(listOf(fixtures[1], fixtures[4]))
        whenever(fixtureRepo.getGameFixturesByTeam(t2)).thenReturn(listOf(fixtures[2], fixtures[4]))
        whenever(fixtureRepo.getGameFixturesByTeam(t3)).thenReturn(listOf(fixtures[1], fixtures[5]))
        whenever(fixtureRepo.getGameFixturesByTeam(t4)).thenReturn(listOf(fixtures[2], fixtures[5]))
        whenever(fixtureRepo.getGameFixturesByTeam(t5)).thenReturn(listOf(fixtures[0], fixtures[3]))

        // Per-team result mocks (t0 and t5 share the same fixture ids)
        whenever(resultRepo.getGamesByFixtureIds(listOf(0, 3))).thenReturn(listOf(results[0], results[3]))
        whenever(resultRepo.getGamesByFixtureIds(listOf(1, 4))).thenReturn(listOf(results[1], results[4]))
        whenever(resultRepo.getGamesByFixtureIds(listOf(2, 4))).thenReturn(listOf(results[2], results[4]))
        whenever(resultRepo.getGamesByFixtureIds(listOf(1, 5))).thenReturn(listOf(results[1], results[5]))
        whenever(resultRepo.getGamesByFixtureIds(listOf(2, 5))).thenReturn(listOf(results[2], results[5]))

        val useCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val standings = useCase.getByLeague(league)

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

        assertDoubleEquals(0.0, s0.gameBack)
        assertDoubleEquals(0.5, s1.gameBack)
        assertDoubleEquals(1.0, s2.gameBack)
        assertDoubleEquals(1.0, s3.gameBack)
        assertDoubleEquals(1.5, s4.gameBack)
        assertDoubleEquals(2.0, s5.gameBack)
    }

    @Test
    fun getAll_returnsCombinedStandingsFromBothLeagues() = runTest {
        val teamRepo: TeamRepository = mock()
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()

        val l1 = League.L1
        val l2 = League.L2
        val t0 = Team(0, "L1-A", l1)
        val t1 = Team(1, "L1-B", l1)
        val t2 = Team(2, "L2-A", l2)
        val t3 = Team(3, "L2-B", l2)

        val d1 = LocalDate.of(2025, 4, 1)

        val fixtures = listOf(
            GameFixture(id = 0, date = d1, numberOfGames = 0, homeTeamId = t0.id, awayTeamId = t1.id),
            GameFixture(id = 1, date = d1, numberOfGames = 1, homeTeamId = t2.id, awayTeamId = t3.id),
        )

        val results = listOf(
            GameResult(fixtureId = 0, homeScore = 3, awayScore = 1),
            GameResult(fixtureId = 1, homeScore = 2, awayScore = 5),
        )

        whenever(teamRepo.getTeamsByLeague(l1)).thenReturn(listOf(t0, t1))
        whenever(teamRepo.getTeamsByLeague(l2)).thenReturn(listOf(t2, t3))

        // L1 per-team mocks
        whenever(fixtureRepo.getGameFixturesByTeam(t0)).thenReturn(listOf(fixtures[0]))
        whenever(fixtureRepo.getGameFixturesByTeam(t1)).thenReturn(listOf(fixtures[0]))
        whenever(resultRepo.getGamesByFixtureIds(listOf(0))).thenReturn(listOf(results[0]))

        // L2 per-team mocks
        whenever(fixtureRepo.getGameFixturesByTeam(t2)).thenReturn(listOf(fixtures[1]))
        whenever(fixtureRepo.getGameFixturesByTeam(t3)).thenReturn(listOf(fixtures[1]))
        whenever(resultRepo.getGamesByFixtureIds(listOf(1))).thenReturn(listOf(results[1]))

        // Other leagues return empty
        League.entries.filter { it != l1 && it != l2 }.forEach { league ->
            whenever(teamRepo.getTeamsByLeague(league)).thenReturn(emptyList())
        }

        val useCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val all = useCase.getAll()

        // L1: t0 wins, t1 loses; L2: t3 wins, t2 loses
        assertEquals(4, all.size)

        val l1Standings = all.filter { it.team.league == l1 }
        val l2Standings = all.filter { it.team.league == l2 }
        assertEquals(2, l1Standings.size)
        assertEquals(2, l2Standings.size)

        assertEquals(t0.id, l1Standings[0].team.id)
        assertEquals(1, l1Standings[0].rank)
        assertEquals(t3.id, l2Standings[0].team.id)
        assertEquals(1, l2Standings[0].rank)
    }

    @Test
    fun getByLeague_emptyTeams_returnsEmptyList() = runTest {
        val teamRepo: TeamRepository = mock()
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()

        whenever(teamRepo.getTeamsByLeague(League.L1)).thenReturn(emptyList())

        val useCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val standings = useCase.getByLeague(League.L1)
        assertEquals(emptyList<TeamStanding>(), standings)
    }

    @Test
    fun getByLeague_teamsWithNoGames_allZeroStats() = runTest {
        val teamRepo: TeamRepository = mock()
        val fixtureRepo: GameFixtureRepository = mock()
        val resultRepo: GameResultRepository = mock()

        val league = League.L1
        val t0 = Team(0, "A", league)
        val t1 = Team(1, "B", league)

        whenever(teamRepo.getTeamsByLeague(league)).thenReturn(listOf(t0, t1))
        whenever(fixtureRepo.getGameFixturesByTeam(t0)).thenReturn(emptyList())
        whenever(fixtureRepo.getGameFixturesByTeam(t1)).thenReturn(emptyList())
        whenever(resultRepo.getGamesByFixtureIds(emptyList())).thenReturn(emptyList())

        val useCase = RankingUseCase(teamRepo, fixtureRepo, resultRepo)
        val standings = useCase.getByLeague(league)

        assertEquals(2, standings.size)
        standings.forEach {
            assertEquals(0, it.wins)
            assertEquals(0, it.losses)
            assertEquals(0, it.draws)
        }
    }
}
