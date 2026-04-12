package com.example.penasim.domain.repository

import com.example.penasim.domain.FielderAppointment

interface FielderAppointmentRepository {
  suspend fun getFielderAppointmentsByTeamId(teamId: Int): List<FielderAppointment>
  suspend fun getFielderAppointmentByPlayerId(playerId: Int): FielderAppointment?

  suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment)
  suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>)

  suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment)
  suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>)

  suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment)
  suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>)
}