package com.example.penasim.usecase

import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.repository.GameFixtureRepository
import com.example.penasim.features.schedule.usecase.GameScheduleResolver
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.team.domain.repository.TeamRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GameScheduleResolverTest {

  private val fixtureRepository: GameFixtureRepository = mock()
  private val teamRepository: TeamRepository = mock()
  private val resolver = GameScheduleResolver(fixtureRepository, teamRepository)

  @Test
  fun getByFixtureId_returnsNull_whenFixtureDoesNotExist() = runTest {
    whenever(fixtureRepository.getGameFixture(1)).thenReturn(null)

    val actual = resolver.getByFixtureId(1)

    assertEquals(null, actual)
  }

  @Test
  fun getByFixtureId_buildsSchedule_whenTeamsExist() = runTest {
    val home = Team(1, "Home", League.L1)
    val away = Team(2, "Away", League.L1)
    val fixture = GameFixture(1, LocalDate.of(2025, 7, 1), 1, home.id, away.id)
    whenever(fixtureRepository.getGameFixture(1)).thenReturn(fixture)
    whenever(teamRepository.getTeam(home.id)).thenReturn(home)
    whenever(teamRepository.getTeam(away.id)).thenReturn(away)

    val actual = resolver.getByFixtureId(1)

    assertEquals(fixture, actual?.fixture)
    assertEquals(home, actual?.homeTeam)
    assertEquals(away, actual?.awayTeam)
  }

  @Test
  fun getByDate_throws_whenRequiredTeamIsMissing() = runTest {
    val fixture = GameFixture(1, LocalDate.of(2025, 7, 1), 1, 1, 2)
    whenever(fixtureRepository.getGameFixturesByDate(fixture.date)).thenReturn(listOf(fixture))
    whenever(teamRepository.getTeam(1)).thenReturn(Team(1, "Home", League.L1))
    whenever(teamRepository.getTeam(2)).thenReturn(null)

    assertFailsWith<IllegalArgumentException> {
      resolver.getByDate(fixture.date)
    }
  }
}
