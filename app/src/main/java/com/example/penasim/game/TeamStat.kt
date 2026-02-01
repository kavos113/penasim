package com.example.penasim.game

import com.example.penasim.domain.BattingStat
import com.example.penasim.domain.GameResult
import com.example.penasim.domain.HomeRun
import com.example.penasim.domain.InningScore
import com.example.penasim.domain.PitchingStat

data class TeamStat(
  val fixtureId: Int,
  var homeScore: Int = 0,
  var awayScore: Int = 0,
  val homeScores: MutableList<Int> = mutableListOf(),
  val awayScores: MutableList<Int> = mutableListOf(0),
  val battingStats: MutableMap<Int, BattingStat> = mutableMapOf(),
  val pitchingStats: MutableMap<Int, PitchingStat> = mutableMapOf(),
  val homeHomeRuns: MutableList<HomeRun> = mutableListOf(),
  val awayHomeRuns: MutableList<HomeRun> = mutableListOf()
) {

  fun result(): GameResult = GameResult(
    fixtureId = fixtureId,
    homeScore = homeScore,
    awayScore = awayScore
  )

  fun inningScores(homeTeamId: Int, awayTeamId: Int): List<InningScore> {
    val scores = mutableListOf<InningScore>()
    for (i in 1..homeScores.size) {
      scores.add(
        InningScore(
          fixtureId = fixtureId,
          teamId = homeTeamId,
          inning = i,
          score = homeScores[i - 1]
        )
      )
    }
    for (i in 1..awayScores.size) {
      scores.add(
        InningScore(
          fixtureId = fixtureId,
          teamId = awayTeamId,
          inning = i,
          score = awayScores[i - 1]
        )
      )
    }
    return scores
  }

  fun homeRuns(): List<HomeRun> = homeHomeRuns + awayHomeRuns

  fun newInning(half: Half) {
    when (half) {
      Half.INNING_TOP -> awayScores.add(0)
      Half.INNING_BOTTOM -> homeScores.add(0)
    }
  }

  fun finalize(homePitcherId: Int, awayPitcherId: Int) {
    awayScores.removeAt(awayScores.lastIndex)

    if (homeScore > awayScore) {
      pitchingStats[homePitcherId] = pitchingStats[homePitcherId]!!.copy(
        win = true,
      )
      pitchingStats[awayPitcherId] = pitchingStats[awayPitcherId]!!.copy(
        lose = true,
      )
    } else {
      pitchingStats[homePitcherId] = pitchingStats[homePitcherId]!!.copy(
        lose = true,
      )
      pitchingStats[awayPitcherId] = pitchingStats[awayPitcherId]!!.copy(
        win = true,
      )
    }
  }

  fun score(batterId: Int, pitcherId: Int, half: Half, count: Int) {
    if (batterId in battingStats) {
      battingStats[batterId] = battingStats[batterId]!!.copy(
        rbi = battingStats[batterId]!!.rbi + count,
      )
    } else {
      battingStats[batterId] = BattingStat(
        gameFixtureId = fixtureId,
        playerId = batterId,
        rbi = count
      )
    }

    if (pitcherId in pitchingStats) {
      pitchingStats[pitcherId] = pitchingStats[pitcherId]!!.copy(
        run = pitchingStats[pitcherId]!!.run + count,
        earnedRun = pitchingStats[pitcherId]!!.earnedRun + count,
      )
    } else {
      pitchingStats[pitcherId] = PitchingStat(
        gameFixtureId = fixtureId,
        playerId = pitcherId,
        run = count,
        earnedRun = count
      )
    }

    when (half) {
      Half.INNING_TOP -> {
        awayScore += count
        awayScores[awayScores.lastIndex] += count
      }

      Half.INNING_BOTTOM -> {
        homeScore += count
        homeScores[homeScores.lastIndex] += count
      }
    }
  }

  fun out(batterId: Int, pitcherId: Int) {
    if (batterId in battingStats) {
      battingStats[batterId] = battingStats[batterId]!!.copy(
        atBat = battingStats[batterId]!!.atBat + 1,
      )
    } else {
      battingStats[batterId] = BattingStat(
        gameFixtureId = fixtureId,
        playerId = batterId,
        atBat = 1
      )
    }

    if (pitcherId in pitchingStats) {
      pitchingStats[pitcherId] = pitchingStats[pitcherId]!!.copy(
        inningPitched = pitchingStats[pitcherId]!!.inningPitched,
      )
    } else {
      pitchingStats[pitcherId] = PitchingStat(
        gameFixtureId = fixtureId,
        playerId = pitcherId,
        inningPitched = 1,
      )
    }
  }

  fun single(batterId: Int, pitcherId: Int) {
    if (batterId in battingStats) {
      battingStats[batterId] = battingStats[batterId]!!.copy(
        atBat = battingStats[batterId]!!.atBat + 1,
        hit = battingStats[batterId]!!.hit + 1,
      )
    } else {
      battingStats[batterId] = BattingStat(
        gameFixtureId = fixtureId,
        playerId = batterId,
        atBat = 1,
        hit = 1
      )
    }

    if (pitcherId in pitchingStats) {
      pitchingStats[pitcherId] = pitchingStats[pitcherId]!!.copy(
        hit = pitchingStats[pitcherId]!!.hit + 1
      )
    } else {
      pitchingStats[pitcherId] = PitchingStat(
        gameFixtureId = fixtureId,
        playerId = pitcherId,
        hit = 1
      )
    }
  }

  fun double(batterId: Int, pitcherId: Int) {
    if (batterId in battingStats) {
      battingStats[batterId] = battingStats[batterId]!!.copy(
        atBat = battingStats[batterId]!!.atBat + 1,
        hit = battingStats[batterId]!!.hit + 1,
        doubleHit = battingStats[batterId]!!.doubleHit + 1
      )
    } else {
      battingStats[batterId] = BattingStat(
        gameFixtureId = fixtureId,
        playerId = batterId,
        atBat = 1,
        hit = 1,
        doubleHit = 1
      )
    }

    if (pitcherId in pitchingStats) {
      pitchingStats[pitcherId] = pitchingStats[pitcherId]!!.copy(
        hit = pitchingStats[pitcherId]!!.hit + 1
      )
    } else {
      pitchingStats[pitcherId] = PitchingStat(
        gameFixtureId = fixtureId,
        playerId = pitcherId,
        hit = 1
      )
    }
  }

  fun triple(batterId: Int, pitcherId: Int) {
    if (batterId in battingStats) {
      battingStats[batterId] = battingStats[batterId]!!.copy(
        atBat = battingStats[batterId]!!.atBat + 1,
        hit = battingStats[batterId]!!.hit + 1,
        tripleHit = battingStats[batterId]!!.tripleHit + 1
      )
    } else {
      battingStats[batterId] = BattingStat(
        gameFixtureId = fixtureId,
        playerId = batterId,
        atBat = 1,
        hit = 1,
        tripleHit = 1
      )
    }

    if (pitcherId in pitchingStats) {
      pitchingStats[pitcherId] = pitchingStats[pitcherId]!!.copy(
        hit = pitchingStats[pitcherId]!!.hit + 1
      )
    } else {
      pitchingStats[pitcherId] = PitchingStat(
        gameFixtureId = fixtureId,
        playerId = pitcherId,
        hit = 1
      )
    }
  }

  fun homeRun(batterId: Int, pitcherId: Int, half: Half, inning: Int, count: Int) {
    if (batterId in battingStats) {
      battingStats[batterId] = battingStats[batterId]!!.copy(
        atBat = battingStats[batterId]!!.atBat + 1,
        hit = battingStats[batterId]!!.hit + 1,
        homeRun = battingStats[batterId]!!.homeRun + 1
      )
    } else {
      battingStats[batterId] = BattingStat(
        gameFixtureId = fixtureId,
        playerId = batterId,
        atBat = 1,
        hit = 1,
        homeRun = 1
      )
    }

    if (pitcherId in pitchingStats) {
      pitchingStats[pitcherId] = pitchingStats[pitcherId]!!.copy(
        hit = pitchingStats[pitcherId]!!.hit + 1,
        homeRun = pitchingStats[pitcherId]!!.homeRun + 1
      )
    } else {
      pitchingStats[pitcherId] = PitchingStat(
        gameFixtureId = fixtureId,
        playerId = pitcherId,
        hit = 1,
        homeRun = 1
      )
    }

    val homeRun = HomeRun(
      fixtureId = fixtureId,
      playerId = batterId,
      inning = inning,
      count = count
    )

    when (half) {
      Half.INNING_TOP -> awayHomeRuns.add(homeRun)
      Half.INNING_BOTTOM -> homeHomeRuns.add(homeRun)
    }
  }
}
