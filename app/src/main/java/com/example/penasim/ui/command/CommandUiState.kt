package com.example.penasim.ui.command

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.League
import com.example.penasim.domain.MainMember
import com.example.penasim.domain.MemberType
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.toShortJa

data class CommandUiState(
    val team: Team = Team(0, "", League.L1),
    val players: List<PlayerInfo> = emptyList(),
    val mainMembers: List<MainMember> = emptyList(),
    val fielderAppointments: List<FielderAppointment> = emptyList(),
    val pitcherAppointments: List<PitcherAppointment> = emptyList(),
    val mainViewSelectedFielderId: Int? = null,
    val selectedFielder: Map<OrderType, Int?> = mapOf(
        OrderType.NORMAL to null,
        OrderType.LEFT to null,
        OrderType.LEFT_DH to null,
        OrderType.DH to null,
    ),
    val selectedPitcherId: Int? = null,
) {
    val mainFielders: List<DisplayFielder>
        get() = mainMembers.filter { it.isFielder && it.memberType == MemberType.MAIN }.map { member ->
            val player = players.find { it.player.id == member.playerId }
            DisplayFielder(
                id = member.playerId,
                displayName = player?.player?.firstName ?: "Unknown Player",
                position = Position.BENCH,
                number = 0,
                color = player?.primaryPosition?.color() ?: Position.OUTFIELDER.color()
            )
        }

    val subFielders: List<DisplayFielder>
        get() = mainMembers.filter { it.isFielder && it.memberType == MemberType.SUB }.map { member ->
            val player = players.find { it.player.id == member.playerId }
            DisplayFielder(
                id = member.playerId,
                displayName = player?.player?.firstName ?: "Unknown Player",
                position = Position.BENCH,
                number = 0,
                color = player?.primaryPosition?.color() ?: Position.OUTFIELDER.color()
            )
        }

    fun getOrderFielderAppointments(orderType: OrderType): List<FielderAppointment>
        = fielderAppointments.filter { it.orderType == orderType }.filter { it.position != Position.BENCH && it.position != Position.SUBSTITUTE }.sortedBy { it.number }

    fun getBenchFielderAppointments(orderType: OrderType): List<FielderAppointment>
        = fielderAppointments.filter { it.orderType == orderType }.filter { it.position == Position.BENCH }.sortedBy { it.number }

    val mainStarterPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.type == PitcherType.STARTER }.sortedBy { it.number }

    val mainRelieverPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.type == PitcherType.RELIEVER }.sortedBy { it.number }

    val mainCloserPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.type == PitcherType.CLOSER }.sortedBy { it.number }

    val subPitcherAppointments: List<PitcherAppointment>
        get() = pitcherAppointments.filter { it.type == PitcherType.SUB }.sortedBy { it.number }

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
                position = it.position,
                number = it.number,
                color = players.find { playerInfo -> playerInfo.player.id == it.playerId }?.primaryPosition?.color() ?: Position.OUTFIELDER.color()
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