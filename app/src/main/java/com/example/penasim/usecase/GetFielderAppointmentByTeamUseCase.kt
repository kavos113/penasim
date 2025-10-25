package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.Team
import javax.inject.Inject

class GetFielderAppointmentByTeamUseCase @Inject constructor(
    private val fielderAppointmentUseCase: FielderAppointmentUseCase
) {
    suspend fun execute(team: Team): List<FielderAppointment> =
        fielderAppointmentUseCase.getByTeam(team)
}