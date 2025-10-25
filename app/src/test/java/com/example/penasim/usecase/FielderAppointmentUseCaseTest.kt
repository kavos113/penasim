package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.FielderAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FielderAppointmentUseCaseTest {

    private val repository: FielderAppointmentRepository = mock()
    private val useCase = FielderAppointmentUseCase(repository)

    @Test
    fun updateTeamAppointments_diffAndValidate_updatesOnlyChanged() = runTest {
        val teamId = 1
        val current = listOf(
            FielderAppointment(teamId, playerId = 10, position = Position.CENTER_FIELDER, number = 1, orderType = OrderType.NORMAL),
            FielderAppointment(teamId, playerId = 11, position = Position.SHORTSTOP, number = 2, orderType = OrderType.NORMAL),
        )
        whenever(repository.getFielderAppointmentsByTeamId(teamId)).thenReturn(current)

        val changed = listOf(
            current[0].copy(number = 1), // unchanged
            current[1].copy(position = Position.SECOND_BASEMAN), // changed
        )

        useCase.updateTeamAppointments(changed)

        val captor = argumentCaptor<List<FielderAppointment>>()
        verify(repository).updateFielderAppointments(captor.capture())
        // only one changed entry should be updated
        assert(captor.firstValue.size == 1)
        assert(captor.firstValue.first().playerId == 11)
        assert(captor.firstValue.first().position == Position.SECOND_BASEMAN)
    }

    @Test
    fun updateTeamAppointments_empty_throws() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.updateTeamAppointments(emptyList())
        }
    }

    @Test
    fun getByTeam_delegates() = runTest {
        val teamId = 7
        val expected = emptyList<FielderAppointment>()
        whenever(repository.getFielderAppointmentsByTeamId(eq(teamId))).thenReturn(expected)

        val result = useCase.getByTeam(Team(id = teamId, name = "t"))
        assertEquals(expected, result)
    }
}
