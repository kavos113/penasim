package com.example.penasim.model

import com.example.penasim.R
import kotlin.random.Random

class PennantManager {
    private val games: MutableList<List<GameInfo>> = mutableListOf()
    val teamInfo: MutableList<TeamInfo> = mutableListOf()

    fun initTeams() {
        repeat(12) {
            teamInfo.add(
                TeamInfo(
                    teamIcon = when (it) {
                        0 -> R.drawable.team1_icon
                        1 -> R.drawable.team2_icon
                        2 -> R.drawable.team3_icon
                        3 -> R.drawable.team4_icon
                        4 -> R.drawable.team5_icon
                        5 -> R.drawable.team6_icon
                        6 -> R.drawable.team7_icon
                        7 -> R.drawable.team8_icon
                        8 -> R.drawable.team9_icon
                        9 -> R.drawable.team10_icon
                        10 -> R.drawable.team11_icon
                        else -> R.drawable.team12_icon
                    },
                    teamName = "Team $it",
                    league = it % 2,
                    rank = it / 2
                )
            )
        }
    }

    private fun getRandomGame(): List<GameInfo> {
        val currentGame = games.size

        return List(6) {
            GameInfo(
                day = currentGame,
                numberOfGames = it,
                homeTeamId = it,
                awayTeamId = it + 6,
                homeTeamScore = Random.nextInt(0, 10),
                awayTeamScore = Random.nextInt(0, 10)
            )
        }
    }

    fun nextRandomGame(): Int {
        return nextGame(getRandomGame())
    }

    internal fun nextGame(newGames: List<GameInfo>): Int {
        val currentGame = games.size
        if (currentGame >= 143) {
            return currentGame
        }

        assert(newGames.size == 6)

        games.add(newGames)

        updateRankings(games[currentGame])
        return games.size
    }


    internal fun updateRankings(recentGames: List<GameInfo>) {
        recentGames.forEach { game ->
            if (game.homeTeamScore > game.awayTeamScore) {
                teamInfo[game.homeTeamId].wins++
                teamInfo[game.awayTeamId].losses++
            } else if (game.homeTeamScore < game.awayTeamScore) {
                teamInfo[game.homeTeamId].losses++
                teamInfo[game.awayTeamId].wins++
            } else {
                teamInfo[game.homeTeamId].draws++
                teamInfo[game.awayTeamId].draws++
            }
        }

        val leagueTeams = teamInfo.groupBy { it.league }

        leagueTeams.forEach { (_, teams) ->
            val sortedTeams = teams.sortedWith(compareByDescending<TeamInfo> {
                val total = it.wins + it.losses + it.draws
                if (total == 0) 0.0 else it.wins.toDouble() / total
            }
                .thenByDescending { it.wins }
                .thenBy { it.losses }
            )


            sortedTeams.forEachIndexed { index, team ->
                val teamIndex = teamInfo.indexOf(team)
                teamInfo[teamIndex].rank = index + 1

                if (index == 0) {
                    teamInfo[teamIndex].gameBack = 0.0
                } else {
                    val leader = sortedTeams[0]
                    teamInfo[teamIndex].gameBack =
                        (leader.wins - team.wins + team.losses - leader.losses) / 2.0
                }
            }
        }
    }

    @Deprecated(message = "only for testing")
    fun displayRankings() {
        println("================Day${games.size}=====================")
        games[games.size - 1].forEach {
            println("${teamInfo[it.homeTeamId].teamName} ${it.homeTeamScore} - ${it.awayTeamScore} ${teamInfo[it.awayTeamId].teamName}")
        }

        teamInfo.forEach {
            println("${it.teamName} - Rank${it.rank} - W${it.wins} - L${it.losses} - D${it.draws} - ${it.gameBack}")
        }

    }

    fun getRecentGames(): List<GameInfo> {
        return games.last()
    }

    init {
        initTeams()
    }
}