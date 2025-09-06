package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.repository.FielderAppointmentRepository
import javax.inject.Inject

// give full-team appointments
class UpdateFielderAppointmentsUseCase @Inject constructor(
    private val fielderAppointmentRepository: FielderAppointmentRepository
) {
    suspend fun execute(appointments: List<FielderAppointment>) {
        assert(appointments.isNotEmpty()) { "No appointments provided" }
        assert(appointments.map { it.teamId }.distinct().size == 1) { "Appointments must belong to the same team" }
        assert(appointments.map { it.playerId to it.orderType }.distinct().size == appointments.size) { "Duplicate player ID & order type in appointments" }

        val teamId = appointments.firstOrNull()?.teamId
            ?: throw IllegalArgumentException("No appointments provided")

        val currentAppointments = fielderAppointmentRepository
            .getFielderAppointmentsByTeamId(teamId)
            .associateBy { it.playerId to it.orderType }

        // check changed appointments
        val toUpdate = appointments.mapNotNull { newApp ->
            val currentApp = currentAppointments[newApp.playerId to newApp.orderType]
            if (currentApp == null || currentApp != newApp) {
                newApp
            } else { null }
        }

        fielderAppointmentRepository.updateFielderAppointments(toUpdate)
    }
}