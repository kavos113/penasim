package com.example.penasim.features.command.domain

data class TeamPlayers(
  val team: Team,
  val players: List<PlayerInfo>,
  val pitcherAppointments: List<PitcherAppointment>,
  val fielderAppointments: List<FielderAppointment>
)
