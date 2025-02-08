package com.bashkevich.tennisscorekeeper.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MatchesScreen(
    modifier: Modifier = Modifier,
    onNavigateToMatchRoute: () -> Unit
) {
    Column(
        modifier = Modifier.then(modifier).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("This is a Home Screen")
        Button(onClick = {
            onNavigateToMatchRoute()
        }) {
            Text("Go to match route")
        }
    }
}