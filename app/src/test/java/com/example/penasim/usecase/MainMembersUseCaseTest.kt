package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import com.example.penasim.domain.MemberType
import com.example.penasim.domain.repository.MainMembersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.test.assertFailsWith

class MainMembersUseCaseTest {

    private class RecordingMainMembersRepository(
        private val current: List<MainMember>
    ) : MainMembersRepository {
        var lastUpdated: List<MainMember>? = null
        var lastInsertedOne: MainMember? = null
        var lastInsertedMany: List<MainMember>? = null
        var lastDeletedOne: MainMember? = null
        var lastDeletedMany: List<MainMember>? = null
        var lastUpdatedOne: MainMember? = null

        override suspend fun getMainMembersByTeamId(teamId: Int): List<MainMember> =
            current.filter { it.teamId == teamId }

        override suspend fun getMainMemberByPlayerId(playerId: Int): MainMember? =
            current.find { it.playerId == playerId }

        override suspend fun insertMainMember(mainMember: MainMember) {
            lastInsertedOne = mainMember
        }
        override suspend fun insertMainMembers(mainMembers: List<MainMember>) {
            lastInsertedMany = mainMembers
        }
        override suspend fun deleteMainMember(mainMember: MainMember) {
            lastDeletedOne = mainMember
        }
        override suspend fun deleteMainMembers(mainMembers: List<MainMember>) {
            lastDeletedMany = mainMembers
        }
        override suspend fun updateMainMember(mainMember: MainMember) {
            lastUpdatedOne = mainMember
        }
        override suspend fun updateMainMembers(mainMembers: List<MainMember>) {
            lastUpdated = mainMembers
        }
    }

    private fun mm(teamId: Int, playerId: Int, type: MemberType, isFielder: Boolean) =
        MainMember(teamId = teamId, playerId = playerId, memberType = type, isFielder = isFielder)

    // --- getByTeamId ---

    @Test
    fun getByTeamId_delegates_empty() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        val result = useCase.getByTeamId(3)
        assertEquals(emptyList<MainMember>(), result)
    }

    @Test
    fun getByTeamId_returnsMembersForTeam() = runTest {
        val m1 = mm(3, 30, MemberType.MAIN, true)
        val m2 = mm(3, 31, MemberType.SUB, false)
        val repo = RecordingMainMembersRepository(listOf(m1, m2))
        val useCase = MainMembersUseCase(repo)

        val result = useCase.getByTeamId(3)

        assertEquals(listOf(m1, m2), result)
    }

    // --- getByPlayerId ---

    @Test
    fun getByPlayerId_returnsMember_whenExists() = runTest {
        val expected = mm(3, 30, MemberType.MAIN, true)
        val repo = RecordingMainMembersRepository(listOf(expected))
        val useCase = MainMembersUseCase(repo)

        val result = useCase.getByPlayerId(30)
        assertEquals(expected, result)
    }

    @Test
    fun getByPlayerId_returnsNull_whenNotExists() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        assertNull(useCase.getByPlayerId(999))
    }

    // --- insertOne / insertMany ---

    @Test
    fun insertOne_delegatesToRepository() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)
        val item = mm(3, 30, MemberType.MAIN, true)

        useCase.insertOne(item)

        assertEquals(item, repo.lastInsertedOne)
    }

    @Test
    fun insertMany_doesNotCallRepository_whenEmpty() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        useCase.insertMany(emptyList())

        assertNull(repo.lastInsertedMany)
    }

    @Test
    fun insertMany_delegatesToRepository_whenNonEmpty() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)
        val items = listOf(mm(3, 30, MemberType.MAIN, true), mm(3, 31, MemberType.SUB, false))

        useCase.insertMany(items)

        assertEquals(items, repo.lastInsertedMany)
    }

    // --- deleteOne / deleteMany ---

    @Test
    fun deleteOne_delegatesToRepository() = runTest {
        val item = mm(3, 30, MemberType.MAIN, true)
        val repo = RecordingMainMembersRepository(listOf(item))
        val useCase = MainMembersUseCase(repo)

        useCase.deleteOne(item)

        assertEquals(item, repo.lastDeletedOne)
    }

    @Test
    fun deleteMany_doesNotCallRepository_whenEmpty() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        useCase.deleteMany(emptyList())

        assertNull(repo.lastDeletedMany)
    }

    @Test
    fun deleteMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(mm(3, 30, MemberType.MAIN, true), mm(3, 31, MemberType.SUB, false))
        val repo = RecordingMainMembersRepository(items)
        val useCase = MainMembersUseCase(repo)

        useCase.deleteMany(items)

        assertEquals(items, repo.lastDeletedMany)
    }

    // --- updateOne / updateMany ---

    @Test
    fun updateOne_delegatesToRepository() = runTest {
        val item = mm(3, 30, MemberType.MAIN, true)
        val repo = RecordingMainMembersRepository(listOf(item))
        val useCase = MainMembersUseCase(repo)

        useCase.updateOne(item)

        assertEquals(item, repo.lastUpdatedOne)
    }

    @Test
    fun updateMany_doesNotCallRepository_whenEmpty() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        useCase.updateMany(emptyList())

        assertNull(repo.lastUpdated)
    }

    @Test
    fun updateMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(mm(3, 30, MemberType.MAIN, true), mm(3, 31, MemberType.SUB, false))
        val repo = RecordingMainMembersRepository(items)
        val useCase = MainMembersUseCase(repo)

        useCase.updateMany(items)

        assertEquals(items, repo.lastUpdated)
    }

    // --- updateOnlyDiff ---

    @Test
    fun updateOnlyDiff_updatesOnlyChanged() = runTest {
        val teamId = 3
        val current = listOf(
            mm(teamId, 30, MemberType.MAIN, true),
            mm(teamId, 31, MemberType.SUB, false),
        )
        val repo = RecordingMainMembersRepository(current)
        val useCase = MainMembersUseCase(repo)

        val changed = listOf(
            current[0].copy(memberType = MemberType.MAIN), // unchanged
            current[1].copy(memberType = MemberType.MAIN), // changed
            mm(teamId, 32, MemberType.SUB, true), // new
        )

        useCase.updateOnlyDiff(changed)

        val updated = repo.lastUpdated
        requireNotNull(updated)
        assertEquals(listOf(changed[1], changed[2]), updated)
    }

    @Test
    fun updateOnlyDiff_empty_throws() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(emptyList())
        }
    }

    @Test
    fun updateOnlyDiff_differentTeams_throws() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        val members = listOf(
            mm(1, 30, MemberType.MAIN, true),
            mm(2, 31, MemberType.SUB, false),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(members)
        }
    }

    @Test
    fun updateOnlyDiff_duplicatePlayerId_throws() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        val members = listOf(
            mm(3, 30, MemberType.MAIN, true),
            mm(3, 30, MemberType.SUB, false),
        )

        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(members)
        }
    }

    @Test
    fun updateOnlyDiff_noChanges_doesNotCallUpdate() = runTest {
        val teamId = 3
        val current = listOf(
            mm(teamId, 30, MemberType.MAIN, true),
            mm(teamId, 31, MemberType.SUB, false),
        )
        val repo = RecordingMainMembersRepository(current)
        val useCase = MainMembersUseCase(repo)

        useCase.updateOnlyDiff(current)

        assertNull(repo.lastUpdated)
    }
}

