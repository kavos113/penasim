package com.example.penasim.game

import com.example.penasim.domain.GameResult
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.TeamPlayers
import com.example.penasim.domain.isStarting

const val MAX_INNINGS = 9

enum class Half {
    INNING_TOP,
    INNING_BOTTOM
}

class Match(
    private val schedule: GameSchedule,
    private val homePlayers: TeamPlayers,
    private val awayPlayers: TeamPlayers
) {
    private var inning = 1
    private var half = Half.INNING_TOP

    private var outs = 0
    private var firstBaseOccupied = false
    private var secondBaseOccupied = false
    private var thirdBaseOccupied = false

    private var homeScore = 0
    private var awayScore = 0

    // 1-indexed
    private var homeBatterIndex = 1
    private var awayBatterIndex = 1

    private val homeScores: MutableList<Int> = mutableListOf()
    private val awayScores: MutableList<Int> = mutableListOf(0)

    private fun homeBatter(number: Int)
        = homePlayers.fielderAppointments.filter { it.position.isStarting() }.find { it.number == number }?.playerId
            ?: throw IllegalArgumentException("no home batter for number $number")
    private fun awayBatter(number: Int)
        = awayPlayers.fielderAppointments.filter { it.position.isStarting() }.find { it.number == number }?.playerId
            ?: throw IllegalArgumentException("no away batter for number $number")

    private fun Boolean.toStr() = if (this) "X" else " "
    private fun Half.toStr() = if (this == Half.INNING_TOP) "表" else "裏"

    fun play(): GameResult {
        println("------- ${schedule.awayTeam.name} @ ${schedule.homeTeam.name} -------")
        while (inning <= MAX_INNINGS) {
            batting()
        }

        awayScores.removeAt(awayScores.lastIndex)

        println("  1 2 3 4 5 6 7 8 9 | R")
        println("${schedule.awayTeam.name} ${awayScores.joinToString(" ")} | $awayScore")
        println("${schedule.homeTeam.name} ${homeScores.joinToString(" ")} | $homeScore")

        println("Final Score: $awayScore - $homeScore")

        return GameResult(
            fixtureId = schedule.fixture.id,
            homeScore = homeScore,
            awayScore = awayScore
        )
    }

    private fun batting() {
        val batter = when (half) {
            Half.INNING_TOP -> awayBatter(awayBatterIndex)
            Half.INNING_BOTTOM -> homeBatter(homeBatterIndex)
        }

//        println("$awayScore - $homeScore ${inning}回${half.toStr()} Out $outs, Bases: [${firstBaseOccupied.toStr()}, ${secondBaseOccupied.toStr()}, ${thirdBaseOccupied.toStr()}], Batter: $batter")

        // Simplified batting logic
        val outcome = (1..100).random()
        nextBatter()
        when {
            outcome <= 70 -> out() // 70% chance of out
            outcome <= 85 -> single() // 15% chance of single
            outcome <= 95 -> double() // 10% chance of double
            else -> triple() // 5% chance of triple
        }
    }

    private fun nextBatter() {
        when (half) {
            Half.INNING_TOP -> {
                awayBatterIndex++
                if (awayBatterIndex > 9) {
                    awayBatterIndex = 1
                }
            }
            Half.INNING_BOTTOM -> {
                homeBatterIndex++
                if (homeBatterIndex > 9) {
                    homeBatterIndex = 1
                }
            }
        }
    }

    private fun resetInning() {
        outs = 0
        firstBaseOccupied = false
        secondBaseOccupied = false
        thirdBaseOccupied = false
    }

    private fun score() {
        when (half) {
            Half.INNING_TOP -> {
                awayScore++
                awayScores[awayScores.lastIndex]++
            }
            Half.INNING_BOTTOM -> {
                homeScore++
                homeScores[homeScores.lastIndex]++
            }
        }
    }

    private fun out() {
        outs++
        if (outs >= 3) {
            when (half) {
                Half.INNING_TOP -> {
                    half = Half.INNING_BOTTOM
                    homeScores.add(0)
                }
                Half.INNING_BOTTOM -> {
                    half = Half.INNING_TOP
                    inning++
                    awayScores.add(0)
                }
            }
            resetInning()
        }
    }

    private fun single() {
        if (thirdBaseOccupied) {
            score()
            thirdBaseOccupied = false
        }
        if (secondBaseOccupied) {
            thirdBaseOccupied = true
            secondBaseOccupied = false
        }
        if (firstBaseOccupied) {
            secondBaseOccupied = true
            firstBaseOccupied = false
        }
        firstBaseOccupied = true
    }

    private fun double() {
        if (thirdBaseOccupied) {
            score()
            thirdBaseOccupied = false
        }
        if (secondBaseOccupied) {
            score()
            secondBaseOccupied = false
        }
        if (firstBaseOccupied) {
            thirdBaseOccupied = true
            firstBaseOccupied = false
        }
        secondBaseOccupied = true
    }

    private fun triple() {
        if (thirdBaseOccupied) {
            score()
            thirdBaseOccupied = false
        }
        if (secondBaseOccupied) {
            score()
            secondBaseOccupied = false
        }
        if (firstBaseOccupied) {
            score()
            firstBaseOccupied = false
        }
        thirdBaseOccupied = true
    }
}