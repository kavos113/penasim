package com.example.penasim.features.schedule.ui.calender

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.penasim.TestTheme
import com.example.penasim.core.ui.PenasimTestTags
import com.example.penasim.sampleGames
import com.example.penasim.sampleRankings
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CalendarScreenTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun nextGameButton_invokesCallback() {
    var nextClicks = 0

    composeTestRule.setContent {
      TestTheme {
        CalendarContent(
          uiState = CalendarUiState(
            games = sampleGames(),
            rankings = sampleRankings(),
            currentDay = LocalDate.of(2025, 3, 28)
          ),
          onNextGame = { nextClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithTag(PenasimTestTags.CALENDAR_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PenasimTestTags.CALENDAR_NEXT_GAME_BUTTON).performClick()

    assertEquals(1, nextClicks)
  }
}
