package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.BattingStatRepository
import com.example.penasim.domain.repository.PitchingStatRepository
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class PlayerInfoUseCaseTest {

    private class FakePlayerRepository(private val players: List<Player>) : PlayerRepository {
        override suspend fun getPlayerCount(teamId: Int): Int = players.count { it.teamId == teamId }
        override suspend fun getPlayers(teamId: Int): List<Player> = players.filter { it.teamId == teamId }
        override suspend fun getPlayer(id: Int): Player? = players.find { it.id == id }
        override suspend fun getAllPlayers(): List<Player> = players
    }

    private class FakePlayerPositionRepository(private val positions: List<PlayerPosition>) : PlayerPositionRepository {
        override suspend fun getPlayerPositions(playerId: Int): List<PlayerPosition> = positions.filter { it.playerId == playerId }
        override suspend fun getAllPlayerPositions(): List<PlayerPosition> = positions
        override suspend fun getAllPlayerPositionsByPosition(position: Position): List<PlayerPosition> = positions.filter { it.position == position }
    }

    private class FakeTeamRepository(private val teams: List<Team>) : TeamRepository {
        override suspend fun getTeam(id: Int): Team? = teams.find { it.id == id }
        override suspend fun getTeamsByLeague(league: League): List<Team> = teams.filter { it.league == league }
        override suspend fun getAllTeams(): List<Team> = teams
    }

    private class FakeBattingStatRepository(private val stats: List<BattingStat>) : BattingStatRepository {
        override suspend fun getByFixtureId(fixtureId: Int): List<BattingStat> = stats.filter { it.gameFixtureId == fixtureId }
        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<BattingStat> = stats.filter { it.gameFixtureId in fixtureIds }
        override suspend fun getByPlayerId(playerId: Int): List<BattingStat> = stats.filter { it.playerId == playerId }
        override suspend fun getByPlayerIds(playerIds: List<Int>): List<BattingStat> = stats.filter { it.playerId in playerIds }
        override suspend fun insertAll(items: List<BattingStat>) {}
        override suspend fun deleteByFixtureId(fixtureId: Int) {}
    }

    private class FakePitchingStatRepository(private val stats: List<PitchingStat>) : PitchingStatRepository {
        override suspend fun getByFixtureId(fixtureId: Int): List<PitchingStat> = stats.filter { it.gameFixtureId == fixtureId }
        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<PitchingStat> = stats.filter { it.gameFixtureId in fixtureIds }
        override suspend fun getByPlayerId(playerId: Int): List<PitchingStat> = stats.filter { it.playerId == playerId }
        override suspend fun getByPlayerIds(playerIds: List<Int>): List<PitchingStat> = stats.filter { it.playerId in playerIds }
        override suspend fun insertAll(items: List<PitchingStat>) {}
        override suspend fun deleteByFixtureId(fixtureId: Int) {}
    }

    private fun player(id: Int, teamId: Int) = Player(id, "F$id", "L$id", teamId, 1,1,1,1,1,1, 1,1,1, 1, 1)

    @Test
    fun getByPlayerId_returnsPlayerInfo_withTotals() = runTest {
        val team = Team(1, "A", League.L1)
        val p = player(10, 1)
        val positions = listOf(PlayerPosition(10, Position.OUTFIELDER, 50), PlayerPosition(10, Position.CATCHER, 60))
        val batting = listOf(
            BattingStat(gameFixtureId = 100, playerId = 10, atBat = 4, hit = 2),
            BattingStat(gameFixtureId = 101, playerId = 10, atBat = 5, hit = 1, homeRun = 1)
        )
        val pitching = listOf(
            PitchingStat(gameFixtureId = 100, playerId = 10, inningPitched = 3, strikeOut = 2, win = true),
            PitchingStat(gameFixtureId = 101, playerId = 10, inningPitched = 6, strikeOut = 5)
        )
        val useCase = PlayerInfoUseCase(
            FakePlayerRepository(listOf(p)),
            FakePlayerPositionRepository(positions),
            FakeTeamRepository(listOf(team)),
            FakeBattingStatRepository(batting),
            FakePitchingStatRepository(pitching)
        )

        val info = useCase.getByPlayerId(10)

        assertEquals(p, info.player)
        assertEquals(team, info.team)
        assertEquals(positions, info.positions)
        assertEquals(9, info.battingStat.atBat)
        assertEquals(3, info.battingStat.hit)
        assertEquals(9, info.pitchingStat.inningsPitched)
        assertEquals(7, info.pitchingStat.strikeOuts)
        assertEquals(1, info.pitchingStat.wins)
    }

    @Test
    fun getByTeamId_returnsInfos_forTeam_andThrowsWhenTeamMissing() = runTest {
        val team = Team(1, "A", League.L1)
        val ps = listOf(player(10,1), player(11,1), player(20,2))
        val positions = listOf(
            PlayerPosition(10, Position.OUTFIELDER, 50),
            PlayerPosition(11, Position.CATCHER, 60),
            PlayerPosition(20, Position.PITCHER, 70)
        )
        val batting = listOf(
            BattingStat(gameFixtureId = 100, playerId = 10, atBat = 4, hit = 2),
            BattingStat(gameFixtureId = 101, playerId = 11, atBat = 5, hit = 3)
        )
        val pitching = listOf(
            PitchingStat(gameFixtureId = 100, playerId = 10, inningPitched = 3, strikeOut = 2),
            PitchingStat(gameFixtureId = 101, playerId = 11, inningPitched = 6, strikeOut = 5)
        )
        val useCase = PlayerInfoUseCase(
            FakePlayerRepository(ps),
            FakePlayerPositionRepository(positions),
            FakeTeamRepository(listOf(team)),
            FakeBattingStatRepository(batting),
            FakePitchingStatRepository(pitching)
        )

        val infos = useCase.getByTeamId(1)
        assertEquals(2, infos.size)
        assertEquals(team, infos[0].team)
        assertEquals(team, infos[1].team)
        assertEquals(2, infos[0].battingStat.hit)
        assertEquals(3, infos[1].battingStat.hit)

        // team missing -> throws
        val missingTeamUseCase = PlayerInfoUseCase(
            FakePlayerRepository(emptyList()),
            FakePlayerPositionRepository(emptyList()),
            FakeTeamRepository(emptyList()),
            FakeBattingStatRepository(emptyList()),
            FakePitchingStatRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { missingTeamUseCase.getByTeamId(1) }
    }
}

