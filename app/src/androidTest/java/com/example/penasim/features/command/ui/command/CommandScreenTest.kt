package com.example.penasim.features.command.ui.command

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.penasim.TestTheme
import com.example.penasim.core.ui.PenasimTestTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CommandScreenTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun tabs_switchBetweenPages_andCallDispose() {
    var disposeCalls = 0

    composeTestRule.setContent {
      TestTheme {
        CommandContent(
          onDispose = { disposeCalls++ },
          fielderContent = { Text("fielder content") },
          pitcherContent = { Text("pitcher content") }
        )
      }
    }

    composeTestRule.onNodeWithTag(PenasimTestTags.COMMAND_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PenasimTestTags.COMMAND_FIELDER_TAB).assertIsDisplayed()
    composeTestRule.onNodeWithText("fielder content").assertIsDisplayed()

    composeTestRule.onNodeWithTag(PenasimTestTags.COMMAND_PITCHER_TAB).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("pitcher content").assertIsDisplayed()
    assertEquals(0, disposeCalls)
  }
}
