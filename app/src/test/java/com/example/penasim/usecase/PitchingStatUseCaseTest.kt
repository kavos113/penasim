package com.example.penasim.usecase

import com.example.penasim.features.game.domain.PitchingStat
import com.example.penasim.features.game.domain.repository.PitchingStatRepository
import com.example.penasim.features.game.usecase.PitchingStatUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PitchingStatUseCaseTest {

    private val repo: PitchingStatRepository = mock()
    private val useCase = PitchingStatUseCase(repo)

    private fun ps(fix: Int, player: Int, inningOuts: Int = 0, so: Int = 0) =
        PitchingStat(gameFixtureId = fix, playerId = player, inningPitched = inningOuts, strikeOut = so)

    @Test
    fun getByFixtureId_delegatesToRepository() = runTest {
        val expected = listOf(ps(20, 1, 9, 5), ps(20, 2, 3, 1))
        whenever(repo.getByFixtureId(20)).thenReturn(expected)

        assertEquals(expected, useCase.getByFixtureId(20))
        verify(repo).getByFixtureId(20)
    }

    @Test
    fun getByFixtureIds_delegatesToRepository() = runTest {
        val expected = listOf(ps(20, 1), ps(21, 1))
        whenever(repo.getByFixtureIds(listOf(20, 21))).thenReturn(expected)

        assertEquals(expected, useCase.getByFixtureIds(listOf(20, 21)))
        verify(repo).getByFixtureIds(listOf(20, 21))
    }

    @Test
    fun getByPlayerId_delegatesToRepository() = runTest {
        val expected = listOf(ps(20, 1), ps(21, 1))
        whenever(repo.getByPlayerId(1)).thenReturn(expected)

        assertEquals(expected, useCase.getByPlayerId(1))
        verify(repo).getByPlayerId(1)
    }

    @Test
    fun getByPlayerIds_delegatesToRepository() = runTest {
        val expected = listOf(ps(20, 1), ps(20, 2), ps(21, 1))
        whenever(repo.getByPlayerIds(listOf(1, 2))).thenReturn(expected)

        assertEquals(expected, useCase.getByPlayerIds(listOf(1, 2)))
        verify(repo).getByPlayerIds(listOf(1, 2))
    }

    @Test
    fun insertAll_ignoresEmpty() = runTest {
        useCase.insertAll(emptyList())

        verify(repo, never()).insertAll(any())
    }

    @Test
    fun insertAll_delegatesNonEmpty() = runTest {
        val items = listOf(ps(22, 3, 3, 1), ps(22, 4, 6, 2))

        useCase.insertAll(items)

        verify(repo).insertAll(items)
    }

    @Test
    fun deleteByFixtureId_delegatesToRepository() = runTest {
        useCase.deleteByFixtureId(23)

        verify(repo).deleteByFixtureId(23)
    }
}


