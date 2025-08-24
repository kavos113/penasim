package com.example.penasim.ui.command

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.League
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.Team
import com.example.penasim.domain.toShortJa

data class CommandUiState(
    val team: Team = Team(0, "", League.L1),
    val players: List<PlayerInfo> = emptyList(),
    val fielderAppointments: List<FielderAppointment> = emptyList(),
    val pitcherAppointments: List<PitcherAppointment> = emptyList(),
    val selectedFielderId: Int? = null,
    val selectedPitcherId: Int? = null,
) {
    val orderFielderAppointments: List<FielderAppointment>
        get() = fielderAppointments.filter { it.isMain }.sortedBy { it.number }.take(9)

    val benchFielderAppointments: List<FielderAppointment>
        get() = fielderAppointments.filter { it.isMain }.sortedBy { it.number }.drop(9)

    val mainPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.isMain }.sortedBy { it.number }

    val subFielderAppointments: List<FielderAppointment>
        get() = fielderAppointments.filter { !it.isMain }.sortedBy { it.number }

    val subPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { !it.isMain }.sortedBy { it.number }

    fun getPlayerDisplayName(fielderAppointment: FielderAppointment): String {
        val playerInfo = players.find { it.player.id == fielderAppointment.playerId }
        return playerInfo?.player?.firstName ?: "Unknown Player"
    }

    fun getDisplayFielders(fielderAppointments: List<FielderAppointment>): List<DisplayFielder> {
        return fielderAppointments.map {
            DisplayFielder(
                id = it.playerId,
                displayName = getPlayerDisplayName(it),
                position = it.position.toShortJa(),
                number = it.number,
                isMain = it.isMain,
                color = it.position.color()
            )
        }
    }

    fun getDisplayPlayerDetails(): List<DisplayPlayerDetail> {
        return players.map { playerInfo ->
            val color = playerInfo.primaryPosition.color()
            DisplayPlayerDetail(
                player = playerInfo.player,
                positions = playerInfo.positions,
                color = color
            )
        }.sortedBy { it.player.id }
    }

    fun getDisplayPlayerDetail(playerId: Int): DisplayPlayerDetail? {
        val playerInfo = players.find { it.player.id == playerId } ?: return null
        val color = playerInfo.primaryPosition.color()
        println("PlayerID: $playerId, Info: $playerInfo, Color: $color")
        return DisplayPlayerDetail(
            player = playerInfo.player,
            positions = playerInfo.positions,
            color = color
        )
    }
}