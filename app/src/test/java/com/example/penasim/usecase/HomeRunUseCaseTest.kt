package com.example.penasim.usecase

import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.repository.HomeRunRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class HomeRunUseCaseTest {

    private val repository: HomeRunRepository = mock()
    private val useCase = HomeRunUseCase(repository)

    @Test
    fun getByFixtureId_delegatesToRepository() = runTest {
        val fixtureId = 10
        val expected = listOf(
            HomeRun(fixtureId = fixtureId, playerId = 100, inning = 3, count = 1),
        )
        whenever(repository.getHomeRunsByFixtureId(fixtureId)).thenReturn(expected)

        val actual = useCase.getByFixtureId(fixtureId)

        assertEquals(expected, actual)
        verify(repository).getHomeRunsByFixtureId(fixtureId)
    }

    @Test
    fun insert_emptyList_isNoop() = runTest {
        val captor = argumentCaptor<List<HomeRun>>()

        useCase.insert(emptyList())

        // repository.insertHomeRuns should not be called
        try {
            verify(repository).insertHomeRuns(captor.capture())
            throw AssertionError("insertHomeRuns should not be called with empty list")
        } catch (_: Throwable) {
            // success: no invocation expected
        }
    }
}
