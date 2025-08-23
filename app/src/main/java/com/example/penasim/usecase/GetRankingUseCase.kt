package com.example.penasim.usecase

import com.example.penasim.domain.repository.GameResultRepository
import com.example.penasim.domain.League
import com.example.penasim.domain.repository.TeamRepository
import com.example.penasim.domain.TeamStanding
import com.example.penasim.domain.repository.GameFixtureRepository
import javax.inject.Inject

class GetRankingUseCase @Inject constructor(
    private val teamRepository: TeamRepository,
    private val gameFixtureRepository: GameFixtureRepository,
    private val gameResultRepository: GameResultRepository,
) {
    suspend fun execute(league: League): List<TeamStanding> {
        val teams = teamRepository.getTeamsByLeague(league)
        val standings = mutableListOf<TeamStanding>()

        for (team in teams) {
            val fixtures = gameFixtureRepository.getGameFixturesByTeam(team)
            val fixtureIds = fixtures.map { it.id }
            val gameResults = gameResultRepository.getGamesByFixtureIds(fixtureIds)

            var wins = 0
            var losses = 0
            var draws = 0
            for (gameResult in gameResults) {
                val fixture = fixtures.find { it.id == gameResult.fixtureId }
                    ?: throw IllegalArgumentException("Fixture with id ${gameResult.fixtureId} not found")
                assert(fixture.homeTeamId == team.id || fixture.awayTeamId == team.id) {
                    "Game does not belong to the team"
                }

                when {
                    fixture.homeTeamId == team.id -> { // team is home
                        when {
                            gameResult.homeScore > gameResult.awayScore -> wins++
                            gameResult.homeScore < gameResult.awayScore -> losses++
                            else -> draws++
                        }
                    }

                    fixture.awayTeamId == team.id -> { // team is away
                        when {
                            gameResult.awayScore > gameResult.homeScore -> wins++
                            gameResult.awayScore < gameResult.homeScore -> losses++
                            else -> draws++
                        }
                    }
                    else -> throw IllegalStateException("Unreachable code")
                }
            }

            standings.add(
                TeamStanding(
                    team = team,
                    rank = 0, // Rank will be assigned later
                    wins = wins,
                    losses = losses,
                    draws = draws
                )
            )
        }

        // Sort standings by wins, then by losses, then by draws
        standings.sortWith(compareByDescending<TeamStanding> { it.wins }
            .thenByDescending { it.losses }
            .thenByDescending { it.draws })

        // Assign ranks
        for (i in standings.indices) {
            standings[i] = standings[i].copy(rank = i + 1)
        }

        // calculate game back
        if (standings.isNotEmpty()) {
            val leader = standings[0]
            for (i in standings.indices) {
                val teamStanding = standings[i]
                if (i == 0) {
                    standings[i] = teamStanding.copy(gameBack = 0.0)
                } else {
                    val gameBack = (leader.wins - teamStanding.wins + teamStanding.losses - leader.losses) / 2.0
                    standings[i] = teamStanding.copy(gameBack = gameBack)
                }
            }
        }

        return standings
    }
}