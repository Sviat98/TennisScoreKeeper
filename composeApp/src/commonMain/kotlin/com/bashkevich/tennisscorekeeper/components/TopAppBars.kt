package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bashkevich.tennisscorekeeper.AppConfig
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Settings
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListAppBar() {
    TopAppBar(
        title = { Text("Tournaments") },
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListAppBarWithButton(
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = { Text("Tournaments") },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = "Navigate to settings")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailsAppBar(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = { Text("Tournament") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = "Navigate to settings")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMatchAppBar(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = { Text("Add Match") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = "Navigate to settings")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentAppBar(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    TopAppBar(
        title = { Text("Add Tournament") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = "Navigate to settings")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAppBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text("Login") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsAppBar(
    matchId: Int,
    onBack: () -> Unit,
    onCopyLink: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val appConfig = AppConfig.current

    val baseUrlFrontend = appConfig.baseUrlFrontend
    TopAppBar(
        title = { Text("Match") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(IconGroup.Default.Share, contentDescription = "Copy link")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Copy link to scoreboard") },
                        onClick = {
                        onCopyLink("$baseUrlFrontend/#matches/${matchId}/scoreboard")
                        expanded = false
                    })
                    DropdownMenuItem(
                        text = { Text("Copy link to panel") },
                        onClick = {
                        onCopyLink("$baseUrlFrontend/#matches/${matchId}")
                        expanded = false
                    })
                }
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = "Navigate to settings")
            }
        }
    )
}
