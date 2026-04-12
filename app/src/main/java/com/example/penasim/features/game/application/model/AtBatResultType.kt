package com.example.penasim.features.game.application.model

enum class AtBatResultType {
  OUT,
  SINGLE_HIT,
  DOUBLE_HIT,
  TRIPLE_HIT,
  HOMERUN;

  fun toDisplayText(): String = when (this) {
    OUT -> {
      val random = (1..2).random()
      when (random) {
        1 -> "ゴ"
        else -> "飛"
      }
    }
    SINGLE_HIT -> "安"
    DOUBLE_HIT -> "二"
    TRIPLE_HIT -> "三"
    HOMERUN -> "本"
  }

  fun toPlayDescription(): String {
    return if (this == HOMERUN) {
      randomOutfieldPosition() + "本"
    } else {
      randomPosition() + toDisplayText()
    }
  }

  private fun randomPosition(): String {
    val random = (1..100).random()
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

  private fun randomOutfieldPosition(): String {
    val random = (1..3).random()
    return when (random) {
      1 -> "左"
      2 -> "中"
      else -> "右"
    }
  }
}
