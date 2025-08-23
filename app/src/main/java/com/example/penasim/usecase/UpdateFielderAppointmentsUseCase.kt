package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.repository.FielderAppointmentRepository

// give full-team appointments
class UpdateFielderAppointmentsUseCase(
    private val fielderAppointmentRepository: FielderAppointmentRepository
) {
    suspend fun execute(appointments: List<FielderAppointment>) {
        assert(appointments.isNotEmpty()) { "No appointments provided" }
        assert(appointments.map { it.teamId }.distinct().size == 1) { "Appointments must belong to the same team" }
        assert(appointments.map { it.playerId }.distinct().size == appointments.size) { "Duplicate player IDs in appointments" }

        val currentAppointments = fielderAppointmentRepository
            .getFielderAppointmentsByTeamId(appointments.firstOrNull()?.teamId
                ?: throw IllegalArgumentException("No appointments provided"))
            .associateBy { it.playerId }

        // check changed appointments
        val toUpdate = appointments.mapNotNull { newApp ->
            val currentApp = currentAppointments[newApp.playerId]
            if (currentApp == null || currentApp != newApp) {
                newApp
            } else { null }
        }

        fielderAppointmentRepository.updateFielderAppointments(toUpdate)
    }
}