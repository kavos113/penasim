package com.example.penasim.usecase

import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.repository.BattingStatRepository
import com.example.penasim.domain.repository.PitchingStatRepository
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.domain.toTotalBattingStats
import com.example.penasim.domain.toTotalPitchingStats
import javax.inject.Inject

class GetPlayerInfosByTeamUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val playerPositionRepository: PlayerPositionRepository,
    private val teamRepository: TeamRepository,
    private val battingStatRepository: BattingStatRepository,
    private val pitchingStatRepository: PitchingStatRepository
) {
    suspend fun execute(teamId: Int): List<PlayerInfo> {
        val team = teamRepository.getTeam(teamId)
            ?: throw IllegalArgumentException("Team with id $teamId not found")
        val players = playerRepository.getPlayers(teamId)
        return players.map { player ->
            val positions = playerPositionRepository.getPlayerPositions(player.id)
            val battingStats = battingStatRepository.getByPlayerId(player.id).toTotalBattingStats()
            val pitchingStats = pitchingStatRepository.getByPlayerId(player.id).toTotalPitchingStats()
            PlayerInfo(
                player = player,
                team = team,
                battingStat = battingStats,
                pitchingStat = pitchingStats,
                positions = positions
            )
        }
    }
}