package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
    onBack: () -> Unit
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
            Box() {
                Button(onClick = { expanded = true }) {
                    Text("Click")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(onClick = {expanded = false}){
                        Text("Click")
                    }
                }
            }

        }
    )
}