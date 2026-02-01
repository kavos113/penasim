package com.example.penasim.game

import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.GameSchedule
import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.PitchingStat
import com.example.penasim.domain.TeamPlayers
import com.example.penasim.domain.isStarting

const val MAX_INNINGS = 9

enum class Half {
  INNING_TOP,
  INNING_BOTTOM
}

enum class Result {
  OUT,
  SINGLE_HIT,
  DOUBLE_HIT,
  TRIPLE_HIT,
  HOMERUN;

  override fun toString(): String {
    return when (this) {
      OUT -> {
        val random = (1 .. 2).random()
        when(random) {
          1 -> "ゴ"
          else -> "飛"
        }
      }
      SINGLE_HIT -> "安"
      DOUBLE_HIT -> "二"
      TRIPLE_HIT -> "三"
      HOMERUN -> "本"
    }
  }

  fun randomResult(): String {
    return if (this == HOMERUN) {
      randomOutPosition() + "本"
    } else {
      randomPosition() + this.toString()
    }
  }

  private fun randomPosition(): String {
    val random = (1 .. 100).random()
    return when {
      random <= 2 -> "捕"
      random <= 16 -> "一"
      random <= 30 -> "二"
      random <= 44 -> "三"
      random <= 58 -> "遊"
      random <= 72 -> "左"
      random <= 86 -> "中"
      else -> "右"
    }
  }

  private fun randomOutPosition(): String {
    val random = (1 .. 3).random()
    return when(random) {
      1 -> "左"
      2 -> "中"
      else -> "右"
    }
  }
}

data class LastResult(
  val result: Result,
  val isHit: Boolean,
  val isScored: Boolean
)

data class ScoreData(
  val scores: List<InningScore>,
  val outCount: Int = 0,
  val baseState: BaseState,
  val lastResult: LastResult,
  val isHomeBatting: Boolean = false,
  val homeBatterState: BatterState,
  val awayBatterState: BatterState,
  val homePitcherState: PitcherState,
  val awayPitcherState: PitcherState,
)

