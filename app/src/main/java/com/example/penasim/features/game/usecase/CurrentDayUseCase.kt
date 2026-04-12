package com.example.penasim.features.game.usecase

import com.example.penasim.const.Constants
import java.time.LocalDate
import javax.inject.Inject

class CurrentDayUseCase @Inject constructor(
  private val gameInfoUseCase: GameInfoUseCase
) {
  suspend fun getCurrentDay(): LocalDate {
    val gameInfos = gameInfoUseCase.getAll()
    return gameInfos.maxOfOrNull { it.fixture.date }?.plusDays(1)
      ?: Constants.START
  }
}
