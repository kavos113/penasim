package com.example.penasim.ui.command

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.League
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.Team

data class CommandUiState(
    val team: Team = Team(0, "", League.L1),
    val players: List<PlayerInfo> = emptyList(),
    val fielderAppointments: List<FielderAppointment> = emptyList(),
    val pitcherAppointments: List<PitcherAppointment> = emptyList(),
)
