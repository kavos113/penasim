package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.Team
import javax.inject.Inject

class GetPitcherAppointmentByTeamUseCase @Inject constructor(
    private val pitcherAppointmentUseCase: PitcherAppointmentUseCase
) {
    suspend fun execute(team: Team): List<PitcherAppointment> =
        pitcherAppointmentUseCase.getByTeam(team)
}