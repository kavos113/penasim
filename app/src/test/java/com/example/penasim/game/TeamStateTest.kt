package com.example.penasim.game

import com.example.penasim.domain.FielderAppointment
import com.example.penasim.domain.OrderType
import com.example.penasim.domain.Player
import com.example.penasim.domain.PlayerInfo
import com.example.penasim.domain.PlayerPosition
import com.example.penasim.domain.PitcherAppointment
import com.example.penasim.domain.PitcherType
import com.example.penasim.domain.Position
import com.example.penasim.domain.Team
import com.example.penasim.domain.TeamPlayers
import com.example.penasim.domain.TotalBattingStats
import com.example.penasim.domain.TotalPitchingStats
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TeamStateTest {

    private fun player(
        id: Int,
        stamina: Int = 10,
        teamId: Int = 1,
    ): Player = Player(
        id = id,
        firstName = "P$id",
        lastName = "L$id",
        teamId = teamId,
        meet = 50,
        power = 50,
        speed = 50,
        throwing = 50,
        defense = 50,
        catching = 50,
        ballSpeed = 50,
        control = 50,
        stamina = stamina,
        starter = 50,
        reliever = 50,
    )

    private fun playerInfo(
        id: Int,
        position: Position,
        stamina: Int = 10,
        teamId: Int = 1,
    ): PlayerInfo = PlayerInfo(
        player = player(id, stamina, teamId),
        positions = listOf(PlayerPosition(playerId = id, position = position, defense = 50)),
        team = Team(id = teamId, name = "T$teamId"),
        battingStat = TotalBattingStats(playerId = id),
        pitchingStat = TotalPitchingStats(playerId = id)
    )

    private fun sampleTeamPlayers(): TeamPlayers {
        val team = Team(id = 1, name = "Home")
        val pitcherId = 100
        val fielders = (1..9).map { order ->
            // playerId: order (1..9) for simplicity
            playerInfo(id = order, position = when (order) {
                1 -> Position.CATCHER
                2 -> Position.FIRST_BASEMAN
                3 -> Position.SECOND_BASEMAN
                4 -> Position.THIRD_BASEMAN
                5 -> Position.SHORTSTOP
                6 -> Position.LEFT_FIELDER
                7 -> Position.CENTER_FIELDER
                8 -> Position.RIGHT_FIELDER
                else -> Position.DH
            })
        }
        val players = fielders + playerInfo(id = pitcherId, position = Position.PITCHER, stamina = 20)

        val pitcherAppointments = listOf(
            PitcherAppointment(teamId = team.id, playerId = pitcherId, type = PitcherType.STARTER, number = 1)
        )
        val fielderAppointments = (1..9).map { order ->
            FielderAppointment(teamId = team.id, playerId = order, position = fielders[order - 1].primaryPosition, number = order, orderType = OrderType.NORMAL)
        }
        return TeamPlayers(team = team, players = players, pitcherAppointments = pitcherAppointments, fielderAppointments = fielderAppointments)
    }

    @Test
    fun init_setsStartingPitcherAndLeadOffBatter() {
        val teamPlayers = sampleTeamPlayers()

        val state = TeamState(players = teamPlayers)

        assertEquals(100, state.pitcher.playerId)
        assertEquals(20, state.pitcher.stamina)
        assertEquals(1, state.batter.battingOrder)
        assertEquals(1, state.batter.playerId)
    }

    @Test
    fun goNextBatter_incrementsOrder_andWrapsFrom9To1() {
        val teamPlayers = sampleTeamPlayers()
        val state = TeamState(players = teamPlayers)

        // move to 2nd batter
        state.goNextBatter()
        assertEquals(2, state.batter.battingOrder)
        assertEquals(2, state.batter.playerId)

        // move until 9th
        repeat(7) { state.goNextBatter() }
        assertEquals(9, state.batter.battingOrder)
        assertEquals(9, state.batter.playerId)

        // wrap to 1st
        state.goNextBatter()
        assertEquals(1, state.batter.battingOrder)
        assertEquals(1, state.batter.playerId)
    }

    @Test
    fun decreasePitcherStamina_reducesBy2or3() {
        val teamPlayers = sampleTeamPlayers()
        val state = TeamState(players = teamPlayers)
        val before = state.pitcher.stamina

        state.decreasePitcherStamina()

        val after = state.pitcher.stamina
        assertTrue(after == before - 2 || after == before - 3, "stamina should decrease by 2 or 3")
    }
}
