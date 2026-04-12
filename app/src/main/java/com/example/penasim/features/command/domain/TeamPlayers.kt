package com.example.penasim.features.command.domain

import com.example.penasim.features.player.domain.PlayerInfo
import com.example.penasim.features.team.domain.Team

data class TeamPlayers(
  val team: Team,
  val players: List<PlayerInfo>,
  val pitcherAppointments: List<PitcherAppointment>,
  val fielderAppointments: List<FielderAppointment>
)
