package com.example.penasim.features.game.application

import com.example.penasim.features.game.application.model.SimulationResult
import com.example.penasim.features.game.domain.GameInfo
import com.example.penasim.features.game.domain.TransactionProvider
import com.example.penasim.features.game.usecase.BattingStatUseCase
import com.example.penasim.features.game.usecase.ExecuteGameUseCase
import com.example.penasim.features.game.usecase.HomeRunUseCase
import com.example.penasim.features.game.usecase.InningScoreUseCase
import com.example.penasim.features.game.usecase.PitchingStatUseCase
import com.example.penasim.features.game.usecase.StatUseCase
import javax.inject.Inject

class GameResultPersister @Inject constructor(
  private val executeGameUseCase: ExecuteGameUseCase,
  private val battingStatUseCase: BattingStatUseCase,
  private val pitchingStatUseCase: PitchingStatUseCase,
  private val inningScoreUseCase: InningScoreUseCase,
  private val homeRunUseCase: HomeRunUseCase,
  private val statUseCase: StatUseCase,
  private val transactionProvider: TransactionProvider
) {
  suspend fun persist(result: SimulationResult): GameInfo {
    return transactionProvider.runInTransaction {
      inningScoreUseCase.insertAll(result.inningScores)
      battingStatUseCase.insertAll(result.battingStats)
      pitchingStatUseCase.insertAll(result.pitchingStats)
      homeRunUseCase.insert(result.homeRuns)
      statUseCase.insertAll(result.stats)

      executeGameUseCase.execute(
        fixtureId = result.gameResult.fixtureId,
        homeScore = result.gameResult.homeScore,
        awayScore = result.gameResult.awayScore
      )
    }
  }
}
