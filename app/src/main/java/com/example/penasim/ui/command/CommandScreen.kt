package com.example.penasim.ui.command

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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination
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
  val tabs = listOf(R.string.fielder, R.string.pitcher)

  val selectedTabIndex = rememberPagerState { tabs.size }
  val tabScope = rememberCoroutineScope()

  LaunchedEffect(teamId) {
    commandViewModel.setTeamId(teamId)
  }

  Scaffold(
    topBar = {
      PrimaryTabRow(
        selectedTabIndex = selectedTabIndex.currentPage,
        modifier = modifier,
      ) {
        tabs.forEachIndexed { index, titleResId ->
          Tab(
            selected = selectedTabIndex.currentPage == index,
            onClick = {
              tabScope.launch {
                selectedTabIndex.animateScrollToPage(index)
              }
            },
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
        0 -> FielderScreen(
          modifier = Modifier.fillMaxSize(),
          commandViewModel = commandViewModel
        )

        1 -> PitcherScreen(
          modifier = Modifier.fillMaxSize(),
          commandViewModel = commandViewModel
        )
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      commandViewModel.save()
    }
  }
}
