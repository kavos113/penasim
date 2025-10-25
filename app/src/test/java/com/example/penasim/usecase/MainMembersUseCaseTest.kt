package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import com.example.penasim.domain.MemberType
import com.example.penasim.domain.repository.MainMembersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class MainMembersUseCaseTest {

    private class RecordingMainMembersRepository(
        private val current: List<MainMember>
    ) : MainMembersRepository {
        var lastUpdated: List<MainMember>? = null

        override suspend fun getMainMembersByTeamId(teamId: Int): List<MainMember> =
            current.filter { it.teamId == teamId }

        override suspend fun getMainMemberByPlayerId(playerId: Int): MainMember? =
            current.find { it.playerId == playerId }

        override suspend fun insertMainMember(mainMember: MainMember) {}
        override suspend fun insertMainMembers(mainMembers: List<MainMember>) {}
        override suspend fun deleteMainMember(mainMember: MainMember) {}
        override suspend fun deleteMainMembers(mainMembers: List<MainMember>) {}
        override suspend fun updateMainMember(mainMember: MainMember) {}
        override suspend fun updateMainMembers(mainMembers: List<MainMember>) {
            lastUpdated = mainMembers
        }
    }

    private fun mm(teamId: Int, playerId: Int, type: MemberType, isFielder: Boolean) =
        MainMember(teamId = teamId, playerId = playerId, memberType = type, isFielder = isFielder)

    @Test
    fun updateTeamMembers_diffAndValidate_updatesOnlyChanged() = runTest {
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

        useCase.updateTeamMembers(changed)

        val updated = repo.lastUpdated
        requireNotNull(updated)
        assertEquals(listOf(changed[1], changed[2]), updated)
    }

    @Test
    fun updateTeamMembers_empty_throws() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = MainMembersUseCase(repo)

        assertFailsWith<IllegalArgumentException> {
            useCase.updateTeamMembers(emptyList())
        }
    }

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
}

