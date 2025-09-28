package com.example.penasim.usecase

import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.repository.BattingStatRepository
import com.example.penasim.domain.repository.PitchingStatRepository
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.domain.toTotalBattingStats
import com.example.penasim.domain.toTotalPitchingStats

class GetPlayerInfoUseCase(
    private val playerRepository: PlayerRepository,
    private val playerPositionRepository: PlayerPositionRepository,
    private val teamRepository: TeamRepository,
    private val battingStatRepository: BattingStatRepository,
    private val pitchingStatRepository: PitchingStatRepository,
) {
    suspend fun execute(playerId: Int): PlayerInfo {
        val player = playerRepository.getPlayer(playerId)
            ?: throw IllegalArgumentException("Player with id $playerId not found")
        val team = teamRepository.getTeam(player.teamId)
            ?: throw IllegalArgumentException("Team with id ${player.teamId} not found")
        val positions = playerPositionRepository.getPlayerPositions(playerId)

        val battingStats = battingStatRepository.getByPlayerId(playerId).toTotalBattingStats()
        val pitchingStats = pitchingStatRepository.getByPlayerId(playerId).toTotalPitchingStats()

        return PlayerInfo(
            player = player,
            positions = positions,
            team = team,
            battingStat = battingStats,
            pitchingStat = pitchingStats
        )
    }
}