package com.example.penasim.core.ui.model

import androidx.compose.ui.graphics.Color
import com.example.penasim.features.player.domain.Position

data class DisplayFielder(
  val id: Int,
  val displayName: String,
  val position: Position,
  val number: Int,
  val color: Color
)
