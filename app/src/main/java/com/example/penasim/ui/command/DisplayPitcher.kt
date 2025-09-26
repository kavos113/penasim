package com.example.penasim.ui.command

import com.example.penasim.domain.PitcherType

data class DisplayPitcher(
    val id: Int,
    val displayName: String,
    val type: PitcherType,
    val number: Int,
)
