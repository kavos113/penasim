package com.example.penasim.ui.command

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination

object CommandDestination : NavigationDestination {
    override val route: String = "command"
    override val titleResId: Int = R.string.command
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandScreen(
    modifier: Modifier = Modifier,
) {
    val tabs = listOf(R.string.batter, R.string.pitcher)

    val navController = rememberNavController()
    val selectedTabIndex = rememberPagerState { tabs.size }

    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex.currentPage,
        modifier = modifier,
    ) {
        tabs.forEachIndexed { index, titleResId ->
            Tab(
                selected = selectedTabIndex.currentPage == index,
                onClick = {
                    navController.navigate(
                        when (index) {
                            0 -> BatterDestination.route
                            1 -> PitcherDestination.route
                            else -> throw IndexOutOfBoundsException()
                        }
                    )
                },
                text = {
                    Text(stringResource(titleResId))
                }
            )
        }
    }
    HorizontalPager(
        state = selectedTabIndex,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> BatterScreen(modifier = Modifier.fillMaxSize())
            1 -> PitcherScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview
@Composable
fun CommandScreenPreview() {
    CommandScreen()
}