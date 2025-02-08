package com.bashkevich.tennisscorekeeper.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MatchScreen(
    modifier: Modifier = Modifier,
    matchId: Int
) {
    Column(
        modifier = Modifier.then(modifier).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("This is a Match Screen with id: $matchId")
    }
}