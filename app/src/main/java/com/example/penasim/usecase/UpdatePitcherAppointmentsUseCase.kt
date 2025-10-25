package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import javax.inject.Inject

class UpdatePitcherAppointmentsUseCase @Inject constructor(
    private val pitcherAppointmentUseCase: PitcherAppointmentUseCase
) {
    suspend fun execute(appointments: List<PitcherAppointment>) {
        pitcherAppointmentUseCase.updateTeamAppointments(appointments)
    }
}