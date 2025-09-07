package com.example.penasim.ui.command

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.League
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.isStarting
import com.example.penasim.domain.toShortJa

data class CommandUiState(
    val team: Team = Team(0, "", League.L1),
    val players: List<PlayerInfo> = emptyList(),
    val fielderAppointments: List<FielderAppointment> = emptyList(),
    val pitcherAppointments: List<PitcherAppointment> = emptyList(),
    val selectedFielderId: Int? = null,
    val selectedPitcherId: Int? = null,
    val currentFielderOrderType: OrderType = OrderType.NORMAL,
) {

    val orderFielderAppointments: List<FielderAppointment>
        get() = fielderAppointments.filter { it.orderType == currentFielderOrderType }.filter { it.isStarting() }.sortedBy { it.number }

    val benchFielderAppointments: List<FielderAppointment>
        get() = fielderAppointments.filter { it.orderType == currentFielderOrderType }.filter { it.position == Position.BENCH }.sortedBy { it.number }

    val subFielderAppointments: List<FielderAppointment>
        get() = fielderAppointments.filter { it.orderType == currentFielderOrderType }.filter { it.position == Position.SUBSTITUTE }.sortedBy { it.number }

    val currentFielderAppointments: List<FielderAppointment>
        get() = fielderAppointments.filter { it.orderType == currentFielderOrderType }.sortedBy { it.number }

    val mainStarterPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.isMain }.filter { it.type == PitcherType.STARTER }.sortedBy { it.number }

    val mainRelieverPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.isMain }.filter { it.type == PitcherType.RELIEVER }.sortedBy { it.number }

    val mainCloserPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.isMain }.filter { it.type == PitcherType.CLOSER }.sortedBy { it.number }

    val subPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { !it.isMain }.sortedBy { it.number }

    fun getFielderDisplayName(fielderAppointment: FielderAppointment): String {
        val playerInfo = players.find { it.player.id == fielderAppointment.playerId }
        return playerInfo?.player?.firstName ?: "Unknown Player"
    }

    fun getPitcherDisplayName(pitcherAppointment: PitcherAppointment): String {
        val playerInfo = players.find { it.player.id == pitcherAppointment.playerId }
        return playerInfo?.player?.firstName ?: "Unknown Player"
    }

    fun getDisplayFielders(fielderAppointments: List<FielderAppointment>): List<DisplayFielder> {
        return fielderAppointments.map {
            DisplayFielder(
                id = it.playerId,
                displayName = getFielderDisplayName(it),
                position = it.position.toShortJa(),
                number = it.number,
                color = it.position.color()
            )
        }
    }

    fun getDisplayPitchers(pitcherAppointments: List<PitcherAppointment>): List<DisplayPitcher> {
        return pitcherAppointments.map {
            DisplayPitcher(
                id = it.playerId,
                displayName = getPitcherDisplayName(it),
                type = it.type,
                number = it.number,
                isMain = it.isMain,
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