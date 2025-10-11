package com.example.penasim.domain

data class PitchingStat(
    val gameFixtureId: Int,
    val playerId: Int,
    val inningPitched: Int = 0,
    val hit: Int = 0,
    val run: Int = 0,
    val earnedRun: Int = 0,
    val walk: Int = 0,
    val strikeOut: Int = 0,
    val homeRun: Int = 0,
    val win: Boolean = false,
    val lose: Boolean = false,
    val hold: Boolean = false,
    val save: Boolean = false,
    val numberOfPitches: Int = 0, // 登板順
) {
    val displayInningPitched: String
        get() {
            val wholeInnings = inningPitched / 3
            val partialInnings = inningPitched % 3
            return if (partialInnings == 0) {
                wholeInnings.toString()
            } else {
                "$wholeInnings.${partialInnings}"
            }
        }
}
