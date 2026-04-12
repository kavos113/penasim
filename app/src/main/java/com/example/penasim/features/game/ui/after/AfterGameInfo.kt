package com.example.penasim.features.game.ui.after

import com.example.penasim.const.Constants
import com.example.penasim.features.game.domain.InningScore
import com.example.penasim.features.game.ui.common.FielderResult
import com.example.penasim.features.game.ui.common.PitcherResult
import com.example.penasim.features.schedule.ui.model.GameUiInfo
import com.example.penasim.features.standing.ui.model.RankingUiInfo
import java.time.LocalDate

data class AfterGameInfo(
  val date: LocalDate = Constants.START,
  val isRunning: Boolean = false,
  val homeTeamName: String = "",
  val awayTeamName: String = "",
  val homeScores: List<InningScore> = emptyList(),
  val awayScores: List<InningScore> = emptyList(),
  val homePitcherResults: List<PitcherResult> = emptyList(),
  val awayPitcherResults: List<PitcherResult> = emptyList(),
  val homeFielderResults: List<FielderResult> = emptyList(),
  val awayFielderResults: List<FielderResult> = emptyList(),
  val rankings: List<RankingUiInfo> = emptyList(),
  val games: List<GameUiInfo> = emptyList(),
)
