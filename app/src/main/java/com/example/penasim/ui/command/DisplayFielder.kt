package com.example.penasim.ui.command

import androidx.compose.ui.graphics.Color

data class DisplayFielder(
    val displayName: String,
    val position: String,
    val number: Int,
    val isMain: Boolean,
    val color: Color
)