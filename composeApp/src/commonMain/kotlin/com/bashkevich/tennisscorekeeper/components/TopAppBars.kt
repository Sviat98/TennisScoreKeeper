package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bashkevich.tennisscorekeeper.AppConfig
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Person
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Share
import com.bashkevich.tennisscorekeeper.components.icons.filled_icons.Person

@Composable
fun TournamentListAppBar() {
    TopAppBar(
        title = { Text("Tournaments") },
    )
}
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
                    DropdownMenuItem(onClick = {
                        onCopyLink("$baseUrlFrontend/#matches/${matchId}/scoreboard")
                        expanded = false
                    }) {
                        Text("Copy link to scoreboard")
                    }
                    DropdownMenuItem(onClick = {
                        onCopyLink("$baseUrlFrontend/#matches/${matchId}")
                        expanded = false
                    }) {
                        Text("Copy link to panel")
                    }
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