package com.example.penasim.features.schedule.usecase

import com.example.penasim.features.schedule.domain.GameSchedule
import com.example.penasim.features.schedule.domain.repository.GameFixtureRepository
import com.example.penasim.features.team.domain.Team
import com.example.penasim.features.team.domain.repository.TeamRepository
import java.time.LocalDate
import javax.inject.Inject

class GameScheduleResolver @Inject constructor(
  private val gameFixtureRepository: GameFixtureRepository,
  private val teamRepository: TeamRepository
) {
  suspend fun getByFixtureId(id: Int): GameSchedule? {
    val fixture = gameFixtureRepository.getGameFixture(id) ?: return null
    return resolveFixture(fixture.id, fixture.homeTeamId, fixture.awayTeamId)?.copy(fixture = fixture)
  }

  suspend fun getByDate(date: LocalDate): List<GameSchedule> {
    val fixtures = gameFixtureRepository.getGameFixturesByDate(date)
    return resolveFixtures(fixtures.map { Triple(it.id, it.homeTeamId, it.awayTeamId) }, fixtures.associateBy { it.id })
  }

  suspend fun getByTeam(team: Team): List<GameSchedule> {
    val fixtures = gameFixtureRepository.getGameFixturesByTeam(team)
    return resolveFixtures(fixtures.map { Triple(it.id, it.homeTeamId, it.awayTeamId) }, fixtures.associateBy { it.id })
  }

  suspend fun getAll(): List<GameSchedule> {
    val fixtures = gameFixtureRepository.getAllGameFixtures()
    return resolveFixtures(fixtures.map { Triple(it.id, it.homeTeamId, it.awayTeamId) }, fixtures.associateBy { it.id })
  }

  private suspend fun resolveFixtures(
    fixtureMeta: List<Triple<Int, Int, Int>>,
    fixturesById: Map<Int, com.example.penasim.features.schedule.domain.GameFixture>
  ): List<GameSchedule> {
    val teams = fixtureMeta.flatMap { listOf(it.second, it.third) }
      .distinct()
      .mapNotNull { teamRepository.getTeam(it) }
      .associateBy { it.id }

    return fixtureMeta.map { (fixtureId, homeTeamId, awayTeamId) ->
      val fixture = fixturesById[fixtureId]
        ?: throw IllegalArgumentException("Game fixture with id $fixtureId not found")
      val homeTeam = teams[homeTeamId]
        ?: throw IllegalArgumentException("Home team with id $homeTeamId not found")
      val awayTeam = teams[awayTeamId]
        ?: throw IllegalArgumentException("Away team with id $awayTeamId not found")

      GameSchedule(
        fixture = fixture,
        homeTeam = homeTeam,
        awayTeam = awayTeam
      )
    }
  }

  private suspend fun resolveFixture(fixtureId: Int, homeTeamId: Int, awayTeamId: Int): GameSchedule? {
    val fixture = gameFixtureRepository.getGameFixture(fixtureId) ?: return null
    val homeTeam = teamRepository.getTeam(homeTeamId)
      ?: throw IllegalArgumentException("Home team with id $homeTeamId not found")
    val awayTeam = teamRepository.getTeam(awayTeamId)
      ?: throw IllegalArgumentException("Away team with id $awayTeamId not found")

    return GameSchedule(
      fixture = fixture,
      homeTeam = homeTeam,
      awayTeam = awayTeam
    )
  }
}
