package com.example.penasim.usecase

import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.repository.HomeRunRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeRunUseCaseTest {

    private class FakeHomeRunRepository(
        private val data: List<HomeRun> = emptyList()
    ) : HomeRunRepository {
        var lastInserted: List<HomeRun>? = null

        override suspend fun getHomeRunsByFixtureId(fixtureId: Int): List<HomeRun> =
            data.filter { it.fixtureId == fixtureId }

        override suspend fun getHomeRunsByPlayerId(playerId: Int): List<HomeRun> =
            data.filter { it.playerId == playerId }

        override suspend fun insertHomeRuns(homeRuns: List<HomeRun>) {
            lastInserted = homeRuns
        }
    }

    @Test
    fun getByFixtureId_delegatesToRepository() = runTest {
        val fixtureId = 10
        val expected = listOf(
            HomeRun(fixtureId = fixtureId, playerId = 100, inning = 3, count = 1),
        )
        val repository = FakeHomeRunRepository(expected)
        val useCase = HomeRunUseCase(repository)

        val actual = useCase.getByFixtureId(fixtureId)

        assertEquals(expected, actual)
    }

    @Test
    fun getByPlayerId_delegatesToRepository() = runTest {
        val data = listOf(
            HomeRun(fixtureId = 10, playerId = 100, inning = 3, count = 1),
            HomeRun(fixtureId = 11, playerId = 100, inning = 5, count = 2),
            HomeRun(fixtureId = 10, playerId = 200, inning = 7, count = 1),
        )
        val repository = FakeHomeRunRepository(data)
        val useCase = HomeRunUseCase(repository)

        val result = useCase.getByPlayerId(100)

        assertEquals(2, result.size)
        assertEquals(listOf(data[0], data[1]), result)
    }

    @Test
    fun getByPlayerId_returnsEmpty_whenNoMatch() = runTest {
        val repository = FakeHomeRunRepository(emptyList())
        val useCase = HomeRunUseCase(repository)

        val result = useCase.getByPlayerId(999)

        assertEquals(emptyList<HomeRun>(), result)
    }

    @Test
    fun insert_emptyList_doesNotCallRepository() = runTest {
        val repository = FakeHomeRunRepository()
        val useCase = HomeRunUseCase(repository)

        useCase.insert(emptyList())

        assertNull(repository.lastInserted)
    }

    @Test
    fun insert_nonEmptyList_delegatesToRepository() = runTest {
        val repository = FakeHomeRunRepository()
        val useCase = HomeRunUseCase(repository)
        val items = listOf(
            HomeRun(fixtureId = 10, playerId = 100, inning = 3, count = 1),
            HomeRun(fixtureId = 10, playerId = 200, inning = 5, count = 4),
        )

        useCase.insert(items)

        assertEquals(items, repository.lastInserted)
    }
}
