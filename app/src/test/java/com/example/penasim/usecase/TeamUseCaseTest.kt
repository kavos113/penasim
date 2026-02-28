package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

class TeamUseCaseTest {

    private fun player(id: Int, teamId: Int) = Player(id, "F$id", "L$id", teamId, 1,1,1,1,1,1, 1,1,1, 1, 1)

    @Test
    fun getTeam_returnsTeamOrNull() = runTest {
        val teamRepo: TeamRepository = mock()
        val t1 = Team(1, "Alpha", League.L1)
        val t2 = Team(2, "Beta", League.L2)
        whenever(teamRepo.getTeam(1)).thenReturn(t1)
        whenever(teamRepo.getTeam(2)).thenReturn(t2)

        val useCase = TeamUseCase(
            teamRepository = teamRepo,
            playerRepository = mock(),
            fielderAppointmentRepository = mock(),
            pitcherAppointmentRepository = mock(),
            playerPositionRepository = mock(),
            battingStatRepository = mock(),
            pitchingStatRepository = mock()
        )

        assertEquals(t1, useCase.getTeam(1))
        assertEquals(t2, useCase.getTeam(2))
        assertNull(useCase.getTeam(999))
    }

    @Test
    fun getTeamPlayers_composesInfos_andAppointments() = runTest {
        val teamRepo: TeamRepository = mock()
        val playerRepo: PlayerRepository = mock()
        val positionRepo: PlayerPositionRepository = mock()
        val battingRepo: BattingStatRepository = mock()
        val pitchingRepo: PitchingStatRepository = mock()
        val fielderAppointmentRepo: FielderAppointmentRepository = mock()
        val pitcherAppointmentRepo: PitcherAppointmentRepository = mock()

        val team = Team(1, "A", League.L1)
        val p1 = player(10, 1)
        val p2 = player(11, 1)

        whenever(teamRepo.getTeam(1)).thenReturn(team)
        whenever(playerRepo.getPlayers(1)).thenReturn(listOf(p1, p2))

        // player 10
        whenever(positionRepo.getPlayerPositions(10)).thenReturn(
            listOf(PlayerPosition(10, Position.OUTFIELDER, 50))
        )
        whenever(battingRepo.getByPlayerId(10)).thenReturn(listOf(
            BattingStat(gameFixtureId = 100, playerId = 10, atBat = 4, hit = 2),
            BattingStat(gameFixtureId = 101, playerId = 10, atBat = 5, hit = 1),
        ))
        whenever(pitchingRepo.getByPlayerId(10)).thenReturn(listOf(
            PitchingStat(gameFixtureId = 100, playerId = 10, inningPitched = 3, strikeOut = 2, win = true),
            PitchingStat(gameFixtureId = 101, playerId = 10, inningPitched = 6, strikeOut = 5),
        ))

        // player 11
        whenever(positionRepo.getPlayerPositions(11)).thenReturn(
            listOf(PlayerPosition(11, Position.CATCHER, 60))
        )
        whenever(battingRepo.getByPlayerId(11)).thenReturn(listOf(
            BattingStat(gameFixtureId = 100, playerId = 11, atBat = 3, hit = 2),
        ))
        whenever(pitchingRepo.getByPlayerId(11)).thenReturn(listOf(
            PitchingStat(gameFixtureId = 100, playerId = 11, inningPitched = 3, strikeOut = 1),
        ))

        val fApps = listOf(
            FielderAppointment(teamId = 1, playerId = 10, position = Position.OUTFIELDER, number = 1, orderType = OrderType.NORMAL),
            FielderAppointment(teamId = 1, playerId = 11, position = Position.CATCHER, number = 2, orderType = OrderType.NORMAL)
        )
        val pApps = listOf(
            PitcherAppointment(teamId = 1, playerId = 10, type = PitcherType.STARTER, number = 1),
            PitcherAppointment(teamId = 1, playerId = 21, type = PitcherType.CLOSER, number = 2)
        )
        whenever(fielderAppointmentRepo.getFielderAppointmentsByTeamId(1)).thenReturn(fApps)
        whenever(pitcherAppointmentRepo.getPitcherAppointmentsByTeamId(1)).thenReturn(pApps)

        val useCase = TeamUseCase(
            teamRepository = teamRepo,
            playerRepository = playerRepo,
            fielderAppointmentRepository = fielderAppointmentRepo,
            pitcherAppointmentRepository = pitcherAppointmentRepo,
            playerPositionRepository = positionRepo,
            battingStatRepository = battingRepo,
            pitchingStatRepository = pitchingRepo
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
        val teamRepo: TeamRepository = mock()
        whenever(teamRepo.getTeam(1)).thenReturn(null)

        val useCase = TeamUseCase(
            teamRepository = teamRepo,
            playerRepository = mock(),
            fielderAppointmentRepository = mock(),
            pitcherAppointmentRepository = mock(),
            playerPositionRepository = mock(),
            battingStatRepository = mock(),
            pitchingStatRepository = mock()
        )
        assertFailsWith<IllegalArgumentException> { useCase.getTeamPlayers(1) }
    }
}

