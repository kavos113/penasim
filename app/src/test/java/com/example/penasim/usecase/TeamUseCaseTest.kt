package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.test.assertFailsWith

class TeamUseCaseTest {

    private class FakeTeamRepository(private val teams: List<Team>) : TeamRepository {
        override suspend fun getTeam(id: Int): Team? = teams.find { it.id == id }
        override suspend fun getTeamsByLeague(league: League): List<Team> = teams.filter { it.league == league }
        override suspend fun getAllTeams(): List<Team> = teams
    }

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

    private class FakeFielderAppointmentRepository(private val data: Map<Int, List<FielderAppointment>>) : FielderAppointmentRepository {
        override suspend fun getFielderAppointmentsByTeamId(teamId: Int): List<FielderAppointment> = data[teamId] ?: emptyList()
        override suspend fun getFielderAppointmentByPlayerId(playerId: Int): FielderAppointment? = null
        override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
    }

    private class FakePitcherAppointmentRepository(private val data: Map<Int, List<PitcherAppointment>>) : PitcherAppointmentRepository {
        override suspend fun getPitcherAppointmentsByTeamId(teamId: Int): List<PitcherAppointment> = data[teamId] ?: emptyList()
        override suspend fun getPitcherAppointmentByPlayerId(playerId: Int): PitcherAppointment? = null
        override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
    }

    private fun player(id: Int, teamId: Int) = Player(id, "F$id", "L$id", teamId, 1,1,1,1,1,1, 1,1,1, 1, 1)

    @Test
    fun getTeam_returnsTeamOrNull() = runTest {
        val t1 = Team(1, "Alpha", League.L1)
        val t2 = Team(2, "Beta", League.L2)
        val useCase = TeamUseCase(
            teamRepository = FakeTeamRepository(listOf(t1, t2)),
            playerRepository = FakePlayerRepository(emptyList()),
            fielderAppointmentRepository = FakeFielderAppointmentRepository(emptyMap()),
            pitcherAppointmentRepository = FakePitcherAppointmentRepository(emptyMap()),
            playerPositionRepository = FakePlayerPositionRepository(emptyList()),
            battingStatRepository = FakeBattingStatRepository(emptyList()),
            pitchingStatRepository = FakePitchingStatRepository(emptyList())
        )

        assertEquals(t1, useCase.getTeam(1))
        assertEquals(t2, useCase.getTeam(2))
        assertNull(useCase.getTeam(999))
    }

    @Test
    fun getTeamPlayers_composesInfos_andAppointments() = runTest {
        val team = Team(1, "A", League.L1)
        val other = Team(2, "B", League.L2)
        val p1 = player(10, 1)
        val p2 = player(11, 1)
        val p3 = player(20, 2)
        val positions = listOf(
            PlayerPosition(10, Position.OUTFIELDER, 50),
            PlayerPosition(11, Position.CATCHER, 60),
            PlayerPosition(20, Position.PITCHER, 70)
        )
        val batting = listOf(
            BattingStat(gameFixtureId = 100, playerId = 10, atBat = 4, hit = 2),
            BattingStat(gameFixtureId = 101, playerId = 10, atBat = 5, hit = 1),
            BattingStat(gameFixtureId = 100, playerId = 11, atBat = 3, hit = 2),
        )
        val pitching = listOf(
            PitchingStat(gameFixtureId = 100, playerId = 10, inningPitched = 3, strikeOut = 2, win = true),
            PitchingStat(gameFixtureId = 101, playerId = 10, inningPitched = 6, strikeOut = 5),
            PitchingStat(gameFixtureId = 100, playerId = 11, inningPitched = 3, strikeOut = 1),
        )
        val fApps = listOf(
            FielderAppointment(teamId = 1, playerId = 10, position = Position.OUTFIELDER, number = 1, orderType = OrderType.NORMAL),
            FielderAppointment(teamId = 1, playerId = 11, position = Position.CATCHER, number = 2, orderType = OrderType.NORMAL)
        )
        val pApps = listOf(
            PitcherAppointment(teamId = 1, playerId = 10, type = PitcherType.STARTER, number = 1),
            PitcherAppointment(teamId = 1, playerId = 21, type = PitcherType.CLOSER, number = 2)
        )

        val useCase = TeamUseCase(
            teamRepository = FakeTeamRepository(listOf(team, other)),
            playerRepository = FakePlayerRepository(listOf(p1, p2, p3)),
            fielderAppointmentRepository = FakeFielderAppointmentRepository(mapOf(1 to fApps)),
            pitcherAppointmentRepository = FakePitcherAppointmentRepository(mapOf(1 to pApps)),
            playerPositionRepository = FakePlayerPositionRepository(positions),
            battingStatRepository = FakeBattingStatRepository(batting),
            pitchingStatRepository = FakePitchingStatRepository(pitching)
        )

        val result = useCase.getTeamPlayers(1)

        assertEquals(team, result.team)
        assertEquals(2, result.players.size)
        // p1 totals: atBat 9, hit 3; innings 9, SO 7, wins 1
        val info1 = result.players.first { it.player.id == 10 }
        assertEquals(9, info1.battingStat.atBat)
        assertEquals(3, info1.battingStat.hit)
        assertEquals(9, info1.pitchingStat.inningsPitched)
        assertEquals(7, info1.pitchingStat.strikeOuts)
        assertEquals(1, info1.pitchingStat.wins)

        assertEquals(fApps, result.fielderAppointments)
        assertEquals(pApps, result.pitcherAppointments)
    }

    @Test
    fun getTeamPlayers_throws_whenTeamMissing() = runTest {
        val useCase = TeamUseCase(
            teamRepository = FakeTeamRepository(emptyList()),
            playerRepository = FakePlayerRepository(emptyList()),
            fielderAppointmentRepository = FakeFielderAppointmentRepository(emptyMap()),
            pitcherAppointmentRepository = FakePitcherAppointmentRepository(emptyMap()),
            playerPositionRepository = FakePlayerPositionRepository(emptyList()),
            battingStatRepository = FakeBattingStatRepository(emptyList()),
            pitchingStatRepository = FakePitchingStatRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { useCase.getTeamPlayers(1) }
    }
}

