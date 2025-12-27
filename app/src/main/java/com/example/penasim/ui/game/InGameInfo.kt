package com.example.penasim.ui.game

import com.example.penasim.domain.InningScore
import com.example.penasim.domain.MainMember
import com.example.penasim.domain.MemberType
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.Position
import com.example.penasim.ui.common.DisplayFielder
import com.example.penasim.ui.common.color

data class InGameInfo(
  val homeTeam: InGameTeamInfo = InGameTeamInfo(),
  val awayTeam: InGameTeamInfo = InGameTeamInfo(),
  val outCount: Int = 0,
  val firstBase: DisplayFielder? = null,
  val secondBase: DisplayFielder? = null,
  val thirdBase: DisplayFielder? = null
)

data class InGameTeamInfo(
  val inningScores: List<InningScore> = emptyList(),
  val players: List<PlayerInfo> = emptyList(),
  val mainMembers: List<MainMember> = emptyList(),
  val activePlayerId: Int = 0,
  val activeNumber: Int? = null // activeな打順
) {
  // include pitcher
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

  // include pitcher
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

  val activePlayer: DisplayFielder
    get() {
      val player = players.find { it.player.id == activePlayerId }
      return DisplayFielder(
        id = activePlayerId,
        displayName = player?.player?.firstName ?: "Unknown Player",
        position = Position.BENCH,
        number = 0,
        color = player?.primaryPosition?.color() ?: Position.OUTFIELDER.color()
      )
    }
}