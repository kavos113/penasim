package com.example.penasim.usecase

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.FielderAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

class FielderAppointmentUseCaseTest {

    private val repo: FielderAppointmentRepository = mock()
    private val useCase = FielderAppointmentUseCase(repo)

    private fun app(teamId: Int, playerId: Int, position: Position, number: Int, orderType: OrderType) =
        FielderAppointment(teamId = teamId, playerId = playerId, position = position, number = number, orderType = orderType)

    // --- getByTeam ---

    @Test
    fun getByTeam_delegates_empty() = runTest {
        val teamId = 7
        whenever(repo.getFielderAppointmentsByTeamId(teamId)).thenReturn(emptyList())

        val result = useCase.getByTeam(Team(id = teamId, name = "t"))

        assertEquals(emptyList<FielderAppointment>(), result)
        verify(repo).getFielderAppointmentsByTeamId(teamId)
    }

    @Test
    fun getByTeam_returnsAppointmentsForTeam() = runTest {
        val team = Team(1, "A")
        val apps = listOf(
            app(teamId = 1, playerId = 10, position = Position.CATCHER, number = 4, orderType = OrderType.NORMAL),
            app(teamId = 1, playerId = 11, position = Position.OUTFIELDER, number = 7, orderType = OrderType.LEFT)
        )
        whenever(repo.getFielderAppointmentsByTeamId(1)).thenReturn(apps)

        val result = useCase.getByTeam(team)

        assertEquals(apps, result)
        verify(repo).getFielderAppointmentsByTeamId(1)
    }

    // --- getByPlayerId ---

    @Test
    fun getByPlayerId_returnsAppointment_whenExists() = runTest {
        val expected = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)
        whenever(repo.getFielderAppointmentByPlayerId(10)).thenReturn(expected)

        val result = useCase.getByPlayerId(10)

        assertEquals(expected, result)
        verify(repo).getFielderAppointmentByPlayerId(10)
    }

    @Test
    fun getByPlayerId_returnsNull_whenNotExists() = runTest {
        whenever(repo.getFielderAppointmentByPlayerId(999)).thenReturn(null)

        assertNull(useCase.getByPlayerId(999))
        verify(repo).getFielderAppointmentByPlayerId(999)
    }

    // --- insertOne / insertMany ---

    @Test
    fun insertOne_delegatesToRepository() = runTest {
        val item = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)

        useCase.insertOne(item)

        verify(repo).insertFielderAppointment(item)
    }

    @Test
    fun insertMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.insertMany(emptyList())

        verify(repo, never()).insertFielderAppointments(any())
    }

    @Test
    fun insertMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(
            app(1, 10, Position.CATCHER, 4, OrderType.NORMAL),
            app(1, 11, Position.SHORTSTOP, 5, OrderType.NORMAL)
        )

        useCase.insertMany(items)

        verify(repo).insertFielderAppointments(items)
    }

    // --- deleteOne / deleteMany ---

    @Test
    fun deleteOne_delegatesToRepository() = runTest {
        val item = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)

        useCase.deleteOne(item)

        verify(repo).deleteFielderAppointment(item)
    }

    @Test
    fun deleteMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.deleteMany(emptyList())

        verify(repo, never()).deleteFielderAppointments(any())
    }

    @Test
    fun deleteMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(
            app(1, 10, Position.CATCHER, 4, OrderType.NORMAL),
            app(1, 11, Position.SHORTSTOP, 5, OrderType.NORMAL)
        )

        useCase.deleteMany(items)

        verify(repo).deleteFielderAppointments(items)
    }

    // --- updateOne / updateMany ---

    @Test
    fun updateOne_delegatesToRepository() = runTest {
        val item = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)

        useCase.updateOne(item)

        verify(repo).updateFielderAppointment(item)
    }

    @Test
    fun updateMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.updateMany(emptyList())

        verify(repo, never()).updateFielderAppointments(any())
    }

    @Test
    fun updateMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(
            app(1, 10, Position.CATCHER, 4, OrderType.NORMAL),
            app(1, 11, Position.SHORTSTOP, 5, OrderType.NORMAL)
        )

        useCase.updateMany(items)

        verify(repo).updateFielderAppointments(items)
    }

    // --- updateOnlyDiff ---

    @Test
    fun updateOnlyDiff_updatesOnlyChanged() = runTest {
        val teamId = 1
        val current = listOf(
            app(teamId, playerId = 10, position = Position.CENTER_FIELDER, number = 1, orderType = OrderType.NORMAL),
            app(teamId, playerId = 11, position = Position.SHORTSTOP, number = 2, orderType = OrderType.NORMAL),
        )
        whenever(repo.getFielderAppointmentsByTeamId(teamId)).thenReturn(current)

        val newApps = listOf(
            current[0].copy(number = 1), // unchanged
            current[1].copy(position = Position.SECOND_BASEMAN), // changed
            app(teamId, playerId = 12, position = Position.CATCHER, number = 3, orderType = OrderType.NORMAL), // new
        )

        useCase.updateOnlyDiff(newApps)

        val expected = listOf(
            current[1].copy(position = Position.SECOND_BASEMAN),
            app(teamId, playerId = 12, position = Position.CATCHER, number = 3, orderType = OrderType.NORMAL),
        )
        verify(repo).updateFielderAppointments(expected)
    }

    @Test
    fun updateOnlyDiff_empty_throws() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(emptyList())
        }
    }

    @Test
    fun updateOnlyDiff_differentTeams_throws() = runTest {
        val appointments = listOf(
            app(1, 10, Position.CATCHER, 1, OrderType.NORMAL),
            app(2, 11, Position.SHORTSTOP, 2, OrderType.NORMAL),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(appointments)
        }
    }

    @Test
    fun updateOnlyDiff_duplicatePlayerAndOrderType_throws() = runTest {
        val appointments = listOf(
            app(1, 10, Position.CATCHER, 1, OrderType.NORMAL),
            app(1, 10, Position.SHORTSTOP, 2, OrderType.NORMAL),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(appointments)
        }
    }

    @Test
    fun updateOnlyDiff_noChanges_doesNotCallUpdate() = runTest {
        val teamId = 1
        val current = listOf(
            app(teamId, 10, Position.CENTER_FIELDER, 1, OrderType.NORMAL),
            app(teamId, 11, Position.SHORTSTOP, 2, OrderType.NORMAL),
        )
        whenever(repo.getFielderAppointmentsByTeamId(teamId)).thenReturn(current)

        useCase.updateOnlyDiff(current)

        verify(repo, never()).updateFielderAppointments(any())
    }

    @Test
    fun updateOnlyDiff_newPlayers_updatesAll() = runTest {
        val teamId = 1
        whenever(repo.getFielderAppointmentsByTeamId(teamId)).thenReturn(emptyList())

        val newApps = listOf(
            app(teamId, 10, Position.CENTER_FIELDER, 1, OrderType.NORMAL),
            app(teamId, 11, Position.SHORTSTOP, 2, OrderType.NORMAL),
        )

        useCase.updateOnlyDiff(newApps)

        verify(repo).updateFielderAppointments(newApps)
    }
}
