package com.example.penasim.usecase

import com.example.penasim.features.game.domain.BattingStat
import com.example.penasim.features.game.domain.repository.BattingStatRepository
import com.example.penasim.features.game.usecase.BattingStatUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BattingStatUseCaseTest {

    private val repo: BattingStatRepository = mock()
    private val useCase = BattingStatUseCase(repo)

    private fun bs(fix: Int, player: Int, atBat: Int = 0, hit: Int = 0) =
        BattingStat(gameFixtureId = fix, playerId = player, atBat = atBat, hit = hit)

    @Test
    fun getByFixtureId_delegatesToRepository() = runTest {
        val expected = listOf(bs(10, 1, 4, 2), bs(10, 2, 3, 1))
        whenever(repo.getByFixtureId(10)).thenReturn(expected)

        val result = useCase.getByFixtureId(10)

        assertEquals(expected, result)
        verify(repo).getByFixtureId(10)
    }

    @Test
    fun getByFixtureIds_delegatesToRepository() = runTest {
        val expected = listOf(bs(10, 1), bs(11, 1))
        whenever(repo.getByFixtureIds(listOf(10, 11))).thenReturn(expected)

        val result = useCase.getByFixtureIds(listOf(10, 11))

        assertEquals(expected, result)
        verify(repo).getByFixtureIds(listOf(10, 11))
    }

    @Test
    fun getByPlayerId_delegatesToRepository() = runTest {
        val expected = listOf(bs(10, 1), bs(11, 1))
        whenever(repo.getByPlayerId(1)).thenReturn(expected)

        val result = useCase.getByPlayerId(1)

        assertEquals(expected, result)
        verify(repo).getByPlayerId(1)
    }

    @Test
    fun getByPlayerIds_delegatesToRepository() = runTest {
        val expected = listOf(bs(10, 1), bs(10, 2), bs(11, 1))
        whenever(repo.getByPlayerIds(listOf(1, 2))).thenReturn(expected)

        val result = useCase.getByPlayerIds(listOf(1, 2))

        assertEquals(expected, result)
        verify(repo).getByPlayerIds(listOf(1, 2))
    }

    @Test
    fun insertAll_ignoresEmpty() = runTest {
        useCase.insertAll(emptyList())

        verify(repo, never()).insertAll(any())
    }

    @Test
    fun insertAll_delegatesNonEmpty() = runTest {
        val items = listOf(bs(12, 3, 4, 2), bs(12, 4, 3, 1))

        useCase.insertAll(items)

        verify(repo).insertAll(items)
    }

    @Test
    fun deleteByFixtureId_delegatesToRepository() = runTest {
        useCase.deleteByFixtureId(13)

        verify(repo).deleteByFixtureId(13)
    }
}


