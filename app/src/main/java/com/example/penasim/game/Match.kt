package com.example.penasim.game

import com.example.penasim.domain.GameResult
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.TeamPlayers

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

    fun play(): GameResult {
        homeScore = (0..10).random()
        awayScore = (0..10).random()
        return GameResult(
            fixtureId = schedule.fixture.id,
            homeScore = homeScore,
            awayScore = awayScore
        )
    }
}