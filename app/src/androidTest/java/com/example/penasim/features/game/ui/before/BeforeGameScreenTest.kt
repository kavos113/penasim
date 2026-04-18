package com.example.penasim.features.game.ui.before

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.penasim.TestTheme
import com.example.penasim.core.ui.PenasimTestTags
import com.example.penasim.sampleDisplayFielders
import com.example.penasim.sampleTeamStanding
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class BeforeGameScreenTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun buttons_triggerExpectedCallbacks() {
    var startClicks = 0
    var skipClicks = 0

    composeTestRule.setContent {
      TestTheme {
        BeforeGameContent(
          beforeGameInfo = BeforeGameInfo(
            date = LocalDate.of(2025, 3, 28),
            homeTeam = sampleTeamStanding(teamId = 0, rank = 1),
            awayTeam = sampleTeamStanding(teamId = 1, rank = 2),
            homeStartingPlayers = sampleDisplayFielders(),
            awayStartingPlayers = sampleDisplayFielders(teamOffset = 20)
          ),
          onClickStartGame = { startClicks++ },
          onClickSkipGame = { skipClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithTag(PenasimTestTags.BEFORE_GAME_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PenasimTestTags.BEFORE_GAME_START_BUTTON).performClick()
    composeTestRule.onNodeWithTag(PenasimTestTags.BEFORE_GAME_SKIP_BUTTON).performClick()

    assertEquals(1, startClicks)
    assertEquals(1, skipClicks)
  }
}
