package com.example.penasim.features.command.ui.command

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.penasim.R
import com.example.penasim.core.navigation.NavigationDestination
import com.example.penasim.core.ui.PenasimTestTags
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CommandDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandScreen(
  modifier: Modifier = Modifier,
  commandViewModel: CommandViewModel = hiltViewModel(),
  teamId: Int
) {
  LaunchedEffect(teamId) {
    commandViewModel.setTeamId(teamId)
  }

  CommandContent(
    modifier = modifier,
    onDispose = { commandViewModel.save() },
    fielderContent = {
      FielderScreen(
        modifier = Modifier.fillMaxSize(),
        commandViewModel = commandViewModel
      )
    },
    pitcherContent = {
      PitcherScreen(
        modifier = Modifier.fillMaxSize(),
        commandViewModel = commandViewModel
      )
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommandContent(
  modifier: Modifier = Modifier,
  onDispose: () -> Unit = {},
  fielderContent: @Composable () -> Unit,
  pitcherContent: @Composable () -> Unit,
) {
  val tabs = listOf(R.string.fielder, R.string.pitcher)
  val selectedTabIndex = rememberPagerState { tabs.size }
  val tabScope = rememberCoroutineScope()

  Scaffold(
    topBar = {
      PrimaryTabRow(
        selectedTabIndex = selectedTabIndex.currentPage,
        modifier = modifier.testTag(PenasimTestTags.COMMAND_SCREEN),
      ) {
        tabs.forEachIndexed { index, titleResId ->
          Tab(
            selected = selectedTabIndex.currentPage == index,
            onClick = {
              tabScope.launch {
                selectedTabIndex.animateScrollToPage(index)
              }
            },
            modifier = Modifier.testTag(
              if (index == 0) {
                PenasimTestTags.COMMAND_FIELDER_TAB
              } else {
                PenasimTestTags.COMMAND_PITCHER_TAB
              }
            ),
            text = {
              Text(stringResource(titleResId))
            }
          )
        }
      }
    }
  ) { innerPadding ->
    HorizontalPager(
      state = selectedTabIndex,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) { page ->
      when (page) {
        0 -> fielderContent()
        1 -> pitcherContent()
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      onDispose()
    }
  }
}
