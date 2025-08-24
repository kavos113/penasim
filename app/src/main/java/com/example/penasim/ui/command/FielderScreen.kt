package com.example.penasim.ui.command

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.penasim.R
import com.example.penasim.ui.navigation.NavigationDestination

object FielderDestination : NavigationDestination {
    override val route: String = "fielder"
    override val titleResId: Int = R.string.fielder
}

@Composable
fun FielderScreen(
    modifier: Modifier = Modifier,
) {
    Column {
        Text("Batter Screen")
    }
}