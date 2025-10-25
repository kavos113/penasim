package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.FielderAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class FielderAppointmentUseCaseTest {

    private class RecordingFielderAppointmentRepository(
        private val current: List<FielderAppointment>
    ) : FielderAppointmentRepository {
        var lastUpdated: List<FielderAppointment>? = null

        override suspend fun getFielderAppointmentsByTeamId(teamId: Int): List<FielderAppointment> =
            current.filter { it.teamId == teamId }

        override suspend fun getFielderAppointmentByPlayerId(playerId: Int): FielderAppointment? =
            current.find { it.playerId == playerId }

        override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {}
        override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {}
        override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {
            // Record the last update call so assertions can be made in tests
            lastUpdated = fielderAppointments
        }
    }

    private fun app(teamId: Int, playerId: Int, position: Position, number: Int, orderType: OrderType) =
        FielderAppointment(teamId = teamId, playerId = playerId, position = position, number = number, orderType = orderType)

    @Test
    fun updateTeamAppointments_diffAndValidate_updatesOnlyChanged() = runTest {
        val teamId = 1
        val current = listOf(
            app(teamId, playerId = 10, position = Position.CENTER_FIELDER, number = 1, orderType = OrderType.NORMAL),
            app(teamId, playerId = 11, position = Position.SHORTSTOP, number = 2, orderType = OrderType.NORMAL),
        )
        val repository = RecordingFielderAppointmentRepository(current)
        val useCase = FielderAppointmentUseCase(repository)

        val changed = listOf(
            current[0].copy(number = 1), // unchanged
            current[1].copy(position = Position.SECOND_BASEMAN), // changed
        )

        useCase.updateOnlyDiff(changed)

        // only one changed entry should be updated
        val updated = repository.lastUpdated
        requireNotNull(updated)
        assertEquals(1, updated.size)
        assertEquals(11, updated.first().playerId)
        assertEquals(Position.SECOND_BASEMAN, updated.first().position)
    }

    @Test
    fun updateTeamAppointments_empty_throws() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(emptyList())
        }
    }

    @Test
    fun getByTeam_delegates_empty() = runTest {
        val teamId = 7
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        val result = useCase.getByTeam(Team(id = teamId, name = "t"))
        assertEquals(emptyList<FielderAppointment>(), result)
    }

    @Test
    fun getByTeam_returnsAppointmentsForTeam() = runTest {
        val team = Team(1, "A")
        val apps = listOf(
            app(teamId = 1, playerId = 10, position = Position.CATCHER, number = 4, orderType = OrderType.NORMAL),
            app(teamId = 1, playerId = 11, position = Position.OUTFIELDER, number = 7, orderType = OrderType.LEFT)
        )
        val repository = RecordingFielderAppointmentRepository(apps)
        val useCase = FielderAppointmentUseCase(repository)

        val result = useCase.getByTeam(team)

        assertEquals(apps, result)
    }
}
