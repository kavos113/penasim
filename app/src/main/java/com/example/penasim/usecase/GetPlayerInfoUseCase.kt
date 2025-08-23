package com.example.penasim.usecase

import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository

class GetPlayerInfoUseCase(
    private val playerRepository: PlayerRepository,
    private val playerPositionRepository: PlayerPositionRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(playerId: Int): PlayerInfo {
        val player = playerRepository.getPlayer(playerId)
            ?: throw IllegalArgumentException("Player with id $playerId not found")
        val team = teamRepository.getTeam(player.teamId)
            ?: throw IllegalArgumentException("Team with id ${player.teamId} not found")
        val positions = playerPositionRepository.getPlayerPositions(playerId)
        return PlayerInfo(
            player = player,
            team = team,
            positions = positions
        )
    }
}