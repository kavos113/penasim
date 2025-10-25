package com.example.penasim.usecase

import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.repository.PitchingStatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PitchingStatUseCaseTest {

    private class RecordingPitchingStatRepository(
        private val data: MutableList<PitchingStat>
    ) : PitchingStatRepository {
        var lastInserted: List<PitchingStat>? = null
        var lastDeletedFixtureId: Int? = null

        override suspend fun getByFixtureId(fixtureId: Int): List<PitchingStat> =
            data.filter { it.gameFixtureId == fixtureId }

        override suspend fun getByFixtureIds(fixtureIds: List<Int>): List<PitchingStat> =
            data.filter { it.gameFixtureId in fixtureIds }

        override suspend fun getByPlayerId(playerId: Int): List<PitchingStat> =
            data.filter { it.playerId == playerId }

        override suspend fun getByPlayerIds(playerIds: List<Int>): List<PitchingStat> =
            data.filter { it.playerId in playerIds }

        override suspend fun insertAll(items: List<PitchingStat>) {
            lastInserted = items
            data.addAll(items)
        }

        override suspend fun deleteByFixtureId(fixtureId: Int) {
            lastDeletedFixtureId = fixtureId
            data.removeAll { it.gameFixtureId == fixtureId }
        }
    }

    private fun ps(fix: Int, player: Int, inningOuts: Int = 0, so: Int = 0) =
        PitchingStat(gameFixtureId = fix, playerId = player, inningPitched = inningOuts, strikeOut = so)

    @Test
    fun get_queries_returnExpectedSubsets() = runTest {
        val repo = RecordingPitchingStatRepository(mutableListOf(
            ps(20, 1, inningOuts = 9, so = 5),
            ps(20, 2, inningOuts = 3, so = 1),
            ps(21, 1, inningOuts = 6, so = 4)
        ))
        val useCase = PitchingStatUseCase(repo)

        assertEquals(2, useCase.getByFixtureId(20).size)
        assertEquals(3, useCase.getByFixtureIds(listOf(20, 21)).size)
        assertEquals(2, useCase.getByPlayerId(1).size)
        assertEquals(3, useCase.getByPlayerIds(listOf(1, 2)).size)
    }

    @Test
    fun insertAll_ignoresEmpty_andInsertsNonEmpty() = runTest {
        val repo = RecordingPitchingStatRepository(mutableListOf())
        val useCase = PitchingStatUseCase(repo)

        // empty -> repository.insertAll should not be called
        useCase.insertAll(emptyList())
        assertNull(repo.lastInserted)

        // non-empty -> repository.insertAll should be called with items
        val items = listOf(ps(22, 3, 3, 1), ps(22, 4, 6, 2))
        useCase.insertAll(items)
        assertEquals(items, repo.lastInserted)
        assertEquals(2, repo.getByFixtureId(22).size)
    }

    @Test
    fun deleteByFixtureId_delegates_andRemoves() = runTest {
        val repo = RecordingPitchingStatRepository(mutableListOf(
            ps(23, 5), ps(23, 6), ps(24, 7)
        ))
        val useCase = PitchingStatUseCase(repo)

        useCase.deleteByFixtureId(23)
        assertEquals(23, repo.lastDeletedFixtureId)
        assertEquals(emptyList<PitchingStat>(), repo.getByFixtureId(23))
        assertEquals(1, repo.getByFixtureIds(listOf(24)).size)
    }
}

