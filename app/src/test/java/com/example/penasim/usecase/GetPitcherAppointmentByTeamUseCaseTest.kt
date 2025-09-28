package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPitcherAppointmentByTeamUseCaseTest {

    private class FakePitcherAppointmentRepository(
        private val data: Map<Int, List<PitcherAppointment>>
    ) : PitcherAppointmentRepository {
        override suspend fun getPitcherAppointmentsByTeamId(teamId: Int): List<PitcherAppointment> =
            data[teamId] ?: emptyList()

        override suspend fun getPitcherAppointmentByPlayerId(playerId: Int): PitcherAppointment? = null
        override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
    }

    @Test
    fun execute_returnsAppointmentsForTeam() = runTest {
        val team = Team(2, "B", com.example.penasim.domain.League.L2)
        val apps = listOf(
            PitcherAppointment(teamId = 2, playerId = 20, type = PitcherType.STARTER, number = 1),
            PitcherAppointment(teamId = 2, playerId = 21, type = PitcherType.CLOSER, number = 2)
        )
        val repo = FakePitcherAppointmentRepository(mapOf(2 to apps))
        val useCase = GetPitcherAppointmentByTeamUseCase(repo)

        val result = useCase.execute(team)

        assertEquals(apps, result)
    }
}
