package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.AppConfig
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Person
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Share
import com.bashkevich.tennisscorekeeper.components.icons.filled_icons.Person

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
    isAuthorized: Boolean,
    onNavigateToLoginOrProfile: () -> Unit
) {
    TopAppBar(
        title = { Text("Tournaments") },
        actions = {
            if (isAuthorized){
                IconButton(onClick = onNavigateToLoginOrProfile){
                    Icon(IconGroup.Default.Person, contentDescription = "Navigate to profile")
                }
            }else{
                LoginButton(
                    onNavigateToLogin = onNavigateToLoginOrProfile
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailsAppBar(
    onBack: () -> Unit,
    isAuthorized: Boolean,
    onNavigateToLoginOrProfile: () -> Unit
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
            if (isAuthorized){
                IconButton(onClick = onNavigateToLoginOrProfile){
                    Icon(IconGroup.Filled.Person, contentDescription = "Navigate to profile")
                }
            }else{
                LoginButton(
                    onNavigateToLogin = onNavigateToLoginOrProfile
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMatchAppBar(
    onBack: () -> Unit,
    isAuthorized: Boolean,
    onNavigateToLoginOrProfile: () -> Unit
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
            if (isAuthorized){
                IconButton(onClick = onNavigateToLoginOrProfile){
                    Icon(IconGroup.Filled.Person, contentDescription = "Navigate to profile")
                }
            }else{
                LoginButton(
                    onNavigateToLogin = onNavigateToLoginOrProfile
                )
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
    matchId: String,
    onBack: () -> Unit,
    onCopyLink: (String) -> Unit,
    isAuthorized: Boolean,
    onNavigateToLoginOrProfile: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val appConfig = AppConfig.current

    val baseUrlFrontend = appConfig.baseUrlFrontend
    TopAppBar(
        title = { Text("Match") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red),
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
            if (isAuthorized){
                IconButton(onClick = onNavigateToLoginOrProfile){
                    Icon(IconGroup.Filled.Person, contentDescription = "Navigate to profile")
                }
            }else{
                LoginButton(
                    onNavigateToLogin = onNavigateToLoginOrProfile
                )
            }
        }
    )
}