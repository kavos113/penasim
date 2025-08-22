package com.example.penasim.usecase

import com.example.penasim.domain.GameRepository
import com.example.penasim.domain.League
import com.example.penasim.domain.TeamRepository
import com.example.penasim.domain.TeamStanding

class GetRankingUseCase(
    private val teamRepository: TeamRepository,
    private val gameRepository: GameRepository,
) {
    suspend fun execute(league: League): List<TeamStanding> {
        val teams = teamRepository.getTeamsByLeague(league)
        val standings = mutableListOf<TeamStanding>()

        for (team in teams) {
            val games = gameRepository.getFinishedGamesByTeam(team)
            var wins = 0
            var losses = 0
            var draws = 0
            for (game in games) {
                assert(game.homeTeam == team || game.awayTeam == team) {
                    "Game does not belong to the team"
                }
                assert(game.homeScore != null && game.awayScore != null) {
                    "Game scores must be recorded"
                }

                when {
                    game.homeTeam == team -> {
                        when {
                            game.homeScore!! > game.awayScore!! -> wins++
                            game.homeScore < game.awayScore -> losses++
                            else -> draws++
                        }
                    }
                    game.awayTeam == team -> {
                        when {
                            game.awayScore!! > game.homeScore!! -> wins++
                            game.awayScore < game.homeScore -> losses++
                            else -> draws++
                        }
                    }
                    else -> throw IllegalStateException("Game does not belong to the team")
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