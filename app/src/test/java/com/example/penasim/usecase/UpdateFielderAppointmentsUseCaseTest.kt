package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.Position
import com.example.penasim.domain.repository.FielderAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class UpdateFielderAppointmentsUseCaseTest {

    private class RecordingFielderAppointmentRepository(
        private val current: List<FielderAppointment>
    ) : FielderAppointmentRepository {
        var lastUpdated: List<FielderAppointment>? = null

        override suspend fun getFielderAppointmentsByTeamId(teamId: Int): List<FielderAppointment> = current.filter { it.teamId == teamId }
        override suspend fun getFielderAppointmentByPlayerId(playerId: Int): FielderAppointment? = current.find { it.playerId == playerId }
        override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) { lastUpdated = fielderAppointments }
    }

    private fun app(team: Int, player: Int, pos: Position, isMain: Boolean, num: Int) =
        FielderAppointment(teamId = team, playerId = player, position = pos, isMain = isMain, number = num)

    @Test
    fun execute_updatesOnlyChangedOrNewAppointments() = runTest {
        val current = listOf(
            app(1, 10, Position.CATCHER, true, 4),
            app(1, 11, Position.OUTFIELDER, false, 7)
        )
        val repo = RecordingFielderAppointmentRepository(current)
        val useCase = UpdateFielderAppointmentsUseCase(repo)

        // Case 1: no change -> empty update
        useCase.execute(current)
        assertEquals(emptyList<FielderAppointment>(), repo.lastUpdated)

        // Case 2: one changed, one same, and one new -> update includes changed + new
        val changed = app(1, 10, Position.CATCHER, false, 4) // isMain changed
        val same = current[1]
        val newApp = app(1, 12, Position.FIRST_BASEMAN, true, 8)
        repo.lastUpdated = null
        useCase.execute(listOf(changed, same, newApp))
        assertEquals(listOf(changed, newApp), repo.lastUpdated)
    }

    @Test
    fun execute_assertsOnInvalidInputs() = runTest {
        val repo = RecordingFielderAppointmentRepository(emptyList())
        val useCase = UpdateFielderAppointmentsUseCase(repo)

        // empty
        assertFailsWith<AssertionError> { useCase.execute(emptyList()) }

        // multi-team
        val a = app(1, 10, Position.CATCHER, true, 4)
        val b = app(2, 11, Position.OUTFIELDER, false, 7)
        assertFailsWith<AssertionError> { useCase.execute(listOf(a, b)) }

        // duplicate player ids
        val c = app(1, 10, Position.CATCHER, true, 4)
        val d = app(1, 10, Position.FIRST_BASEMAN, false, 7)
        assertFailsWith<AssertionError> { useCase.execute(listOf(c, d)) }
    }
}
