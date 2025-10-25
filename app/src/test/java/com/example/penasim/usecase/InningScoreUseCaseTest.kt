package com.example.penasim.usecase

import com.example.penasim.domain.InningScore
import com.example.penasim.domain.repository.InningScoreRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InningScoreUseCaseTest {
    private class FakeInningScoreRepository(private val items: List<InningScore> = emptyList()) : InningScoreRepository {
        var inserted: List<InningScore>? = null

        override suspend fun getByFixtureId(fixtureId: Int): List<InningScore> = items.filter { it.fixtureId == fixtureId }
        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<InningScore> = items.filter { it.fixtureId in fixtureIds }
        override suspend fun getByTeamId(teamId: Int): List<InningScore> = items.filter { it.teamId == teamId }
        override suspend fun getByTeamIds(teamIds: List<Int>): List<InningScore> = items.filter { it.teamId in teamIds }
        override suspend fun insertAll(items: List<InningScore>) {
            inserted = items
        }

        override suspend fun deleteByFixtureId(fixtureId: Int) {}
    }

    @Test
    fun getByFixtureId_returnsItems() = runTest {
        val scores = listOf(
            InningScore(1, 10, 1, 0),
            InningScore(2, 11, 1, 1),
            InningScore(1, 10, 2, 2)
        )
        val repo = FakeInningScoreRepository(scores)
        val useCase = InningScoreUseCase(repo)

        val result = useCase.getByFixtureId(1)

        assertEquals(2, result.size)
        assertEquals(scores[0], result[0])
        assertEquals(scores[2], result[1])
    }

    @Test
    fun getByFixtureIds_returnsItems() = runTest {
        val scores = listOf(
            InningScore(1, 10, 1, 0),
            InningScore(2, 11, 1, 1),
            InningScore(3, 10, 1, 2)
        )
        val repo = FakeInningScoreRepository(scores)
        val useCase = InningScoreUseCase(repo)

        val result = useCase.getByFixtureIds(listOf(1, 3))

        assertEquals(2, result.size)
        assertEquals(1, result[0].fixtureId)
        assertEquals(3, result[1].fixtureId)
    }

    @Test
    fun getByTeamId_returnsItems() = runTest {
        val scores = listOf(
            InningScore(1, 10, 1, 0),
            InningScore(2, 11, 1, 1),
            InningScore(3, 10, 2, 2)
        )
        val repo = FakeInningScoreRepository(scores)
        val useCase = InningScoreUseCase(repo)

        val result = useCase.getByTeamId(10)

        assertEquals(2, result.size)
        assertEquals(1, result[0].fixtureId)
        assertEquals(3, result[1].fixtureId)
    }

    @Test
    fun getByTeamIds_returnsItems() = runTest {
        val scores = listOf(
            InningScore(1, 10, 1, 0),
            InningScore(2, 11, 1, 1),
            InningScore(3, 12, 1, 2)
        )
        val repo = FakeInningScoreRepository(scores)
        val useCase = InningScoreUseCase(repo)

        val result = useCase.getByTeamIds(listOf(10, 12))

        assertEquals(2, result.size)
        assertEquals(10, result[0].teamId)
        assertEquals(12, result[1].teamId)
    }

    @Test
    fun insertAll_doesNotCallRepository_whenEmpty() = runTest {
        val repo = FakeInningScoreRepository()
        val useCase = InningScoreUseCase(repo)

        useCase.insertAll(emptyList())

        assertNull(repo.inserted)
    }

    @Test
    fun insertAll_callsRepository_whenNotEmpty() = runTest {
        val repo = FakeInningScoreRepository()
        val useCase = InningScoreUseCase(repo)
        val item = InningScore(1, 10, 1, 2)

        useCase.insertAll(listOf(item))

        assertEquals(listOf(item), repo.inserted)
    }
}

