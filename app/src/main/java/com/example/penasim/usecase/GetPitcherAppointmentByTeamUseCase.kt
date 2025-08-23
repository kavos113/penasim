package com.example.penasim.usecase

import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.PitcherAppointmentRepository

class GetPitcherAppointmentByTeamUseCase(
    private val pitcherAppointmentRepository: PitcherAppointmentRepository
) {
    suspend fun execute(team: Team) =
        pitcherAppointmentRepository.getPitcherAppointmentsByTeamId(team.id)
}