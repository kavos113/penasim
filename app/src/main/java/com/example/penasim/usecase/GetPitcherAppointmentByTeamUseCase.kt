package com.example.penasim.usecase

import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import javax.inject.Inject

class GetPitcherAppointmentByTeamUseCase @Inject constructor(
    private val pitcherAppointmentRepository: PitcherAppointmentRepository
) {
    suspend fun execute(team: Team) =
        pitcherAppointmentRepository.getPitcherAppointmentsByTeamId(team.id)
}