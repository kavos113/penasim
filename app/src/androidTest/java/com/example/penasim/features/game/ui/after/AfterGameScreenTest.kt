package com.example.penasim.features.game.ui.after

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.penasim.TestTheme
import com.example.penasim.core.ui.PenasimTestTags
import com.example.penasim.sampleFielderResults
import com.example.penasim.sampleGames
import com.example.penasim.sampleInningScores
import com.example.penasim.samplePitcherResults
import com.example.penasim.sampleRankings
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class AfterGameScreenTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun afterGame_finishInvokesCallback() {
    var finishClicks = 0

    composeTestRule.setContent {
      TestTheme {
        AfterGameContent(
          afterGameInfo = AfterGameInfo(
            date = LocalDate.of(2025, 3, 28),
            homeTeamName = "Home",
            awayTeamName = "Away",
            homeScores = sampleInningScores(teamId = 0),
            awayScores = sampleInningScores(teamId = 1),
            homePitcherResults = samplePitcherResults(),
            awayPitcherResults = samplePitcherResults(),
            homeFielderResults = sampleFielderResults(),
            awayFielderResults = emptyList(),
            rankings = sampleRankings(),
            games = sampleGames().values.flatten()
          ),
          onClickFinish = { finishClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithTag(PenasimTestTags.AFTER_GAME_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PenasimTestTags.AFTER_GAME_FINISH_BUTTON).performClick()

    assertEquals(1, finishClicks)
  }

  @Test
  fun skippedGame_finishInvokesCallback() {
    var finishClicks = 0

    composeTestRule.setContent {
      TestTheme {
        AfterGameContentWithoutGameResult(
          afterGameInfo = AfterGameInfo(
            date = LocalDate.of(2025, 3, 29),
            rankings = sampleRankings(),
            games = sampleGames(LocalDate.of(2025, 3, 29)).values.flatten()
          ),
          onClickFinish = { finishClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithTag(PenasimTestTags.AFTER_GAME_WITHOUT_RESULT_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PenasimTestTags.AFTER_GAME_FINISH_BUTTON).performClick()

    assertEquals(1, finishClicks)
  }
}
