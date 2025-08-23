package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.repository.PitcherAppointmentRepository

class UpdatePitcherAppointmentsUseCase(
    private val pitcherAppointmentRepository: PitcherAppointmentRepository
) {
    suspend fun execute(appointments: List<PitcherAppointment>) {
        assert(appointments.isNotEmpty()) { "No appointments provided" }
        assert(appointments.map { it.teamId }.distinct().size == 1) { "Appointments must belong to the same team" }
        assert(appointments.map { it.playerId }.distinct().size == appointments.size) { "Duplicate player IDs in appointments" }

        val currentAppointments = pitcherAppointmentRepository
            .getPitcherAppointmentsByTeamId(appointments.firstOrNull()?.teamId
                ?: throw IllegalArgumentException("No appointments provided"))
            .associateBy { it.playerId }

        // check changed appointments
        val toUpdate = appointments.mapNotNull { newApp ->
            val currentApp = currentAppointments[newApp.playerId]
            if (currentApp == null || currentApp != newApp) {
                newApp
            } else { null }
        }

        pitcherAppointmentRepository.updatePitcherAppointments(toUpdate)
    }
}