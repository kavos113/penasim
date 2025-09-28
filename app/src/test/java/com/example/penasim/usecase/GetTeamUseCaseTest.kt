package com.example.penasim.usecase

import com.example.penasim.domain.League
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetTeamUseCaseTest {

    private class FakeTeamRepository(private val teams: List<Team>) : TeamRepository {
        override suspend fun getTeam(id: Int): Team? = teams.find { it.id == id }
        override suspend fun getTeamsByLeague(league: League): List<Team> = teams.filter { it.league == league }
        override suspend fun getAllTeams(): List<Team> = teams
    }

    @Test
    fun execute_returnsMatchingTeam_orNull() = runTest {
        val t1 = Team(1, "Alpha", League.L1)
        val t2 = Team(2, "Beta", League.L2)
        val useCase = GetTeamUseCase(FakeTeamRepository(listOf(t1, t2)))

        assertEquals(t1, useCase.execute(1))
        assertEquals(t2, useCase.execute(2))
        assertNull(useCase.execute(999))
    }
}
