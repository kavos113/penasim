package com.example.penasim.features.team.usecase

import com.example.penasim.features.player.domain.PlayerInfo
import com.example.penasim.features.command.domain.TeamPlayers
import com.example.penasim.features.game.domain.repository.BattingStatRepository
import com.example.penasim.features.command.domain.repository.FielderAppointmentRepository
import com.example.penasim.features.command.domain.repository.PitcherAppointmentRepository
import com.example.penasim.features.game.domain.repository.PitchingStatRepository
import com.example.penasim.features.player.domain.repository.PlayerPositionRepository
import com.example.penasim.features.player.domain.repository.PlayerRepository
import com.example.penasim.features.team.domain.repository.TeamRepository
import com.example.penasim.features.player.domain.toTotalBattingStats
import com.example.penasim.features.player.domain.toTotalPitchingStats
import javax.inject.Inject

class TeamUseCase @Inject constructor(
  private val teamRepository: TeamRepository,
  private val playerRepository: PlayerRepository,
  private val fielderAppointmentRepository: FielderAppointmentRepository,
  private val pitcherAppointmentRepository: PitcherAppointmentRepository,
  private val playerPositionRepository: PlayerPositionRepository,
  private val battingStatRepository: BattingStatRepository,
  private val pitchingStatRepository: PitchingStatRepository
) {
  suspend fun getTeam(teamId: Int) = teamRepository.getTeam(teamId)

  suspend fun getTeamPlayers(teamId: Int): TeamPlayers {
    val team = teamRepository.getTeam(teamId)
      ?: throw IllegalArgumentException("no team for id $teamId")

    val infos = playerRepository.getPlayers(teamId).map { player ->
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

    val pitcherAppointments = pitcherAppointmentRepository.getPitcherAppointmentsByTeamId(teamId)
    val fielderAppointments = fielderAppointmentRepository.getFielderAppointmentsByTeamId(teamId)

    return TeamPlayers(
      team = team,
      players = infos,
      pitcherAppointments = pitcherAppointments,
      fielderAppointments = fielderAppointments
    )
  }
}
