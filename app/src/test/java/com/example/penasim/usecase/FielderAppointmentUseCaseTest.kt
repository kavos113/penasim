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
import kotlin.test.assertFailsWith

class FielderAppointmentUseCaseTest {

    private class RecordingFielderAppointmentRepository(
        current: List<FielderAppointment>
    ) : FielderAppointmentRepository {
        private val current: MutableList<FielderAppointment> = current.toMutableList()
        var lastUpdated: List<FielderAppointment>? = null
        var lastInsertedOne: FielderAppointment? = null
        var lastInsertedMany: List<FielderAppointment>? = null
        var lastDeletedOne: FielderAppointment? = null
        var lastDeletedMany: List<FielderAppointment>? = null
        var lastUpdatedOne: FielderAppointment? = null

        override suspend fun getFielderAppointmentsByTeamId(teamId: Int): List<FielderAppointment> =
            current.filter { it.teamId == teamId }

        override suspend fun getFielderAppointmentByPlayerId(playerId: Int): FielderAppointment? =
            current.find { it.playerId == playerId }

        override suspend fun insertFielderAppointment(fielderAppointment: FielderAppointment) {
            lastInsertedOne = fielderAppointment
        }
        override suspend fun insertFielderAppointments(fielderAppointments: List<FielderAppointment>) {
            lastInsertedMany = fielderAppointments
        }
        override suspend fun deleteFielderAppointment(fielderAppointment: FielderAppointment) {
            lastDeletedOne = fielderAppointment
        }
        override suspend fun deleteFielderAppointments(fielderAppointments: List<FielderAppointment>) {
            lastDeletedMany = fielderAppointments
        }
        override suspend fun updateFielderAppointment(fielderAppointment: FielderAppointment) {
            lastUpdatedOne = fielderAppointment
        }
        override suspend fun updateFielderAppointments(fielderAppointments: List<FielderAppointment>) {
            lastUpdated = fielderAppointments
        }
    }

    private fun app(teamId: Int, playerId: Int, position: Position, number: Int, orderType: OrderType) =
        FielderAppointment(teamId = teamId, playerId = playerId, position = position, number = number, orderType = orderType)

    // --- getByTeam ---

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

    // --- getByPlayerId ---

    @Test
    fun getByPlayerId_returnsAppointment_whenExists() = runTest {
        val expected = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)
        val repository = RecordingFielderAppointmentRepository(listOf(expected))
        val useCase = FielderAppointmentUseCase(repository)

        val result = useCase.getByPlayerId(10)
        assertEquals(expected, result)
    }

    @Test
    fun getByPlayerId_returnsNull_whenNotExists() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        assertNull(useCase.getByPlayerId(999))
    }

    // --- insertOne / insertMany ---

    @Test
    fun insertOne_delegatesToRepository() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)
        val item = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)

        useCase.insertOne(item)

        assertEquals(item, repository.lastInsertedOne)
    }

    @Test
    fun insertMany_doesNotCallRepository_whenEmpty() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        useCase.insertMany(emptyList())

        assertNull(repository.lastInsertedMany)
    }

    @Test
    fun insertMany_delegatesToRepository_whenNonEmpty() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)
        val items = listOf(
            app(1, 10, Position.CATCHER, 4, OrderType.NORMAL),
            app(1, 11, Position.SHORTSTOP, 5, OrderType.NORMAL)
        )

        useCase.insertMany(items)

        assertEquals(items, repository.lastInsertedMany)
    }

    // --- deleteOne / deleteMany ---

    @Test
    fun deleteOne_delegatesToRepository() = runTest {
        val item = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)
        val repository = RecordingFielderAppointmentRepository(listOf(item))
        val useCase = FielderAppointmentUseCase(repository)

        useCase.deleteOne(item)

        assertEquals(item, repository.lastDeletedOne)
    }

    @Test
    fun deleteMany_doesNotCallRepository_whenEmpty() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        useCase.deleteMany(emptyList())

        assertNull(repository.lastDeletedMany)
    }

    @Test
    fun deleteMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(
            app(1, 10, Position.CATCHER, 4, OrderType.NORMAL),
            app(1, 11, Position.SHORTSTOP, 5, OrderType.NORMAL)
        )
        val repository = RecordingFielderAppointmentRepository(items)
        val useCase = FielderAppointmentUseCase(repository)

        useCase.deleteMany(items)

        assertEquals(items, repository.lastDeletedMany)
    }

    // --- updateOne / updateMany ---

    @Test
    fun updateOne_delegatesToRepository() = runTest {
        val item = app(1, 10, Position.CATCHER, 4, OrderType.NORMAL)
        val repository = RecordingFielderAppointmentRepository(listOf(item))
        val useCase = FielderAppointmentUseCase(repository)

        useCase.updateOne(item)

        assertEquals(item, repository.lastUpdatedOne)
    }

    @Test
    fun updateMany_doesNotCallRepository_whenEmpty() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        useCase.updateMany(emptyList())

        assertNull(repository.lastUpdated)
    }

    @Test
    fun updateMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(
            app(1, 10, Position.CATCHER, 4, OrderType.NORMAL),
            app(1, 11, Position.SHORTSTOP, 5, OrderType.NORMAL)
        )
        val repository = RecordingFielderAppointmentRepository(items)
        val useCase = FielderAppointmentUseCase(repository)

        useCase.updateMany(items)

        assertEquals(items, repository.lastUpdated)
    }

    // --- updateOnlyDiff ---

    @Test
    fun updateOnlyDiff_updatesOnlyChanged() = runTest {
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

        val updated = repository.lastUpdated
        requireNotNull(updated)
        assertEquals(1, updated.size)
        assertEquals(11, updated.first().playerId)
        assertEquals(Position.SECOND_BASEMAN, updated.first().position)
    }

    @Test
    fun updateOnlyDiff_empty_throws() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(emptyList())
        }
    }

    @Test
    fun updateOnlyDiff_differentTeams_throws() = runTest {
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

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
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

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
        val repository = RecordingFielderAppointmentRepository(current)
        val useCase = FielderAppointmentUseCase(repository)

        useCase.updateOnlyDiff(current)

        assertNull(repository.lastUpdated)
    }

    @Test
    fun updateOnlyDiff_newPlayers_updatesAll() = runTest {
        val teamId = 1
        val repository = RecordingFielderAppointmentRepository(emptyList())
        val useCase = FielderAppointmentUseCase(repository)

        val newApps = listOf(
            app(teamId, 10, Position.CENTER_FIELDER, 1, OrderType.NORMAL),
            app(teamId, 11, Position.SHORTSTOP, 2, OrderType.NORMAL),
        )

        useCase.updateOnlyDiff(newApps)

        assertEquals(newApps, repository.lastUpdated)
    }
}
