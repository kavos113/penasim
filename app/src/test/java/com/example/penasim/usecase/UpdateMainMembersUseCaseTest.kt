package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import com.example.penasim.domain.MemberType
import com.example.penasim.domain.repository.MainMembersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class UpdateMainMembersUseCaseTest {

    private class RecordingMainMembersRepository(
        private val current: List<MainMember>
    ) : MainMembersRepository {
        var lastUpdated: List<MainMember>? = null

        override suspend fun getMainMembersByTeamId(teamId: Int): List<MainMember> = current.filter { it.teamId == teamId }
        override suspend fun getMainMemberByPlayerId(playerId: Int): MainMember? = current.find { it.playerId == playerId }
        override suspend fun insertMainMember(mainMember: MainMember) {}
        override suspend fun insertMainMembers(mainMembers: List<MainMember>) {}
        override suspend fun deleteMainMember(mainMember: MainMember) {}
        override suspend fun deleteMainMembers(mainMembers: List<MainMember>) {}
        override suspend fun updateMainMember(mainMember: MainMember) {}
        override suspend fun updateMainMembers(mainMembers: List<MainMember>) { lastUpdated = mainMembers }
    }

    private fun mm(teamId: Int, playerId: Int, type: MemberType, isFielder: Boolean) =
        MainMember(teamId = teamId, playerId = playerId, memberType = type, isFielder = isFielder)

    @Test
    fun execute_updatesOnlyChangedOrNewMembers() = runTest {
        val current = listOf(
            mm(1, 10, MemberType.MAIN, true),
            mm(1, 11, MemberType.SUB, false)
        )
        val repo = RecordingMainMembersRepository(current)
        val useCase = UpdateMainMembersUseCase(repo)

        // Case 1: no change -> empty update
        useCase.execute(current)
        assertEquals(emptyList<MainMember>(), repo.lastUpdated)

        // Case 2: one changed + one same + one new -> update contains changed + new
        val changed = mm(1, 10, MemberType.SUB, true)
        val same = current[1]
        val newOne = mm(1, 12, MemberType.MAIN, true)
        repo.lastUpdated = null
        useCase.execute(listOf(changed, same, newOne))
        assertEquals(listOf(changed, newOne), repo.lastUpdated)
    }

    @Test
    fun execute_assertsOnInvalidInputs() = runTest {
        val repo = RecordingMainMembersRepository(emptyList())
        val useCase = UpdateMainMembersUseCase(repo)

        // empty
        assertFailsWith<AssertionError> { useCase.execute(emptyList()) }

        // mixed teams
        val a = mm(1, 10, MemberType.MAIN, true)
        val b = mm(2, 11, MemberType.SUB, false)
        assertFailsWith<AssertionError> { useCase.execute(listOf(a, b)) }

        // duplicate players
        val c = mm(1, 10, MemberType.MAIN, true)
        val d = mm(1, 10, MemberType.SUB, false)
        assertFailsWith<AssertionError> { useCase.execute(listOf(c, d)) }
    }
}
