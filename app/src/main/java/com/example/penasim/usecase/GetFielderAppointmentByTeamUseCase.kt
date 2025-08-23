package com.example.penasim.usecase

import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.FielderAppointmentRepository

class GetFielderAppointmentByTeamUseCase(
    private val fielderAppointmentRepository: FielderAppointmentRepository
) {
    suspend fun execute(team: Team) =
        fielderAppointmentRepository.getFielderAppointmentsByTeamId(team.id)
}