package com.example.penasim.ui.command

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination

object PitcherDestination : NavigationDestination {
    override val route: String = "pitcher"
    override val titleResId: Int = R.string.pitcher
}

@Composable
fun PitcherScreen(
    modifier: Modifier = Modifier,
) {
    Column {
        Text("Pitcher Screen")
    }
}