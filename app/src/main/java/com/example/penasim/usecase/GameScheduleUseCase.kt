package com.example.penasim.usecase

import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.Team
import com.example.penasim.domain.repository.GameFixtureRepository
import com.example.penasim.domain.repository.TeamRepository
import java.time.LocalDate
import javax.inject.Inject

class GameScheduleUseCase @Inject constructor(
    private val gameFixtureRepository: GameFixtureRepository,
    private val teamRepository: TeamRepository
) {
    suspend fun getByFixtureId(id: Int): GameSchedule {
        val fixture = gameFixtureRepository.getGameFixture(id)
            ?: throw IllegalArgumentException("Game fixture with id $id not found")

        val homeTeam = teamRepository.getTeam(fixture.homeTeamId)
            ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")
        val awayTeam = teamRepository.getTeam(fixture.awayTeamId)
            ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")

        return GameSchedule(
            fixture = fixture,
            homeTeam = homeTeam,
            awayTeam = awayTeam
        )
    }

    suspend fun getByTeam(team: Team): List<GameSchedule> {
        val fixtures = gameFixtureRepository.getGameFixturesByTeam(team)

        val teams = fixtures.flatMap { listOf(it.homeTeamId, it.awayTeamId) }
            .distinct()
            .mapNotNull { teamRepository.getTeam(it) }

        return fixtures.map { fixture ->
            val homeTeam = teams.find { it.id == fixture.homeTeamId }
                ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")
            val awayTeam = teams.find { it.id == fixture.awayTeamId }
                ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")
            GameSchedule(
                fixture = fixture,
                homeTeam = homeTeam,
                awayTeam = awayTeam
            )
        }
    }

    suspend fun getByDate(date: LocalDate): List<GameSchedule> {
        val fixtures = gameFixtureRepository.getGameFixturesByDate(date)

        val teams = fixtures.flatMap { listOf(it.homeTeamId, it.awayTeamId) }
            .distinct()
            .mapNotNull { teamRepository.getTeam(it) }

        return fixtures.map { fixture ->
            val homeTeam = teams.find { it.id == fixture.homeTeamId }
                ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")
            val awayTeam = teams.find { it.id == fixture.awayTeamId }
                ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")
            GameSchedule(
                fixture = fixture,
                homeTeam = homeTeam,
                awayTeam = awayTeam
            )
        }
    }

    suspend fun getAll(): List<GameSchedule> {
        val fixtures = gameFixtureRepository.getAllGameFixtures()

        val teams = fixtures.flatMap { listOf(it.homeTeamId, it.awayTeamId) }
            .distinct()
            .mapNotNull { teamRepository.getTeam(it) }

        return fixtures.map { fixture ->
            val homeTeam = teams.find { it.id == fixture.homeTeamId }
                ?: throw IllegalArgumentException("Home team with id ${fixture.homeTeamId} not found")
            val awayTeam = teams.find { it.id == fixture.awayTeamId }
                ?: throw IllegalArgumentException("Away team with id ${fixture.awayTeamId} not found")
            GameSchedule(
                fixture = fixture,
                homeTeam = homeTeam,
                awayTeam = awayTeam
            )
        }
    }
}