package com.example.penasim.features.game.usecase

import com.example.penasim.features.game.domain.GameResult
import com.example.penasim.features.game.usecase.GameInfoAssembler
import com.example.penasim.features.schedule.domain.GameFixture
import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.team.domain.League
import com.example.penasim.features.team.domain.Team
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GameInfoAssemblerTest {

  private val assembler = GameInfoAssembler()
  private val home = Team(1, "Home", League.L1)
  private val away = Team(2, "Away", League.L1)

  @Test
  fun fromSchedule_buildsGameInfo() {
    val schedule = GameSchedule(
      fixture = GameFixture(1, LocalDate.of(2025, 7, 1), 1, home.id, away.id),
      homeTeam = home,
      awayTeam = away
    )
    val result = GameResult(1, 3, 1)

    val actual = assembler.fromSchedule(schedule, result)

    assertEquals(schedule.fixture, actual.fixture)
    assertEquals(home, actual.homeTeam)
    assertEquals(away, actual.awayTeam)
    assertEquals(result, actual.result)
  }

  @Test
  fun fromSchedules_throws_whenResultHasNoMatchingSchedule() {
    val schedules = listOf(
      GameSchedule(GameFixture(1, LocalDate.of(2025, 7, 1), 1, home.id, away.id), home, away)
    )
    val results = listOf(GameResult(2, 0, 0))

    assertFailsWith<IllegalArgumentException> {
      assembler.fromSchedules(schedules, results)
    }
  }
}


