package com.example.penasim.features.home.ui.home

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.penasim.TestTheme
import com.example.penasim.core.ui.PenasimTestTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeMenuTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun gameDay_clicksPrimaryActions() {
    var gameClicks = 0
    var calendarClicks = 0
    var commandClicks = 0

    composeTestRule.setContent {
      TestTheme {
        HomeMenu(
          isGameDay = true,
          onGameClick = { gameClicks++ },
          onCalenderClick = { calendarClicks++ },
          onCommandClick = { commandClicks++ }
        )
      }
    }

    composeTestRule.onNodeWithTag(PenasimTestTags.HOME_GAME_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(PenasimTestTags.HOME_CALENDAR_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(PenasimTestTags.HOME_COMMAND_BUTTON).assertIsDisplayed().performClick()

    assertEquals(1, gameClicks)
    assertEquals(1, calendarClicks)
    assertEquals(1, commandClicks)
  }

  @Test
  fun offDay_showsNextDayInsteadOfGame() {
    var nextDayClicks = 0

    composeTestRule.setContent {
      TestTheme {
        HomeMenu(
          isGameDay = false,
          onNoGameDayClick = { nextDayClicks++ }
        )
      }
    }

    composeTestRule.onAllNodesWithTag(PenasimTestTags.HOME_GAME_BUTTON).assertCountEquals(0)
    composeTestRule.onNodeWithTag(PenasimTestTags.HOME_NEXT_DAY_BUTTON).assertIsDisplayed().performClick()

    assertEquals(1, nextDayClicks)
  }
}
