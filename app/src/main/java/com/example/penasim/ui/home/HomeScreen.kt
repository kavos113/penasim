package com.example.penasim.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination
import com.example.penasim.ui.theme.PenasimTheme

object HomeDestination : NavigationDestination {
    override val route: String = "home"
    override val titleResId: Int = R.string.app_name
}

@Composable
fun HomeScreen(
    onGameClick: () -> Unit = {},
    onCalenderClick: () -> Unit = {},
    onCommandClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(30.dp)
    ) {
        HomeInformation(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        HomeMenu(
            onGameClick = onGameClick,
            onCalenderClick = onCalenderClick,
            onCommandClick = onCommandClick,
            modifier = Modifier
        )
    }
}

@Composable
fun HomeInformation(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Text(stringResource(R.string.date, 100))
        Text(stringResource(R.string.rank, 2))
    }
}

@Composable
fun HomeMenu(
    onGameClick: () -> Unit = {},
    onCalenderClick: () -> Unit = {},
    onCommandClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Button(
            onClick = onGameClick,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.game))
        }
        Button(
            onClick = onCalenderClick,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.calender))
        }
        Button(
            onClick = onCommandClick,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.command))
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    PenasimTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
        ) {
            HomeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}