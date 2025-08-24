package com.example.penasim.usecase

import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.FielderAppointmentRepository
import javax.inject.Inject

class GetFielderAppointmentByTeamUseCase @Inject constructor(
    private val fielderAppointmentRepository: FielderAppointmentRepository
) {
    suspend fun execute(team: Team) =
        fielderAppointmentRepository.getFielderAppointmentsByTeamId(team.id)
}