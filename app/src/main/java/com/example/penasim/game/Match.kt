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

data class ScoreData(
  val scores: List<InningScore>,
  val outCount: Int = 0,
  val firstBaseId: Int? = null,
  val secondBaseId: Int? = null,
  val thirdBaseId: Int? = null,
  val lastResult: String,
  val homeActiveId: Int,
  val awayActiveId: Int,
  val homeActiveNumber: Int?,
  val awayActiveNumber: Int?
)

class Match(
  private val schedule: GameSchedule,
  private val homePlayers: TeamPlayers,
  private val awayPlayers: TeamPlayers
) {
  // game states
  private var inning = 1
  private var half = Half.INNING_TOP

  private var outs = 0
  private var firstBaseId: Int? = null
  private var secondBaseId: Int? = null
  private var thirdBaseId: Int? = null
  private var lastResult: Result = Result.OUT

  private var homeScore = 0
  private var awayScore = 0

  // 1-indexed
  private var homeBatterIndex = 0
  private var awayBatterIndex = 1

  // game records
  private val homeScores: MutableList<Int> = mutableListOf()
  private val awayScores: MutableList<Int> = mutableListOf(0)

  private val battingStats: MutableMap<Int, BattingStat> = mutableMapOf()
  private val pitchingStats: MutableMap<Int, PitchingStat> = mutableMapOf()

  private val homeHomeRuns: MutableList<HomeRun> = mutableListOf()
  private val awayHomeRuns: MutableList<HomeRun> = mutableListOf()

  private fun homeBatter(number: Int) =
    homePlayers.fielderAppointments.filter { it.position.isStarting() }
      .find { it.number == number }?.playerId
      ?: throw IllegalArgumentException("no home batter for number $number")

  private fun awayBatter(number: Int) =
    awayPlayers.fielderAppointments.filter { it.position.isStarting() }
      .find { it.number == number }?.playerId
      ?: throw IllegalArgumentException("no away batter for number $number")

  private fun homePitcher() =
    homePlayers.pitcherAppointments.find { it.type == PitcherType.STARTER && it.number == 1 }?.playerId
      ?: throw IllegalArgumentException("no home pitcher")

  private fun awayPitcher() =
    awayPlayers.pitcherAppointments.find { it.type == PitcherType.STARTER && it.number == 1 }?.playerId
      ?: throw IllegalArgumentException("no away pitcher")

  private fun currentBatter() = when (half) {
    Half.INNING_TOP -> awayBatter(awayBatterIndex)
    Half.INNING_BOTTOM -> homeBatter(homeBatterIndex)
  }

  private fun currentPitcher() = when (half) {
    Half.INNING_TOP -> homePitcher()
    Half.INNING_BOTTOM -> awayPitcher()
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
    awayScores.removeAt(awayScores.lastIndex)

    if (homeScore > awayScore) {
      pitchingStats[homePitcher()] = pitchingStats[homePitcher()]!!.copy(
        win = true
      )
      pitchingStats[awayPitcher()] = pitchingStats[awayPitcher()]!!.copy(
        lose = true
      )
    } else if (awayScore > homeScore) {
      pitchingStats[awayPitcher()] = pitchingStats[awayPitcher()]!!.copy(
        win = true
      )
      pitchingStats[homePitcher()] = pitchingStats[homePitcher()]!!.copy(
        lose = true
      )
    }

    println(
      """
            ------- ${schedule.awayTeam.name} @ ${schedule.homeTeam.name} -------
              1 2 3 4 5 6 7 8 9 | R
            ${schedule.awayTeam.name} ${awayScores.joinToString(" ")} | $awayScore
            ${schedule.homeTeam.name} ${homeScores.joinToString(" ")} | $homeScore
            Final Score: $awayScore - $homeScore
        """.trimIndent()
    )
  }

  fun result(): GameResult = GameResult(
    fixtureId = schedule.fixture.id,
    homeScore = homeScore,
    awayScore = awayScore,
  )

  fun inningScores(): List<InningScore> {
    val scores = mutableListOf<InningScore>()
    for (i in 1..homeScores.size) {
      scores.add(
        InningScore(
          fixtureId = schedule.fixture.id,
          teamId = schedule.homeTeam.id,
          inning = i,
          score = homeScores[i - 1]
        )
      )
    }
    for (i in 1..awayScores.size) {
      scores.add(
        InningScore(
          fixtureId = schedule.fixture.id,
          teamId = schedule.awayTeam.id,
          inning = i,
          score = awayScores[i - 1]
        )
      )
    }
    return scores
  }

  fun scoreData(): ScoreData {
    when(half) {
      Half.INNING_TOP -> return ScoreData(
        scores = inningScores(),
        outCount = outs,
        firstBaseId = firstBaseId,
        secondBaseId = secondBaseId,
        thirdBaseId = thirdBaseId,
        lastResult = lastResult.randomResult(),
        awayActiveId = awayBatter(awayBatterIndex),
        awayActiveNumber = awayBatterIndex,
        homeActiveId = homePitcher(),
        homeActiveNumber = null
      )
      Half.INNING_BOTTOM -> return ScoreData(
        scores = inningScores(),
        outCount = outs,
        firstBaseId = firstBaseId,
        secondBaseId = secondBaseId,
        thirdBaseId = thirdBaseId,
        lastResult = lastResult.randomResult(),
        awayActiveId = awayPitcher(),
        awayActiveNumber = null,
        homeActiveId = homeBatter(homeBatterIndex),
        homeActiveNumber = homeBatterIndex
      )
    }
  }

  fun battingStats(): List<BattingStat> = battingStats.values.toList()
  fun pitchingStats(): List<PitchingStat> = pitchingStats.values.toList()
  fun homeRuns(): List<HomeRun> = homeHomeRuns + awayHomeRuns

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
    nextBatter()
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
    firstBaseId = null
    secondBaseId = null
    thirdBaseId = null
  }

  private fun score() {
    val batter = currentBatter()
    val pitcher = currentPitcher()

    // stats are already initialized in out() / single() / double() / triple()
    battingStats[batter] = battingStats[batter]!!.copy(
      hit = battingStats[batter]!!.rbi + 1,
    )

    pitchingStats[pitcher] = pitchingStats[pitcher]!!.copy(
      run = pitchingStats[pitcher]!!.run + 1,
      earnedRun = pitchingStats[pitcher]!!.earnedRun + 1,
    )

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
    lastResult = Result.OUT

    val batter = currentBatter()
    val pitcher = currentPitcher()

    if (batter in battingStats) {
      battingStats[batter] = battingStats[batter]!!.copy(
        atBat = battingStats[batter]!!.atBat + 1
      )
    } else {
      battingStats[batter] = BattingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = batter,
        atBat = 1,
      )
    }

    if (pitcher in pitchingStats) {
      pitchingStats[pitcher] = pitchingStats[pitcher]!!.copy(
        inningPitched = pitchingStats[pitcher]!!.inningPitched + 1
      )
    } else {
      pitchingStats[pitcher] = PitchingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = pitcher,
        inningPitched = 1,
      )
    }

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
    val batter = currentBatter()
    val pitcher = currentPitcher()

    lastResult = Result.SINGLE_HIT

    if (batter in battingStats) {
      battingStats[batter] = battingStats[batter]!!.copy(
        atBat = battingStats[batter]!!.atBat + 1,
        hit = battingStats[batter]!!.hit + 1
      )
    } else {
      battingStats[batter] = BattingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = batter,
        atBat = 1,
        hit = 1,
      )
    }

    if (pitcher in pitchingStats) {
      pitchingStats[pitcher] = pitchingStats[pitcher]!!.copy(
        hit = pitchingStats[pitcher]!!.hit + 1
      )
    } else {
      pitchingStats[pitcher] = PitchingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = pitcher,
        hit = 1,
      )
    }

    if (thirdBaseId != null) {
      score()
      thirdBaseId = null
    }
    if (secondBaseId != null) {
      thirdBaseId = secondBaseId
      secondBaseId = null
    }
    if (firstBaseId != null) {
      secondBaseId = firstBaseId
      firstBaseId = null
    }
    firstBaseId = batter
  }

  private fun double() {
    val batter = currentBatter()
    val pitcher = currentPitcher()

    lastResult = Result.DOUBLE_HIT

    if (batter in battingStats) {
      battingStats[batter] = battingStats[batter]!!.copy(
        atBat = battingStats[batter]!!.atBat + 1,
        hit = battingStats[batter]!!.hit + 1,
        doubleHit = battingStats[batter]!!.doubleHit + 1
      )
    } else {
      battingStats[batter] = BattingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = batter,
        atBat = 1,
        hit = 1,
        doubleHit = 1,
      )
    }

    if (pitcher in pitchingStats) {
      pitchingStats[pitcher] = pitchingStats[pitcher]!!.copy(
        hit = pitchingStats[pitcher]!!.hit + 1
      )
    } else {
      pitchingStats[pitcher] = PitchingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = pitcher,
        hit = 1,
      )
    }

    if (thirdBaseId != null) {
      score()
      thirdBaseId = null
    }
    if (secondBaseId != null) {
      score()
      secondBaseId = null
    }
    if (firstBaseId != null) {
      thirdBaseId = firstBaseId
      firstBaseId = null
    }
    secondBaseId = batter
  }

  private fun triple() {
    val batter = currentBatter()
    val pitcher = currentPitcher()

    lastResult = Result.TRIPLE_HIT

    if (batter in battingStats) {
      battingStats[batter] = battingStats[batter]!!.copy(
        atBat = battingStats[batter]!!.atBat + 1,
        hit = battingStats[batter]!!.hit + 1,
        tripleHit = battingStats[batter]!!.tripleHit + 1
      )
    } else {
      battingStats[batter] = BattingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = batter,
        atBat = 1,
        hit = 1,
        tripleHit = 1,
      )
    }

    if (pitcher in pitchingStats) {
      pitchingStats[pitcher] = pitchingStats[pitcher]!!.copy(
        hit = pitchingStats[pitcher]!!.hit + 1
      )
    } else {
      pitchingStats[pitcher] = PitchingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = pitcher,
        hit = 1,
      )
    }

    if (thirdBaseId != null) {
      score()
      thirdBaseId = null
    }
    if (secondBaseId != null) {
      score()
      secondBaseId = null
    }
    if (firstBaseId != null) {
      score()
      firstBaseId = null
    }
    thirdBaseId = batter
  }

  private fun homeRun() {
    val batter = currentBatter()
    val pitcher = currentPitcher()

    lastResult = Result.HOMERUN

    if (batter in battingStats) {
      battingStats[batter] = battingStats[batter]!!.copy(
        atBat = battingStats[batter]!!.atBat + 1,
        hit = battingStats[batter]!!.hit + 1,
        homeRun = battingStats[batter]!!.homeRun + 1,
      )
    } else {
      battingStats[batter] = BattingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = batter,
        atBat = 1,
        hit = 1,
        homeRun = 1,
      )
    }

    if (pitcher in pitchingStats) {
      pitchingStats[pitcher] = pitchingStats[pitcher]!!.copy(
        hit = pitchingStats[pitcher]!!.hit + 1,
        homeRun = pitchingStats[pitcher]!!.homeRun + 1,
      )
    } else {
      pitchingStats[pitcher] = PitchingStat(
        gameFixtureId = schedule.fixture.id,
        playerId = pitcher,
        hit = 1,
        homeRun = 1,
      )
    }

    var count = 1

    if (thirdBaseId != null) {
      score()
      thirdBaseId = null
      count++
    }
    if (secondBaseId != null) {
      score()
      secondBaseId = null
      count++
    }
    if (firstBaseId != null) {
      score()
      firstBaseId = null
      count++
    }
    score()

    when (half) {
      Half.INNING_TOP -> {
        awayHomeRuns.add(
          HomeRun(
            fixtureId = schedule.fixture.id,
            playerId = batter,
            inning = inning,
            count = count
          )
        )
      }

      Half.INNING_BOTTOM -> {
        homeHomeRuns.add(
          HomeRun(
            fixtureId = schedule.fixture.id,
            playerId = batter,
            inning = inning,
            count = count
          )
        )
      }
    }
  }
}