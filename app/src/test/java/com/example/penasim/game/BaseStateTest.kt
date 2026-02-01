package com.example.penasim.game

import org.junit.Test
import kotlin.test.assertEquals

class BaseStateTest {

    @Test
    fun single_emptyBases_noScore() {
        val state = BaseState()

        val scored = state.single(batter = 10)

        assertEquals(0, scored)
    }

    @Test
    fun single_thenSingle_movesRunner_noScore() {
        val state = BaseState()

        state.single(batter = 1) // 1st occupied
        val scored = state.single(batter = 2) // runners: 1->2, 2->1

        assertEquals(0, scored)
    }

    @Test
    fun double_withRunnersOnFirstAndSecond_scoresOne() {
        val state = BaseState()

        state.single(batter = 1) // 1st
        state.single(batter = 2) // 1st, 2nd

        val scored = state.double(batter = 3) // 2nd scores, 1st -> 3rd, batter -> 2nd

        assertEquals(1, scored)
    }

    @Test
    fun double_withOnlyRunnerOnFirst_noScore_runnerAdvancesToThird() {
        val state = BaseState()

        state.single(batter = 1) // 1st only

        val scored = state.double(batter = 2) // 1st -> 3rd, batter -> 2nd, no score

        assertEquals(0, scored)
    }

    @Test
    fun triple_withBasesLoaded_scoresThree() {
        val state = BaseState()

        // Make bases loaded by three consecutive singles
        state.single(batter = 1) // 1st
        state.single(batter = 2) // 1st,2nd
        state.single(batter = 3) // 1st,2nd,3rd

        val scored = state.triple(batter = 4) // all three runners score

        assertEquals(3, scored)
    }

    @Test
    fun homeRun_withBasesLoaded_scoresFour() {
        val state = BaseState()

        // Load bases
        state.single(batter = 1)
        state.single(batter = 2)
        state.single(batter = 3)

        val scored = state.homeRun()

        assertEquals(4, scored)
    }

    @Test
    fun homeRun_withEmptyBases_scoresOne() {
        val state = BaseState()

        val scored = state.homeRun()

        assertEquals(1, scored)
    }

    @Test
    fun reset_clearsRunners() {
        val state = BaseState()

        // Load some runners
        state.single(batter = 1)
        state.single(batter = 2)

        // Reset the bases
        state.reset()

        // Now a home run should be solo
        val scored = state.homeRun()

        assertEquals(1, scored)
    }
}
