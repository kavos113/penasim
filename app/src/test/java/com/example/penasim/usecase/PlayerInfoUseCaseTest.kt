package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

class PlayerInfoUseCaseTest {

    private fun player(id: Int, teamId: Int) = Player(id, "F$id", "L$id", teamId, 1,1,1,1,1,1, 1,1,1, 1, 1)

    @Test
    fun getByPlayerId_returnsPlayerInfo_withTotals() = runTest {
        val playerRepo: PlayerRepository = mock()
        val posRepo: PlayerPositionRepository = mock()
        val teamRepo: TeamRepository = mock()
        val battingRepo: BattingStatRepository = mock()
        val pitchingRepo: PitchingStatRepository = mock()

        val p = player(10, 1)
        val team = Team(1, "A", League.L1)
        val positions = listOf(PlayerPosition(10, Position.OUTFIELDER, 50), PlayerPosition(10, Position.CATCHER, 60))
        val battingStats = listOf(
            BattingStat(gameFixtureId = 100, playerId = 10, atBat = 4, hit = 2),
            BattingStat(gameFixtureId = 101, playerId = 10, atBat = 5, hit = 1, homeRun = 1)
        )
        val pitchingStats = listOf(
            PitchingStat(gameFixtureId = 100, playerId = 10, inningPitched = 3, strikeOut = 2, win = true),
            PitchingStat(gameFixtureId = 101, playerId = 10, inningPitched = 6, strikeOut = 5)
        )

        whenever(playerRepo.getPlayer(10)).thenReturn(p)
        whenever(teamRepo.getTeam(1)).thenReturn(team)
        whenever(posRepo.getPlayerPositions(10)).thenReturn(positions)
        whenever(battingRepo.getByPlayerId(10)).thenReturn(battingStats)
        whenever(pitchingRepo.getByPlayerId(10)).thenReturn(pitchingStats)

        val useCase = PlayerInfoUseCase(playerRepo, posRepo, teamRepo, battingRepo, pitchingRepo)
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
        val playerRepo: PlayerRepository = mock()
        val posRepo: PlayerPositionRepository = mock()
        val teamRepo: TeamRepository = mock()
        val battingRepo: BattingStatRepository = mock()
        val pitchingRepo: PitchingStatRepository = mock()

        val team = Team(1, "A", League.L1)
        val p10 = player(10, 1)
        val p11 = player(11, 1)

        whenever(teamRepo.getTeam(1)).thenReturn(team)
        whenever(playerRepo.getPlayers(1)).thenReturn(listOf(p10, p11))

        whenever(posRepo.getPlayerPositions(10)).thenReturn(listOf(PlayerPosition(10, Position.OUTFIELDER, 50)))
        whenever(posRepo.getPlayerPositions(11)).thenReturn(listOf(PlayerPosition(11, Position.CATCHER, 60)))

        whenever(battingRepo.getByPlayerId(10)).thenReturn(listOf(BattingStat(gameFixtureId = 100, playerId = 10, atBat = 4, hit = 2)))
        whenever(battingRepo.getByPlayerId(11)).thenReturn(listOf(BattingStat(gameFixtureId = 101, playerId = 11, atBat = 5, hit = 3)))

        whenever(pitchingRepo.getByPlayerId(10)).thenReturn(listOf(PitchingStat(gameFixtureId = 100, playerId = 10, inningPitched = 3, strikeOut = 2)))
        whenever(pitchingRepo.getByPlayerId(11)).thenReturn(listOf(PitchingStat(gameFixtureId = 101, playerId = 11, inningPitched = 6, strikeOut = 5)))

        val useCase = PlayerInfoUseCase(playerRepo, posRepo, teamRepo, battingRepo, pitchingRepo)
        val infos = useCase.getByTeamId(1)

        assertEquals(2, infos.size)
        assertEquals(team, infos[0].team)
        assertEquals(team, infos[1].team)
        assertEquals(2, infos[0].battingStat.hit)
        assertEquals(3, infos[1].battingStat.hit)

        // team missing -> throws
        val missingTeamRepo: TeamRepository = mock()
        whenever(missingTeamRepo.getTeam(1)).thenReturn(null)
        val missingTeamUseCase = PlayerInfoUseCase(mock(), mock(), missingTeamRepo, mock(), mock())
        assertFailsWith<IllegalArgumentException> { missingTeamUseCase.getByTeamId(1) }
    }
}