class Match(
  private val schedule: GameSchedule,
  homePlayers: TeamPlayers,
  awayPlayers: TeamPlayers
) {
  // game states
  private var inning = 1
  private var half = Half.INNING_TOP

  private var outs = 0
  private var lastResult: LastResult = LastResult(
    result = Result.OUT,
    isHit = false,
    isScored = false
  )
  private val baseState: BaseState = BaseState()

  private val homeTeamState: TeamState = TeamState(homePlayers)
  private val awayTeamState: TeamState = TeamState(awayPlayers)

  private val teamStat: TeamStat = TeamStat(fixtureId = schedule.fixture.id)

  private fun currentBatterId() = when (half) {
    Half.INNING_TOP -> awayTeamState.batter.playerId
    Half.INNING_BOTTOM -> homeTeamState.batter.playerId
  }

  private fun currentPitcherId() = when (half) {
    Half.INNING_TOP -> homeTeamState.pitcher.playerId
    Half.INNING_BOTTOM -> awayTeamState.pitcher.playerId
  }

  private fun Boolean.toStr() = if (this) "X" else " "
  private fun Half.toStr() = if (this == Half.INNING_TOP) "表" else "裏"

  fun play() {
    while (next()) {}
    postFinishGame()
  }

  // 続くならtrue
  fun next(): Boolean {
    if (inning > MAX_INNINGS) {
      return false
    }

    batting()

    return true
  }

  fun postFinishGame() {
    teamStat.finalize(homeTeamState.pitcher.playerId, awayTeamState.pitcher.playerId)

    println(
      """
            ------- ${schedule.awayTeam.name} @ ${schedule.homeTeam.name} -------
              1 2 3 4 5 6 7 8 9 | R
            ${schedule.awayTeam.name} ${teamStat.awayScores.joinToString(" ")} | ${teamStat.awayScore}
            ${schedule.homeTeam.name} ${teamStat.homeScores.joinToString(" ")} | ${teamStat.homeScore}
            Final Score: ${teamStat.awayScore} - ${teamStat.homeScore}
        """.trimIndent()
    )
  }

  fun result(): GameResult = teamStat.result()
  fun inningScores(): List<InningScore> = teamStat.inningScores(
    homeTeamId = schedule.homeTeam.id,
    awayTeamId = schedule.awayTeam.id
  )
  fun battingStats(): List<BattingStat> = teamStat.battingStats.values.toList()
  fun pitchingStats(): List<PitchingStat> = teamStat.pitchingStats.values.toList()
  fun homeRuns(): List<HomeRun> = teamStat.homeRuns()

  fun scoreData(): ScoreData = ScoreData(
    scores = inningScores(),
    outCount = outs,
    baseState = baseState,
    lastResult = lastResult,
    isHomeBatting = half == Half.INNING_BOTTOM,
    awayBatterState = awayTeamState.batter,
    homeBatterState = homeTeamState.batter,
    awayPitcherState = awayTeamState.pitcher,
    homePitcherState = homeTeamState.pitcher,
  )

  private fun batting() {
//        println("$awayScore - $homeScore ${inning}回${half.toStr()} Out $outs, Bases: [${firstBaseOccupied.toStr()}, ${secondBaseOccupied.toStr()}, ${thirdBaseOccupied.toStr()}], Batter: $batter")

    // Simplified batting logic
    val outcome = (1..100).random()
    when {
      outcome <= 70 -> out()
      outcome <= 85 -> single()
      outcome <= 95 -> double()
      outcome <= 98 -> triple()
      else -> homeRun()
    }
    decreasePitcherStamina()
    nextBatter()
  }

  private fun nextBatter() {
    when (half) {
      Half.INNING_TOP -> awayTeamState.goNextBatter()
      Half.INNING_BOTTOM -> homeTeamState.goNextBatter()
    }
  }

  private fun decreasePitcherStamina() {
    when (half) {
      Half.INNING_TOP -> homeTeamState.decreasePitcherStamina()
      Half.INNING_BOTTOM -> awayTeamState.decreasePitcherStamina()
    }
  }

  private fun resetInning() {
    outs = 0
    baseState.reset()
  }

  private fun score(count: Int) {
    val batter = currentBatterId()
    val pitcher = currentPitcherId()

    teamStat.score(batter, pitcher, half, count)
  }

  private fun out() {
    outs++
    lastResult = LastResult(
      result = Result.OUT,
      isHit = false,
      isScored = false
    )

    val batter = currentBatterId()
    val pitcher = currentPitcherId()

    teamStat.out(batter, pitcher)

    if (outs >= 3) {
      when (half) {
        Half.INNING_TOP -> {
          half = Half.INNING_BOTTOM
        }

        Half.INNING_BOTTOM -> {
          half = Half.INNING_TOP
          inning++
        }
      }
      teamStat.newInning(half)
      resetInning()
    }
  }

  private fun single() {
    val batter = currentBatterId()
    val pitcher = currentPitcherId()

    teamStat.single(batter, pitcher)

    val score = baseState.single(batter)
    if (score > 0) {
      score(score)
    }

    lastResult = LastResult(
      result = Result.SINGLE_HIT,
      isHit = true,
      isScored = score > 0
    )
  }

  private fun double() {
    val batter = currentBatterId()
    val pitcher = currentPitcherId()

    teamStat.double(batter, pitcher)

    val score = baseState.double(batter)
    if (score > 0) {
      score(score)
    }

    lastResult = LastResult(
      result = Result.DOUBLE_HIT,
      isHit = true,
      isScored = score > 0
    )
  }

  private fun triple() {
    val batter = currentBatterId()
    val pitcher = currentPitcherId()

    teamStat.triple(batter, pitcher)

    val score = baseState.triple(batter)
    if (score > 0) {
      score(score)
    }

    lastResult = LastResult(
      result = Result.TRIPLE_HIT,
      isHit = true,
      isScored = score > 0
    )
  }

  private fun homeRun() {
    val batter = currentBatterId()
    val pitcher = currentPitcherId()

    val score = baseState.homeRun()
    if (score > 0) {
      score(score)
    }

    teamStat.homeRun(batter, pitcher, half, inning, score)

    lastResult = LastResult(
      result = Result.HOMERUN,
      isHit = true,
      isScored = true
    )
  }
}