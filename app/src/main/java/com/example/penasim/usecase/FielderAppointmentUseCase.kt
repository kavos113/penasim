package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.FielderAppointmentRepository
import javax.inject.Inject

class FielderAppointmentUseCase @Inject constructor(
  private val repository: FielderAppointmentRepository
) {
  suspend fun getByTeam(team: Team): List<FielderAppointment> =
    repository.getFielderAppointmentsByTeamId(team.id)

  suspend fun getByPlayerId(playerId: Int): FielderAppointment? =
    repository.getFielderAppointmentByPlayerId(playerId)

  suspend fun insertOne(item: FielderAppointment) = repository.insertFielderAppointment(item)
  suspend fun insertMany(items: List<FielderAppointment>) {
    if (items.isEmpty()) return
    repository.insertFielderAppointments(items)
  }

  suspend fun deleteOne(item: FielderAppointment) = repository.deleteFielderAppointment(item)
  suspend fun deleteMany(items: List<FielderAppointment>) {
    if (items.isEmpty()) return
    repository.deleteFielderAppointments(items)
  }

  suspend fun updateOne(item: FielderAppointment) = repository.updateFielderAppointment(item)
  suspend fun updateMany(items: List<FielderAppointment>) {
    if (items.isEmpty()) return
    repository.updateFielderAppointments(items)
  }

  // Full-team appointments update with validation and diffing
  suspend fun updateOnlyDiff(appointments: List<FielderAppointment>) {
    require(appointments.isNotEmpty()) { "No appointments provided" }
    require(appointments.map { it.teamId }
      .distinct().size == 1) { "Appointments must belong to the same team" }
    require(appointments.map { it.playerId to it.orderType }
      .distinct().size == appointments.size) { "Duplicate player ID & order type in appointments" }

    val teamId = appointments.first().teamId
    val currentAppointments = repository
      .getFielderAppointmentsByTeamId(teamId)
      .associateBy { it.playerId to it.orderType }

    val toUpdate = appointments.mapNotNull { newApp ->
      val currentApp = currentAppointments[newApp.playerId to newApp.orderType]
      if (currentApp == null || currentApp != newApp) newApp else null
    }
    if (toUpdate.isEmpty()) return
    repository.updateFielderAppointments(toUpdate)
  }
}
