package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class PitcherAppointmentUseCaseTest {

    private class RecordingPitcherAppointmentRepository(
        private val current: List<PitcherAppointment>
    ) : PitcherAppointmentRepository {
        var lastUpdated: List<PitcherAppointment>? = null

        override suspend fun getPitcherAppointmentsByTeamId(teamId: Int): List<PitcherAppointment> =
            current.filter { it.teamId == teamId }

        override suspend fun getPitcherAppointmentByPlayerId(playerId: Int): PitcherAppointment? =
            current.find { it.playerId == playerId }

        override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {
            lastUpdated = pitcherAppointments
        }
    }

    private fun app(teamId: Int, playerId: Int, type: PitcherType, number: Int) =
        PitcherAppointment(teamId = teamId, playerId = playerId, type = type, number = number)

    @Test
    fun updateTeamAppointments_diffAndValidate_updatesOnlyChanged() = runTest {
        val teamId = 2
        val current = listOf(
            app(teamId, 20, PitcherType.STARTER, 1),
            app(teamId, 21, PitcherType.CLOSER, 2),
        )
        val repo = RecordingPitcherAppointmentRepository(current)
        val useCase = PitcherAppointmentUseCase(repo)

        val changed = listOf(
            current[0].copy(type = PitcherType.STARTER), // unchanged
            current[1].copy(type = PitcherType.RELIEVER), // changed
            app(teamId, 22, PitcherType.STARTER, 3), // new
        )

        useCase.updateOnlyDiff(changed)

        val updated = repo.lastUpdated
        requireNotNull(updated)
        assertEquals(listOf(changed[1], changed[2]), updated)
    }

    @Test
    fun updateTeamAppointments_empty_throws() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(emptyList())
        }
    }

    @Test
    fun getByTeam_delegates_empty() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        val result = useCase.getByTeam(Team(id = 2, name = "B"))
        assertEquals(emptyList<PitcherAppointment>(), result)
    }

    @Test
    fun getByTeam_returnsAppointmentsForTeam() = runTest {
        val team = Team(2, "B")
        val apps = listOf(
            app(2, 20, PitcherType.STARTER, 1),
            app(2, 21, PitcherType.CLOSER, 2)
        )
        val repo = RecordingPitcherAppointmentRepository(apps)
        val useCase = PitcherAppointmentUseCase(repo)

        val result = useCase.getByTeam(team)

        assertEquals(apps, result)
    }
}

