package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.PitcherAppointmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.test.assertFailsWith

class PitcherAppointmentUseCaseTest {

    private class RecordingPitcherAppointmentRepository(
        private val current: List<PitcherAppointment>
    ) : PitcherAppointmentRepository {
        var lastUpdated: List<PitcherAppointment>? = null
        var lastInsertedOne: PitcherAppointment? = null
        var lastInsertedMany: List<PitcherAppointment>? = null
        var lastDeletedOne: PitcherAppointment? = null
        var lastDeletedMany: List<PitcherAppointment>? = null
        var lastUpdatedOne: PitcherAppointment? = null

        override suspend fun getPitcherAppointmentsByTeamId(teamId: Int): List<PitcherAppointment> =
            current.filter { it.teamId == teamId }

        override suspend fun getPitcherAppointmentByPlayerId(playerId: Int): PitcherAppointment? =
            current.find { it.playerId == playerId }

        override suspend fun insertPitcherAppointment(pitcherAppointment: PitcherAppointment) {
            lastInsertedOne = pitcherAppointment
        }
        override suspend fun insertPitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {
            lastInsertedMany = pitcherAppointments
        }
        override suspend fun deletePitcherAppointment(pitcherAppointment: PitcherAppointment) {
            lastDeletedOne = pitcherAppointment
        }
        override suspend fun deletePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {
            lastDeletedMany = pitcherAppointments
        }
        override suspend fun updatePitcherAppointment(pitcherAppointment: PitcherAppointment) {
            lastUpdatedOne = pitcherAppointment
        }
        override suspend fun updatePitcherAppointments(pitcherAppointments: List<PitcherAppointment>) {
            lastUpdated = pitcherAppointments
        }
    }

    private fun app(teamId: Int, playerId: Int, type: PitcherType, number: Int) =
        PitcherAppointment(teamId = teamId, playerId = playerId, type = type, number = number)

    // --- getByTeam ---

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

    // --- getByPlayerId ---

    @Test
    fun getByPlayerId_returnsAppointment_whenExists() = runTest {
        val expected = app(2, 20, PitcherType.STARTER, 1)
        val repo = RecordingPitcherAppointmentRepository(listOf(expected))
        val useCase = PitcherAppointmentUseCase(repo)

        val result = useCase.getByPlayerId(20)
        assertEquals(expected, result)
    }

    @Test
    fun getByPlayerId_returnsNull_whenNotExists() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        assertNull(useCase.getByPlayerId(999))
    }

    // --- insertOne / insertMany ---

    @Test
    fun insertOne_delegatesToRepository() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)
        val item = app(2, 20, PitcherType.STARTER, 1)

        useCase.insertOne(item)

        assertEquals(item, repo.lastInsertedOne)
    }

    @Test
    fun insertMany_doesNotCallRepository_whenEmpty() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.insertMany(emptyList())

        assertNull(repo.lastInsertedMany)
    }

    @Test
    fun insertMany_delegatesToRepository_whenNonEmpty() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)
        val items = listOf(app(2, 20, PitcherType.STARTER, 1), app(2, 21, PitcherType.CLOSER, 2))

        useCase.insertMany(items)

        assertEquals(items, repo.lastInsertedMany)
    }

    // --- deleteOne / deleteMany ---

    @Test
    fun deleteOne_delegatesToRepository() = runTest {
        val item = app(2, 20, PitcherType.STARTER, 1)
        val repo = RecordingPitcherAppointmentRepository(listOf(item))
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.deleteOne(item)

        assertEquals(item, repo.lastDeletedOne)
    }

    @Test
    fun deleteMany_doesNotCallRepository_whenEmpty() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.deleteMany(emptyList())

        assertNull(repo.lastDeletedMany)
    }

    @Test
    fun deleteMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(app(2, 20, PitcherType.STARTER, 1), app(2, 21, PitcherType.CLOSER, 2))
        val repo = RecordingPitcherAppointmentRepository(items)
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.deleteMany(items)

        assertEquals(items, repo.lastDeletedMany)
    }

    // --- updateOne / updateMany ---

    @Test
    fun updateOne_delegatesToRepository() = runTest {
        val item = app(2, 20, PitcherType.STARTER, 1)
        val repo = RecordingPitcherAppointmentRepository(listOf(item))
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.updateOne(item)

        assertEquals(item, repo.lastUpdatedOne)
    }

    @Test
    fun updateMany_doesNotCallRepository_whenEmpty() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.updateMany(emptyList())

        assertNull(repo.lastUpdated)
    }

    @Test
    fun updateMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(app(2, 20, PitcherType.STARTER, 1), app(2, 21, PitcherType.CLOSER, 2))
        val repo = RecordingPitcherAppointmentRepository(items)
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.updateMany(items)

        assertEquals(items, repo.lastUpdated)
    }

    // --- updateOnlyDiff ---

    @Test
    fun updateOnlyDiff_updatesOnlyChanged() = runTest {
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
    fun updateOnlyDiff_empty_throws() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(emptyList())
        }
    }

    @Test
    fun updateOnlyDiff_differentTeams_throws() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        val appointments = listOf(
            app(1, 20, PitcherType.STARTER, 1),
            app(2, 21, PitcherType.CLOSER, 2),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(appointments)
        }
    }

    @Test
    fun updateOnlyDiff_duplicatePlayerId_throws() = runTest {
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        val appointments = listOf(
            app(2, 20, PitcherType.STARTER, 1),
            app(2, 20, PitcherType.CLOSER, 2),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(appointments)
        }
    }

    @Test
    fun updateOnlyDiff_noChanges_doesNotCallUpdate() = runTest {
        val teamId = 2
        val current = listOf(
            app(teamId, 20, PitcherType.STARTER, 1),
            app(teamId, 21, PitcherType.CLOSER, 2),
        )
        val repo = RecordingPitcherAppointmentRepository(current)
        val useCase = PitcherAppointmentUseCase(repo)

        useCase.updateOnlyDiff(current)

        assertNull(repo.lastUpdated)
    }

    @Test
    fun updateOnlyDiff_newPlayers_updatesAll() = runTest {
        val teamId = 2
        val repo = RecordingPitcherAppointmentRepository(emptyList())
        val useCase = PitcherAppointmentUseCase(repo)

        val newApps = listOf(
            app(teamId, 20, PitcherType.STARTER, 1),
            app(teamId, 21, PitcherType.CLOSER, 2),
        )

        useCase.updateOnlyDiff(newApps)

        assertEquals(newApps, repo.lastUpdated)
    }
}

