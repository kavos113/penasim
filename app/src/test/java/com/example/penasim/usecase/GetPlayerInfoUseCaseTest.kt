package com.example.penasim.usecase

import com.example.penasim.domain.*
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class GetPlayerInfoUseCaseTest {

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
    fun execute_returnsPlayerInfo() = runTest {
        val team = Team(1, "A", League.L1)
        val p = player(10, 1)
        val positions = listOf(
            PlayerPosition(10, Position.OUTFIELDER, 50),
            PlayerPosition(10, Position.CATCHER, 60)
        )
        val useCase = GetPlayerInfoUseCase(
            FakePlayerRepository(listOf(p)),
            FakePlayerPositionRepository(positions),
            FakeTeamRepository(listOf(team))
        )

        val info = useCase.execute(10)

        assertEquals(p, info.player)
        assertEquals(team, info.team)
        assertEquals(positions, info.positions)
    }

    @Test
    fun execute_throws_whenPlayerOrTeamMissing() = runTest {
        val team = Team(1, "A", League.L1)
        val p = player(10, 1)
        val positions = listOf(PlayerPosition(10, Position.OUTFIELDER, 50))

        // player missing
        assertFailsWith<IllegalArgumentException> {
            GetPlayerInfoUseCase(
                FakePlayerRepository(emptyList()),
                FakePlayerPositionRepository(positions),
                FakeTeamRepository(listOf(team))
            ).execute(10)
        }

        // team missing
        assertFailsWith<IllegalArgumentException> {
            GetPlayerInfoUseCase(
                FakePlayerRepository(listOf(p)),
                FakePlayerPositionRepository(positions),
                FakeTeamRepository(emptyList())
            ).execute(10)
        }
    }
}
