package com.example.penasim.ui.command

import androidx.compose.ui.graphics.Color

data class DisplayFielder(
    val id: Int,
    val displayName: String,
    val position: String,
    val number: Int,
    val color: Color
)