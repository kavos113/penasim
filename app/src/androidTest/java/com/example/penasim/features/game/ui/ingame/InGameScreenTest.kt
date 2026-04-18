package com.example.penasim.features.game.ui.ingame

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.penasim.TestTheme
import com.example.penasim.core.ui.PenasimTestTags
import com.example.penasim.sampleDisplayFielders
import com.example.penasim.sampleInningScores
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class InGameScreenTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun footerActions_areClickable() {
    var nextClicks = 0
    var skipClicks = 0
    var changeClicks = 0

    composeTestRule.setContent {
      TestTheme {
        InGameContent(
          inGameInfo = InGameInfo(
            homeTeam = InGameTeamInfo(
              name = "Home",
              inningScores = sampleInningScores(teamId = 0),
              players = sampleDisplayFielders(),
              activePlayerId = 1,
              activeNumber = 2
            ),
            awayTeam = InGameTeamInfo(
              name = "Away",
              inningScores = sampleInningScores(teamId = 1),
              players = sampleDisplayFielders(teamOffset = 20),
              activePlayerId = 21,
              activeNumber = 1
            )
          ),
          onClickNext = { nextClicks++ },
          onClickSkip = { skipClicks++ },
          onClickChange = { changeClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithTag(PenasimTestTags.IN_GAME_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PenasimTestTags.IN_GAME_FAST_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PenasimTestTags.IN_GAME_NEXT_BUTTON).performClick()
    composeTestRule.onNodeWithTag(PenasimTestTags.IN_GAME_SKIP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(PenasimTestTags.IN_GAME_CHANGE_BUTTON).performClick()

    assertEquals(1, nextClicks)
    assertEquals(1, skipClicks)
    assertEquals(1, changeClicks)
  }
}
