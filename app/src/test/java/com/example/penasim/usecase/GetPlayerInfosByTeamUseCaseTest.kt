package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class GetPlayerInfosByTeamUseCaseTest {

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

    private fun player(id: Int, teamId: Int) = Player(id, "F$id", "L$id", teamId, 1,1,1,1,1,1, 1,1,1)

    @Test
    fun execute_returnsPlayerInfosOfTeam() = runTest {
        val team = Team(1, "A", League.L1)
        val ps = listOf(player(10,1), player(11,1), player(20,2))
        val positions = listOf(
            PlayerPosition(10, Position.OUTFIELDER, 50),
            PlayerPosition(11, Position.CATCHER, 60),
            PlayerPosition(20, Position.PITCHER, 70)
        )
        val useCase = GetPlayerInfosByTeamUseCase(
            FakePlayerRepository(ps),
            FakePlayerPositionRepository(positions),
            FakeTeamRepository(listOf(team))
        )

        val infos = useCase.execute(1)

        assertEquals(2, infos.size)
        assertEquals(10, infos[0].player.id)
        assertEquals(11, infos[1].player.id)
        assertEquals(team, infos[0].team)
        assertEquals(team, infos[1].team)
    }

    @Test
    fun execute_throws_whenTeamMissing() = runTest {
        val useCase = GetPlayerInfosByTeamUseCase(
            FakePlayerRepository(emptyList()),
            FakePlayerPositionRepository(emptyList()),
            FakeTeamRepository(emptyList())
        )
        assertFailsWith<IllegalArgumentException> { useCase.execute(1) }
    }
}
