package com.example.penasim.usecase

import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.PitcherAppointmentRepository
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

class PitcherAppointmentUseCaseTest {

    private val repo: PitcherAppointmentRepository = mock()
    private val useCase = PitcherAppointmentUseCase(repo)

    private fun app(teamId: Int, playerId: Int, type: PitcherType, number: Int) =
        PitcherAppointment(teamId = teamId, playerId = playerId, type = type, number = number)

    // --- getByTeam ---

    @Test
    fun getByTeam_delegates_empty() = runTest {
        val teamId = 2
        whenever(repo.getPitcherAppointmentsByTeamId(teamId)).thenReturn(emptyList())

        val result = useCase.getByTeam(Team(id = teamId, name = "B"))

        assertEquals(emptyList<PitcherAppointment>(), result)
        verify(repo).getPitcherAppointmentsByTeamId(teamId)
    }

    @Test
    fun getByTeam_returnsAppointmentsForTeam() = runTest {
        val team = Team(2, "B")
        val apps = listOf(
            app(2, 20, PitcherType.STARTER, 1),
            app(2, 21, PitcherType.CLOSER, 2)
        )
        whenever(repo.getPitcherAppointmentsByTeamId(2)).thenReturn(apps)

        val result = useCase.getByTeam(team)

        assertEquals(apps, result)
        verify(repo).getPitcherAppointmentsByTeamId(2)
    }

    // --- getByPlayerId ---

    @Test
    fun getByPlayerId_returnsAppointment_whenExists() = runTest {
        val expected = app(2, 20, PitcherType.STARTER, 1)
        whenever(repo.getPitcherAppointmentByPlayerId(20)).thenReturn(expected)

        val result = useCase.getByPlayerId(20)

        assertEquals(expected, result)
        verify(repo).getPitcherAppointmentByPlayerId(20)
    }

    @Test
    fun getByPlayerId_returnsNull_whenNotExists() = runTest {
        whenever(repo.getPitcherAppointmentByPlayerId(999)).thenReturn(null)

        assertNull(useCase.getByPlayerId(999))
        verify(repo).getPitcherAppointmentByPlayerId(999)
    }

    // --- insertOne / insertMany ---

    @Test
    fun insertOne_delegatesToRepository() = runTest {
        val item = app(2, 20, PitcherType.STARTER, 1)

        useCase.insertOne(item)

        verify(repo).insertPitcherAppointment(item)
    }

    @Test
    fun insertMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.insertMany(emptyList())

        verify(repo, never()).insertPitcherAppointments(any())
    }

    @Test
    fun insertMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(app(2, 20, PitcherType.STARTER, 1), app(2, 21, PitcherType.CLOSER, 2))

        useCase.insertMany(items)

        verify(repo).insertPitcherAppointments(items)
    }

    // --- deleteOne / deleteMany ---

    @Test
    fun deleteOne_delegatesToRepository() = runTest {
        val item = app(2, 20, PitcherType.STARTER, 1)

        useCase.deleteOne(item)

        verify(repo).deletePitcherAppointment(item)
    }

    @Test
    fun deleteMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.deleteMany(emptyList())

        verify(repo, never()).deletePitcherAppointments(any())
    }

    @Test
    fun deleteMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(app(2, 20, PitcherType.STARTER, 1), app(2, 21, PitcherType.CLOSER, 2))

        useCase.deleteMany(items)

        verify(repo).deletePitcherAppointments(items)
    }

    // --- updateOne / updateMany ---

    @Test
    fun updateOne_delegatesToRepository() = runTest {
        val item = app(2, 20, PitcherType.STARTER, 1)

        useCase.updateOne(item)

        verify(repo).updatePitcherAppointment(item)
    }

    @Test
    fun updateMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.updateMany(emptyList())

        verify(repo, never()).updatePitcherAppointments(any())
    }

    @Test
    fun updateMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(app(2, 20, PitcherType.STARTER, 1), app(2, 21, PitcherType.CLOSER, 2))

        useCase.updateMany(items)

        verify(repo).updatePitcherAppointments(items)
    }

    // --- updateOnlyDiff ---

    @Test
    fun updateOnlyDiff_updatesOnlyChanged() = runTest {
        val teamId = 2
        val current = listOf(
            app(teamId, 20, PitcherType.STARTER, 1),
            app(teamId, 21, PitcherType.CLOSER, 2),
        )
        whenever(repo.getPitcherAppointmentsByTeamId(teamId)).thenReturn(current)

        val newApps = listOf(
            current[0].copy(type = PitcherType.STARTER), // unchanged
            current[1].copy(type = PitcherType.RELIEVER), // changed
            app(teamId, 22, PitcherType.STARTER, 3), // new
        )

        useCase.updateOnlyDiff(newApps)

        val expected = listOf(
            current[1].copy(type = PitcherType.RELIEVER),
            app(teamId, 22, PitcherType.STARTER, 3),
        )
        verify(repo).updatePitcherAppointments(expected)
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
            app(1, 20, PitcherType.STARTER, 1),
            app(2, 21, PitcherType.CLOSER, 2),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(appointments)
        }
    }

    @Test
    fun updateOnlyDiff_duplicatePlayerId_throws() = runTest {
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
        whenever(repo.getPitcherAppointmentsByTeamId(teamId)).thenReturn(current)

        useCase.updateOnlyDiff(current)

        verify(repo, never()).updatePitcherAppointments(any())
    }

    @Test
    fun updateOnlyDiff_newPlayers_updatesAll() = runTest {
        val teamId = 2
        whenever(repo.getPitcherAppointmentsByTeamId(teamId)).thenReturn(emptyList())

        val newApps = listOf(
            app(teamId, 20, PitcherType.STARTER, 1),
            app(teamId, 21, PitcherType.CLOSER, 2),
        )

        useCase.updateOnlyDiff(newApps)

        verify(repo).updatePitcherAppointments(newApps)
    }
}

