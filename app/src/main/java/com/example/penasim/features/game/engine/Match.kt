package com.example.penasim.features.game.engine

import com.example.penasim.features.command.domain.TeamPlayers
import com.example.penasim.features.game.application.model.AtBatResultType
import com.example.penasim.features.game.application.model.InGameAtBatResult
import com.example.penasim.features.game.application.model.InGameSnapshot
import com.example.penasim.features.game.application.model.SimulationResult
import com.example.penasim.features.schedule.domain.GameSchedule

private const val MAX_INNINGS = 9

class Match(
  private val schedule: GameSchedule,
  homePlayers: TeamPlayers,
  awayPlayers: TeamPlayers
) {
  private var inning = 1
  private var half = HalfInning.TOP
  private var outs = 0
  private var isFinalized = false

  private var lastResult: InGameAtBatResult = InGameAtBatResult(
    type = AtBatResultType.OUT,
    isHit = false,
    isScored = false
  )
  private val baseState = BaseState()
  private val homeTeamState = TeamState(homePlayers)
  private val awayTeamState = TeamState(awayPlayers)
  private val teamStat = TeamStat(fixtureId = schedule.fixture.id)

  fun play() {
    while (next()) {}
    finishGame()
  }

  fun next(): Boolean {
    if (inning > MAX_INNINGS) {
      return false
    }

    batting()
    return true
  }

  fun snapshot(): InGameSnapshot = InGameSnapshot(
    scores = inningScores(),
    outCount = outs,
    firstBasePlayerId = baseState.firstBaseId,
    secondBasePlayerId = baseState.secondBaseId,
    thirdBasePlayerId = baseState.thirdBaseId,
    lastResult = lastResult,
    isHomeBatting = half == HalfInning.BOTTOM,
    awayBatterState = awayTeamState.batter,
    homeBatterState = homeTeamState.batter,
    awayPitcherState = awayTeamState.pitcher,
    homePitcherState = homeTeamState.pitcher,
  )

  fun simulationResult(): SimulationResult {
    finishGame()
    return SimulationResult(
      gameResult = teamStat.result(),
      inningScores = inningScores(),
      battingStats = battingStats(),
      pitchingStats = pitchingStats(),
      homeRuns = homeRuns(),
      stats = stats()
    )
  }

  private fun finishGame() {
    if (isFinalized) return
    isFinalized = true
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

  private fun currentBatterId() = when (half) {
    HalfInning.TOP -> awayTeamState.batter.playerId
    HalfInning.BOTTOM -> homeTeamState.batter.playerId
  }

  private fun currentPitcherId() = when (half) {
    HalfInning.TOP -> homeTeamState.pitcher.playerId
    HalfInning.BOTTOM -> awayTeamState.pitcher.playerId
  }

  private fun inningScores() = teamStat.inningScores(
    homeTeamId = schedule.homeTeam.id,
    awayTeamId = schedule.awayTeam.id
  )

  private fun battingStats() = teamStat.battingStats.values.toList()
  private fun pitchingStats() = teamStat.pitchingStats.values.toList()
  private fun homeRuns() = teamStat.homeRuns()
  private fun stats() = teamStat.stats.toList()

  private fun batting() {
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
      HalfInning.TOP -> awayTeamState.goNextBatter()
      HalfInning.BOTTOM -> homeTeamState.goNextBatter()
    }
  }

  private fun decreasePitcherStamina() {
    when (half) {
      HalfInning.TOP -> homeTeamState.decreasePitcherStamina()
      HalfInning.BOTTOM -> awayTeamState.decreasePitcherStamina()
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
    val batter = currentBatterId()
    val pitcher = currentPitcherId()
    val resultString = AtBatResultType.OUT.toPlayDescription()

    teamStat.recordStat(
      batterId = batter,
      pitcherId = pitcher,
      inning = inning,
      outCount = outs,
      hitCount = 0,
      earnedRun = 0,
      result = resultString,
    )

    outs++
    lastResult = InGameAtBatResult(
      type = AtBatResultType.OUT,
      isHit = false,
      isScored = false
    )

    teamStat.out(batter, pitcher)

    if (outs >= 3) {
      when (half) {
        HalfInning.TOP -> half = HalfInning.BOTTOM
        HalfInning.BOTTOM -> {
          half = HalfInning.TOP
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

    val resultString = AtBatResultType.SINGLE_HIT.toPlayDescription()
    teamStat.recordStat(
      batterId = batter,
      pitcherId = pitcher,
      inning = inning,
      outCount = outs,
      hitCount = score,
      earnedRun = score,
      result = resultString,
    )

    lastResult = InGameAtBatResult(
      type = AtBatResultType.SINGLE_HIT,
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

    val resultString = AtBatResultType.DOUBLE_HIT.toPlayDescription()
    teamStat.recordStat(
      batterId = batter,
      pitcherId = pitcher,
      inning = inning,
      outCount = outs,
      hitCount = score,
      earnedRun = score,
      result = resultString,
    )

    lastResult = InGameAtBatResult(
      type = AtBatResultType.DOUBLE_HIT,
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

    val resultString = AtBatResultType.TRIPLE_HIT.toPlayDescription()
    teamStat.recordStat(
      batterId = batter,
      pitcherId = pitcher,
      inning = inning,
      outCount = outs,
      hitCount = score,
      earnedRun = score,
      result = resultString,
    )

    lastResult = InGameAtBatResult(
      type = AtBatResultType.TRIPLE_HIT,
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

    val resultString = AtBatResultType.HOMERUN.toPlayDescription()
    teamStat.recordStat(
      batterId = batter,
      pitcherId = pitcher,
      inning = inning,
      outCount = outs,
      hitCount = score,
      earnedRun = score,
      result = resultString,
    )

    lastResult = InGameAtBatResult(
      type = AtBatResultType.HOMERUN,
      isHit = true,
      isScored = true
    )
  }
}
