package com.example.penasim.usecase

import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.TeamPlayers
import com.example.penasim.domain.repository.BattingStatRepository
import com.example.penasim.domain.repository.FielderAppointmentRepository
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import com.example.penasim.domain.repository.PitchingStatRepository
import com.example.penasim.domain.repository.PlayerPositionRepository
import com.example.penasim.domain.repository.PlayerRepository
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.domain.toTotalBattingStats
import com.example.penasim.domain.toTotalPitchingStats
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