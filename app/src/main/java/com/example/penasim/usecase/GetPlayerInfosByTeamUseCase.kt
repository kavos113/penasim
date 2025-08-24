package com.example.penasim.usecase

import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import javax.inject.Inject

class GetPlayerInfosByTeamUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val playerPositionRepository: PlayerPositionRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun execute(teamId: Int): List<PlayerInfo> {
        val team = teamRepository.getTeam(teamId)
            ?: throw IllegalArgumentException("Team with id $teamId not found")
        val players = playerRepository.getPlayers(teamId)
        return players.map { player ->
            val positions = playerPositionRepository.getPlayerPositions(player.id)
            PlayerInfo(
                player = player,
                team = team,
                positions = positions
            )
        }
    }
}