package com.example.penasim.features.game.ui.ingame

import com.example.penasim.features.game.application.model.InGameSnapshot
import com.example.penasim.features.schedule.domain.GameSchedule
import javax.inject.Inject

class InGameInfoAssembler @Inject constructor() {
  fun applySnapshot(
    currentState: InGameInfo,
    snapshot: InGameSnapshot,
    schedule: GameSchedule
  ): InGameInfo {
    val homeScores = snapshot.scores.filter { it.teamId == schedule.homeTeam.id }
    val awayScores = snapshot.scores.filter { it.teamId == schedule.awayTeam.id }

    return currentState.copy(
      homeTeam = currentState.homeTeam.copy(
        inningScores = homeScores,
        activePlayerId = if (snapshot.isHomeBatting) {
          snapshot.homeBatterState.playerId
        } else {
          snapshot.homePitcherState.playerId
        },
        activeNumber = if (snapshot.isHomeBatting) {
          snapshot.homeBatterState.battingOrder
        } else {
          null
        }
      ),
      awayTeam = currentState.awayTeam.copy(
        inningScores = awayScores,
        activePlayerId = if (snapshot.isHomeBatting) {
          snapshot.awayPitcherState.playerId
        } else {
          snapshot.awayBatterState.playerId
        },
        activeNumber = if (snapshot.isHomeBatting) {
          null
        } else {
          snapshot.awayBatterState.battingOrder
        }
      ),
      outCount = snapshot.outCount,
      firstBase = snapshot.firstBasePlayerId?.let { currentState.getByPlayerId(it) },
      secondBase = snapshot.secondBasePlayerId?.let { currentState.getByPlayerId(it) },
      thirdBase = snapshot.thirdBasePlayerId?.let { currentState.getByPlayerId(it) },
      lastResult = snapshot.lastResult
    )
  }
}
