package com.example.penasim.features.game.application.model

data class InGameAtBatResult(
  val type: AtBatResultType,
  val isHit: Boolean,
  val isScored: Boolean
)
