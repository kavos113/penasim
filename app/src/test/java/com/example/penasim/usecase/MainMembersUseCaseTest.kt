package com.example.penasim.usecase

import com.example.penasim.features.command.domain.MainMember
import com.example.penasim.features.command.domain.MemberType
import com.example.penasim.features.command.domain.repository.MainMembersRepository
import com.example.penasim.features.command.usecase.MainMembersUseCase
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

class MainMembersUseCaseTest {

    private val repo: MainMembersRepository = mock()
    private val useCase = MainMembersUseCase(repo)

    private fun mm(teamId: Int, playerId: Int, type: MemberType, isFielder: Boolean) =
        MainMember(teamId = teamId, playerId = playerId, memberType = type, isFielder = isFielder)

    // --- getByTeamId ---

    @Test
    fun getByTeamId_delegates_empty() = runTest {
        whenever(repo.getMainMembersByTeamId(3)).thenReturn(emptyList())

        val result = useCase.getByTeamId(3)

        assertEquals(emptyList<MainMember>(), result)
        verify(repo).getMainMembersByTeamId(3)
    }

    @Test
    fun getByTeamId_returnsMembersForTeam() = runTest {
        val m1 = mm(3, 30, MemberType.MAIN, true)
        val m2 = mm(3, 31, MemberType.SUB, false)
        whenever(repo.getMainMembersByTeamId(3)).thenReturn(listOf(m1, m2))

        val result = useCase.getByTeamId(3)

        assertEquals(listOf(m1, m2), result)
        verify(repo).getMainMembersByTeamId(3)
    }

    // --- getByPlayerId ---

    @Test
    fun getByPlayerId_returnsMember_whenExists() = runTest {
        val expected = mm(3, 30, MemberType.MAIN, true)
        whenever(repo.getMainMemberByPlayerId(30)).thenReturn(expected)

        val result = useCase.getByPlayerId(30)

        assertEquals(expected, result)
        verify(repo).getMainMemberByPlayerId(30)
    }

    @Test
    fun getByPlayerId_returnsNull_whenNotExists() = runTest {
        whenever(repo.getMainMemberByPlayerId(999)).thenReturn(null)

        assertNull(useCase.getByPlayerId(999))
        verify(repo).getMainMemberByPlayerId(999)
    }

    // --- insertOne / insertMany ---

    @Test
    fun insertOne_delegatesToRepository() = runTest {
        val item = mm(3, 30, MemberType.MAIN, true)

        useCase.insertOne(item)

        verify(repo).insertMainMember(item)
    }

    @Test
    fun insertMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.insertMany(emptyList())

        verify(repo, never()).insertMainMembers(any())
    }

    @Test
    fun insertMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(mm(3, 30, MemberType.MAIN, true), mm(3, 31, MemberType.SUB, false))

        useCase.insertMany(items)

        verify(repo).insertMainMembers(items)
    }

    // --- deleteOne / deleteMany ---

    @Test
    fun deleteOne_delegatesToRepository() = runTest {
        val item = mm(3, 30, MemberType.MAIN, true)

        useCase.deleteOne(item)

        verify(repo).deleteMainMember(item)
    }

    @Test
    fun deleteMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.deleteMany(emptyList())

        verify(repo, never()).deleteMainMembers(any())
    }

    @Test
    fun deleteMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(mm(3, 30, MemberType.MAIN, true), mm(3, 31, MemberType.SUB, false))

        useCase.deleteMany(items)

        verify(repo).deleteMainMembers(items)
    }

    // --- updateOne / updateMany ---

    @Test
    fun updateOne_delegatesToRepository() = runTest {
        val item = mm(3, 30, MemberType.MAIN, true)

        useCase.updateOne(item)

        verify(repo).updateMainMember(item)
    }

    @Test
    fun updateMany_doesNotCallRepository_whenEmpty() = runTest {
        useCase.updateMany(emptyList())

        verify(repo, never()).updateMainMembers(any())
    }

    @Test
    fun updateMany_delegatesToRepository_whenNonEmpty() = runTest {
        val items = listOf(mm(3, 30, MemberType.MAIN, true), mm(3, 31, MemberType.SUB, false))

        useCase.updateMany(items)

        verify(repo).updateMainMembers(items)
    }

    // --- updateOnlyDiff ---

    @Test
    fun updateOnlyDiff_updatesOnlyChanged() = runTest {
        val teamId = 3
        val current = listOf(
            mm(teamId, 30, MemberType.MAIN, true),
            mm(teamId, 31, MemberType.SUB, false),
        )
        whenever(repo.getMainMembersByTeamId(3)).thenReturn(current)

        val changed = current[1].copy(memberType = MemberType.MAIN)
        val newMember = mm(teamId, 32, MemberType.SUB, true)

        useCase.updateOnlyDiff(
            listOf(
                current[0].copy(memberType = MemberType.MAIN), // unchanged
                changed, // changed
                newMember, // new
            )
        )

        verify(repo).updateMainMembers(listOf(changed, newMember))
    }

    @Test
    fun updateOnlyDiff_empty_throws() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.updateOnlyDiff(emptyList())
        }
    }

    @Test
    fun updateOnlyDiff_differentTeams_throws() = runTest {
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
        whenever(repo.getMainMembersByTeamId(3)).thenReturn(current)

        useCase.updateOnlyDiff(current)

        verify(repo, never()).updateMainMembers(any())
    }
}


