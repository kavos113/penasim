package com.example.penasim.usecase

import com.example.penasim.domain.InningScore
import com.example.penasim.domain.repository.InningScoreRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class InningScoreUseCaseTest {

    private val repo: InningScoreRepository = mock()
    private val useCase = InningScoreUseCase(repo)

    @Test
    fun getByFixtureId_delegatesToRepository() = runTest {
        val expected = listOf(InningScore(1, 10, 1, 0), InningScore(1, 10, 2, 2))
        whenever(repo.getByFixtureId(1)).thenReturn(expected)

        assertEquals(expected, useCase.getByFixtureId(1))
        verify(repo).getByFixtureId(1)
    }

    @Test
    fun getByFixtureIds_delegatesToRepository() = runTest {
        val expected = listOf(InningScore(1, 10, 1, 0), InningScore(3, 10, 1, 2))
        whenever(repo.getByFixtureIds(listOf(1, 3))).thenReturn(expected)

        assertEquals(expected, useCase.getByFixtureIds(listOf(1, 3)))
        verify(repo).getByFixtureIds(listOf(1, 3))
    }

    @Test
    fun getByTeamId_delegatesToRepository() = runTest {
        val expected = listOf(InningScore(1, 10, 1, 0), InningScore(3, 10, 2, 2))
        whenever(repo.getByTeamId(10)).thenReturn(expected)

        assertEquals(expected, useCase.getByTeamId(10))
        verify(repo).getByTeamId(10)
    }

    @Test
    fun getByTeamIds_delegatesToRepository() = runTest {
        val expected = listOf(InningScore(1, 10, 1, 0), InningScore(3, 12, 1, 2))
        whenever(repo.getByTeamIds(listOf(10, 12))).thenReturn(expected)

        assertEquals(expected, useCase.getByTeamIds(listOf(10, 12)))
        verify(repo).getByTeamIds(listOf(10, 12))
    }

    @Test
    fun insertAll_doesNotCallRepository_whenEmpty() = runTest {
        useCase.insertAll(emptyList())

        verify(repo, never()).insertAll(any())
    }

    @Test
    fun insertAll_callsRepository_whenNotEmpty() = runTest {
        val item = InningScore(1, 10, 1, 2)

        useCase.insertAll(listOf(item))

        verify(repo).insertAll(listOf(item))
    }
}

