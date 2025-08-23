package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.FielderAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetFielderAppointmentByTeamUseCaseTest {

    private class FakeFielderAppointmentRepository(
        private val data: Map<Int, List<FielderAppointment>>
    ) : FielderAppointmentRepository {
        override suspend fun getFielderAppointmentsByTeamId(teamId: Int): List<FielderAppointment> =
            data[teamId] ?: emptyList()

        override suspend fun getFielderAppointmentByPlayerId(playerId: Int): FielderAppointment? = null
        override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
    }

    @Test
    fun execute_returnsAppointmentsForTeam() = runTest {
        val team = Team(1, "A", com.example.penasim.domain.League.L1)
        val apps = listOf(
            FielderAppointment(teamId = 1, playerId = 10, position = Position.CATCHER, isMain = true, number = 4),
            FielderAppointment(teamId = 1, playerId = 11, position = Position.OUTFIELDER, isMain = false, number = 7)
        )
        val repo = FakeFielderAppointmentRepository(mapOf(1 to apps))
        val useCase = GetFielderAppointmentByTeamUseCase(repo)

        val result = useCase.execute(team)

        assertEquals(apps, result)
    }
}
