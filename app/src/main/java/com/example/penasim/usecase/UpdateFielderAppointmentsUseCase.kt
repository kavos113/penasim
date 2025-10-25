package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import javax.inject.Inject

// give full-team appointments
class UpdateFielderAppointmentsUseCase @Inject constructor(
    private val fielderAppointmentUseCase: FielderAppointmentUseCase
) {
    suspend fun execute(appointments: List<FielderAppointment>) {
        fielderAppointmentUseCase.updateTeamAppointments(appointments)
    }
}