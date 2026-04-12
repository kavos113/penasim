package com.example.penasim.features.game.usecase

import com.example.penasim.features.game.domain.HomeRun
import com.example.penasim.features.game.domain.repository.HomeRunRepository
import com.example.penasim.features.game.usecase.HomeRunUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HomeRunUseCaseTest {

    private val repo: HomeRunRepository = mock()
    private val useCase = HomeRunUseCase(repo)

    @Test
    fun getByFixtureId_delegatesToRepository() = runTest {
        val expected = listOf(
            HomeRun(fixtureId = 10, playerId = 100, inning = 3, count = 1),
        )
        whenever(repo.getHomeRunsByFixtureId(10)).thenReturn(expected)

        assertEquals(expected, useCase.getByFixtureId(10))
        verify(repo).getHomeRunsByFixtureId(10)
    }

    @Test
    fun getByPlayerId_delegatesToRepository() = runTest {
        val expected = listOf(
            HomeRun(fixtureId = 10, playerId = 100, inning = 3, count = 1),
            HomeRun(fixtureId = 11, playerId = 100, inning = 5, count = 2),
        )
        whenever(repo.getHomeRunsByPlayerId(100)).thenReturn(expected)

        assertEquals(expected, useCase.getByPlayerId(100))
        verify(repo).getHomeRunsByPlayerId(100)
    }

    @Test
    fun getByPlayerId_returnsEmpty_whenNoMatch() = runTest {
        whenever(repo.getHomeRunsByPlayerId(999)).thenReturn(emptyList())

        assertEquals(emptyList<HomeRun>(), useCase.getByPlayerId(999))
    }

    @Test
    fun insert_emptyList_doesNotCallRepository() = runTest {
        useCase.insert(emptyList())

        verify(repo, never()).insertHomeRuns(any())
    }

    @Test
    fun insert_nonEmptyList_delegatesToRepository() = runTest {
        val items = listOf(
            HomeRun(fixtureId = 10, playerId = 100, inning = 3, count = 1),
            HomeRun(fixtureId = 10, playerId = 200, inning = 5, count = 4),
        )

        useCase.insert(items)

        verify(repo).insertHomeRuns(items)
    }
}



