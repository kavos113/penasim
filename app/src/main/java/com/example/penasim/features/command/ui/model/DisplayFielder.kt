package com.example.penasim.ui.common

import androidx.compose.ui.graphics.Color
import com.example.penasim.domain.Position

data class DisplayFielder(
  val id: Int,
  val displayName: String,
  val position: Position,
  val number: Int,
  val color: Color
)