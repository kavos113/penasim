package com.example.penasim.usecase

import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.repository.BattingStatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BattingStatUseCaseTest {

    private class RecordingBattingStatRepository(
        private val data: MutableList<BattingStat>
    ) : BattingStatRepository {
        var lastInserted: List<BattingStat>? = null
        var lastDeletedFixtureId: Int? = null

        override suspend fun getByFixtureId(fixtureId: Int): List<BattingStat> =
            data.filter { it.gameFixtureId == fixtureId }

        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<BattingStat> =
            data.filter { it.gameFixtureId in fixtureIds }

        override suspend fun getByPlayerId(playerId: Int): List<BattingStat> =
            data.filter { it.playerId == playerId }

        override suspend fun getByPlayerIds(playerIds: List<Int>): List<BattingStat> =
            data.filter { it.playerId in playerIds }

        override suspend fun insertAll(items: List<BattingStat>) {
            lastInserted = items
            data.addAll(items)
        }

        override suspend fun deleteByFixtureId(fixtureId: Int) {
            lastDeletedFixtureId = fixtureId
            data.removeAll { it.gameFixtureId == fixtureId }
        }
    }

    private fun bs(fix: Int, player: Int, atBat: Int = 0, hit: Int = 0) =
        BattingStat(gameFixtureId = fix, playerId = player, atBat = atBat, hit = hit)

    @Test
    fun get_queries_returnExpectedSubsets() = runTest {
        val repo = RecordingBattingStatRepository(mutableListOf(
            bs(10, 1, atBat = 4, hit = 2),
            bs(10, 2, atBat = 3, hit = 1),
            bs(11, 1, atBat = 5, hit = 3)
        ))
        val useCase = BattingStatUseCase(repo)

        assertEquals(2, useCase.getByFixtureId(10).size)
        assertEquals(3, useCase.getByFixtureIds(listOf(10, 11)).size)
        assertEquals(2, useCase.getByPlayerId(1).size)
        assertEquals(3, useCase.getByPlayerIds(listOf(1, 2)).size)
    }

    @Test
    fun insertAll_ignoresEmpty_andInsertsNonEmpty() = runTest {
        val repo = RecordingBattingStatRepository(mutableListOf())
        val useCase = BattingStatUseCase(repo)

        // empty -> repository.insertAll should not be called
        useCase.insertAll(emptyList())
        assertNull(repo.lastInserted)

        // non-empty -> repository.insertAll should be called with items
        val items = listOf(bs(12, 3, 4, 2), bs(12, 4, 3, 1))
        useCase.insertAll(items)
        assertEquals(items, repo.lastInserted)
        assertEquals(2, repo.getByFixtureId(12).size)
    }

    @Test
    fun deleteByFixtureId_delegates_andRemoves() = runTest {
        val repo = RecordingBattingStatRepository(mutableListOf(
            bs(13, 5), bs(13, 6), bs(14, 7)
        ))
        val useCase = BattingStatUseCase(repo)

        useCase.deleteByFixtureId(13)
        assertEquals(13, repo.lastDeletedFixtureId)
        assertEquals(emptyList<BattingStat>(), repo.getByFixtureId(13))
        assertEquals(1, repo.getByFixtureIds(listOf(14)).size)
    }
}

