package com.example.penasim.usecase

import com.example.penasim.domain.MainMember
import com.example.penasim.domain.MemberType
import com.example.penasim.domain.repository.MainMembersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMainMembersByTeamUseCaseTest {

    private class FakeMainMembersRepository(private val data: List<MainMember>) : MainMembersRepository {
        override suspend fun getMainMembersByTeamId(teamId: Int): List<MainMember> = data.filter { it.teamId == teamId }
        override suspend fun getMainMemberByPlayerId(playerId: Int): MainMember? = data.find { it.playerId == playerId }
        override suspend fun insertMainMember(mainMember: MainMember) {}
        override suspend fun insertMainMembers(mainMembers: List<MainMember>) {}
        override suspend fun deleteMainMember(mainMember: MainMember) {}
        override suspend fun deleteMainMembers(mainMembers: List<MainMember>) {}
        override suspend fun updateMainMember(mainMember: MainMember) {}
        override suspend fun updateMainMembers(mainMembers: List<MainMember>) {}
    }

    @Test
    fun execute_returnsMembersForTeam() = runTest {
        val m1 = MainMember(teamId = 1, playerId = 10, memberType = MemberType.MAIN, isFielder = true)
        val m2 = MainMember(teamId = 1, playerId = 11, memberType = MemberType.SUB, isFielder = false)
        val m3 = MainMember(teamId = 2, playerId = 20, memberType = MemberType.MAIN, isFielder = true)
        val useCase = GetMainMembersByTeamUseCase(FakeMainMembersRepository(listOf(m1, m2, m3)))

        assertEquals(listOf(m1, m2), useCase.execute(1))
        assertEquals(listOf(m3), useCase.execute(2))
        assertEquals(emptyList<MainMember>(), useCase.execute(999))
    }
}
