package com.example.penasim.features.game.usecase

import com.example.penasim.features.game.domain.Stat
import com.example.penasim.features.game.domain.repository.StatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class StatUseCaseTest {
  private val repository: StatRepository = mock()
  private val useCase = StatUseCase(repository)

  private val stat = Stat(
    id = 1,
    gameFixtureId = 7,
    batterId = 11,
    pitcherId = 21,
    inning = 3,
    outCount = 1,
    hitCount = 2,
    earnedRun = 0,
    result = "single"
  )

  @Test
  fun getByFixtureId_delegatesToRepository() = runTest {
    whenever(repository.getByFixtureId(7)).thenReturn(listOf(stat))

    val actual = useCase.getByFixtureId(7)

    assertEquals(listOf(stat), actual)
  }

  @Test
  fun getByBatterId_delegatesToRepository() = runTest {
    whenever(repository.getByBatterId(11)).thenReturn(listOf(stat))

    val actual = useCase.getByBatterId(11)

    assertEquals(listOf(stat), actual)
  }

  @Test
  fun getByPitcherId_delegatesToRepository() = runTest {
    whenever(repository.getByPitcherId(21)).thenReturn(listOf(stat))

    val actual = useCase.getByPitcherId(21)

    assertEquals(listOf(stat), actual)
  }

  @Test
  fun getByFixtureIdAndBatterId_delegatesToRepository() = runTest {
    whenever(repository.getByFixtureIdAndBatterId(7, 11)).thenReturn(listOf(stat))

    val actual = useCase.getByFixtureIdAndBatterId(7, 11)

    assertEquals(listOf(stat), actual)
  }

  @Test
  fun getByFixtureIdAndPitcherId_delegatesToRepository() = runTest {
    whenever(repository.getByFixtureIdAndPitcherId(7, 21)).thenReturn(listOf(stat))

    val actual = useCase.getByFixtureIdAndPitcherId(7, 21)

    assertEquals(listOf(stat), actual)
  }

  @Test
  fun insertAll_skipsRepository_whenItemsAreEmpty() = runTest {
    useCase.insertAll(emptyList())

    verify(repository, never()).insertAll(org.mockito.kotlin.any())
  }

  @Test
  fun insertAll_delegatesToRepository_whenItemsExist() = runTest {
    useCase.insertAll(listOf(stat))

    verify(repository, times(1)).insertAll(listOf(stat))
  }

  @Test
  fun deleteByFixtureId_delegatesToRepository() = runTest {
    useCase.deleteByFixtureId(7)

    verify(repository).deleteByFixtureId(7)
  }
}
