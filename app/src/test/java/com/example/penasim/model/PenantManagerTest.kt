package com.example.penasim.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class PennantManagerTest {
    private lateinit var pennantManager: PennantManager
    private lateinit var mockGameMasterDao: GameMasterDao
    private lateinit var testScope: CoroutineScope

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mockGameMasterDao = mock(GameMasterDao::class.java)
        testScope = CoroutineScope(UnconfinedTestDispatcher())
        pennantManager = PennantManager(mockGameMasterDao)
    }

    @Test
    fun updateRankings_correctlyCalculateTeamRankings() {
        val games = listOf(
            GameInfo(0, 0, 0, 6, 3, 2),
            GameInfo(0, 1, 2, 8, 3, 2),
            GameInfo(0, 2, 4, 10, 3, 3),
            GameInfo(0, 3, 1, 7, 3, 4),
            GameInfo(0, 4, 3, 9, 3, 5),
            GameInfo(0, 5, 5, 11, 3, 6),
        )

        pennantManager.nextGame(games)

        assertTrue(pennantManager.teamInfo[0].rank == 1 || pennantManager.teamInfo[0].rank == 2)
        assertTrue(pennantManager.teamInfo[2].rank == 1 || pennantManager.teamInfo[2].rank == 2)
        assertTrue(pennantManager.teamInfo[4].rank == 3 || pennantManager.teamInfo[4].rank == 4)
        assertTrue(pennantManager.teamInfo[10].rank == 3 || pennantManager.teamInfo[10].rank == 4)
        assertTrue(pennantManager.teamInfo[6].rank == 5 || pennantManager.teamInfo[6].rank == 6)
        assertTrue(pennantManager.teamInfo[8].rank == 5 || pennantManager.teamInfo[8].rank == 6)
    }

    @Test
    fun updateRankings_multipleGames() {
        val games = listOf(
            GameInfo(0, 0, 0, 6, 3, 2),
            GameInfo(0, 1, 2, 8, 3, 2),
            GameInfo(0, 2, 4, 10, 3, 3),
            GameInfo(0, 3, 1, 7, 3, 4),
            GameInfo(0, 4, 3, 9, 3, 5),
            GameInfo(0, 5, 5, 11, 3, 6)
        )

        pennantManager.nextGame(games)

        val games2 = listOf(
            GameInfo(1, 0, 0, 8, 3, 2),
            GameInfo(1, 1, 2, 4, 2, 3),
            GameInfo(1, 2, 6, 10, 3, 3),
            GameInfo(1, 3, 1, 7, 3, 4),
            GameInfo(1, 4, 3, 9, 3, 5),
            GameInfo(1, 5, 5, 11, 3, 6),
        )

        pennantManager.nextGame(games2)

        assertEquals(1, pennantManager.teamInfo[0].rank) // 2-0-0
        assertEquals(2, pennantManager.teamInfo[4].rank) // 1-0-1
        assertEquals(3, pennantManager.teamInfo[2].rank) // 1-1-0
        assertEquals(4, pennantManager.teamInfo[10].rank)// 0-0-2
        assertEquals(5, pennantManager.teamInfo[6].rank) // 0-1-1
        assertEquals(6, pennantManager.teamInfo[8].rank) // 0-2-0
    }

    @Test
    fun updateRankings_gameBacks() {
        val games = listOf(
            GameInfo(0, 0, 0, 6, 3, 2),
            GameInfo(0, 1, 2, 8, 3, 2),
            GameInfo(0, 2, 4, 10, 3, 3),
            GameInfo(0, 3, 1, 7, 3, 4),
            GameInfo(0, 4, 3, 9, 3, 5),
            GameInfo(0, 5, 5, 11, 3, 6)
        )

        pennantManager.nextGame(games)

        val games2 = listOf(
            GameInfo(1, 0, 0, 8, 3, 2),
            GameInfo(1, 1, 2, 4, 2, 3),
            GameInfo(1, 2, 6, 10, 3, 3),
            GameInfo(1, 3, 1, 7, 3, 4),
            GameInfo(1, 4, 3, 9, 3, 5),
            GameInfo(1, 5, 5, 11, 3, 6),
        )

        pennantManager.nextGame(games2)

        assertEquals(0.0, pennantManager.teamInfo[0].gameBack, 0.001) // 2-0-0
        assertEquals(0.5, pennantManager.teamInfo[4].gameBack, 0.001) // 1-0-1
        assertEquals(1.0, pennantManager.teamInfo[2].gameBack, 0.001) // 1-1-0
        assertEquals(1.0, pennantManager.teamInfo[10].gameBack, 0.001)// 0-0-2
        assertEquals(1.5, pennantManager.teamInfo[6].gameBack, 0.001) // 0-1-1
        assertEquals(2.0, pennantManager.teamInfo[8].gameBack, 0.001) // 0-2-0
    }

    @Test
    fun updateRankings_winCounts() {
        val games = listOf(
            GameInfo(0, 0, 0, 6, 3, 2),
            GameInfo(0, 1, 2, 8, 3, 2),
            GameInfo(0, 2, 4, 10, 3, 3),
            GameInfo(0, 3, 1, 7, 3, 4),
            GameInfo(0, 4, 3, 9, 3, 5),
            GameInfo(0, 5, 5, 11, 3, 6)
        )

        pennantManager.nextGame(games)

        val games2 = listOf(
            GameInfo(1, 0, 0, 8, 3, 2),
            GameInfo(1, 1, 2, 4, 2, 3),
            GameInfo(1, 2, 6, 10, 3, 3),
            GameInfo(1, 3, 1, 7, 3, 4),
            GameInfo(1, 4, 3, 9, 3, 5),
            GameInfo(1, 5, 5, 11, 3, 6),
        )

        pennantManager.nextGame(games2)

        assertEquals(2, pennantManager.teamInfo[0].wins) // 2-0-0
        assertEquals(0, pennantManager.teamInfo[0].losses)
        assertEquals(0, pennantManager.teamInfo[0].draws)
        assertEquals(1, pennantManager.teamInfo[4].wins) // 1-0-1
        assertEquals(0, pennantManager.teamInfo[4].losses)
        assertEquals(1, pennantManager.teamInfo[4].draws)
        assertEquals(1, pennantManager.teamInfo[2].wins) // 1-1-0
        assertEquals(1, pennantManager.teamInfo[2].losses)
        assertEquals(0, pennantManager.teamInfo[2].draws)
        assertEquals(0, pennantManager.teamInfo[10].wins)// 0-0-2
        assertEquals(0, pennantManager.teamInfo[10].losses)
        assertEquals(2, pennantManager.teamInfo[10].draws)
        assertEquals(0, pennantManager.teamInfo[6].wins) // 0-1-1
        assertEquals(1, pennantManager.teamInfo[6].losses)
        assertEquals(1, pennantManager.teamInfo[6].draws)
        assertEquals(0, pennantManager.teamInfo[8].wins) // 0-2-0
        assertEquals(2, pennantManager.teamInfo[8].losses)
        assertEquals(0, pennantManager.teamInfo[8].draws)
    }

}