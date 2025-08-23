package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class UpdatePitcherAppointmentsUseCaseTest {

    private class RecordingPitcherAppointmentRepository(
        private val current: List<PitcherAppointment>
    ) : PitcherAppointmentRepository {
        var lastUpdated: List<PitcherAppointment>? = null

        override suspend fun getPitcherAppointmentsByTeamId(teamId: Int): List<PitcherAppointment> = current.filter { it.teamId == teamId }
        override suspend fun getPitcherAppointmentByPlayerId(playerId: Int): PitcherAppointment? = current.find { it.playerId == playerId }
        override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {}
        override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {}
        override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) { lastUpdated = pitcherAppointments }
    }

    private fun app(team: Int, player: Int, type: PitcherType, isMain: Boolean, num: Int) =
        com.example.penasim.domain.PitcherAppointment(teamId = team, playerId = player, isMain = isMain, type = type, number = num)

    @Test
    fun execute_updatesOnlyChangedOrNewAppointments() = runTest {
        val current = listOf(
            app(1, 10, PitcherType.STARTER, true, 1),
            app(1, 11, PitcherType.CLOSER, false, 2)
        )
        val repo = RecordingPitcherAppointmentRepository(current)
        val useCase = UpdatePitcherAppointmentsUseCase(repo)

        // Case 1: no change -> empty update
        useCase.execute(current)
        assertEquals(emptyList<PitcherAppointment>(), repo.lastUpdated)

        // Case 2: one changed, one same, and one new -> update includes changed + new
        val changed = app(1, 10, PitcherType.RELIEVER, true, 1) // type changed
        val same = current[1]
        val newApp = app(1, 12, PitcherType.STARTER, true, 3)
        repo.lastUpdated = null
        useCase.execute(listOf(changed, same, newApp))
        assertEquals(listOf(changed, newApp), repo.lastUpdated)
    }

    @Test
    fun execute_assertsOnInvalidInputs() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = UpdatePitcherAppointmentsUseCase(repo)

        // empty
        assertFailsWith<AssertionError> { useCase.execute(emptyList()) }

        // multi-team
        val a = app(1, 10, PitcherType.STARTER, true, 1)
        val b = app(2, 11, PitcherType.CLOSER, false, 2)
        assertFailsWith<AssertionError> { useCase.execute(listOf(a, b)) }

        // duplicate player ids
        val c = app(1, 10, PitcherType.STARTER, true, 1)
        val d = app(1, 10, PitcherType.RELIEVER, false, 2)
        assertFailsWith<AssertionError> { useCase.execute(listOf(c, d)) }
    }
}
