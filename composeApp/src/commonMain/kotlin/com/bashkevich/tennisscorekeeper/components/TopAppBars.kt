package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bashkevich.tennisscorekeeper.core.BASE_URL_FRONTEND

@Composable
fun TournamentListAppBar() {
    TopAppBar(
        title = { Text("Tournaments") }
    )
}

@Composable
fun TournamentDetailsAppBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text("Tournament") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        }
    )
}

@Composable
fun AddMatchAppBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text("Add Match") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        }
    )
}

@Composable
fun MatchDetailsAppBar(
    matchId: String,
    onBack: () -> Unit,
    onCopyLink: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("Match") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.Share, contentDescription = "Copy link")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(onClick = {
                        onCopyLink("$BASE_URL_FRONTEND/#scoreboard?matchId=${matchId}")
                        expanded = false
                    }) {
                        Text("Copy link to scoreboard")
                    }
                    DropdownMenuItem(onClick = {
                        onCopyLink("$BASE_URL_FRONTEND/#matches/${matchId}")
                        expanded = false
                    }) {
                        Text("Copy link to panel")
                    }
                }
            }
            LoginButton(
                onNavigateToLogin = {}
            )
        }
    )
}