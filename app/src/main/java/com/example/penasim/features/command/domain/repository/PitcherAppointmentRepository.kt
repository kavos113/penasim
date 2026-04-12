package com.example.penasim.features.command.domain.repository

import com.example.penasim.features.command.domain.PitcherAppointment

interface PitcherAppointmentRepository {
  suspend fun getPitcherAppointmentsByTeamId(teamId: Int): List<PitcherAppointment>
  suspend fun getPitcherAppointmentByPlayerId(playerId: Int): PitcherAppointment?

  suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment)
  suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>)

  suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment)
  suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>)

  suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment)
  suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>)
}