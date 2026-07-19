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
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.add_match
import tennisscorekeeper.shared.generated.resources.add_tournament
import tennisscorekeeper.shared.generated.resources.copy_link
import tennisscorekeeper.shared.generated.resources.copy_link_to_panel
import tennisscorekeeper.shared.generated.resources.copy_link_to_scoreboard
import tennisscorekeeper.shared.generated.resources.login
import tennisscorekeeper.shared.generated.resources.match
import tennisscorekeeper.shared.generated.resources.navigate_back
import tennisscorekeeper.shared.generated.resources.navigate_to_settings
import tennisscorekeeper.shared.generated.resources.tournament
import tennisscorekeeper.shared.generated.resources.tournaments

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListAppBar() {
    TopAppBar(
        title = { Text(stringResource(Res.string.tournaments)) },
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListAppBarWithButton(
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(Res.string.tournaments)) },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = stringResource(Res.string.navigate_to_settings))
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
        title = { Text(stringResource(Res.string.tournament)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = stringResource(Res.string.navigate_to_settings))
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
        title = { Text(stringResource(Res.string.add_match)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = stringResource(Res.string.navigate_to_settings))
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
        title = { Text(stringResource(Res.string.add_tournament)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = stringResource(Res.string.navigate_to_settings))
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
        title = { Text(stringResource(Res.string.login)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back)
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
        title = { Text(stringResource(Res.string.match)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.navigate_back)
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(IconGroup.Default.Share, contentDescription = stringResource(Res.string.copy_link))
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.copy_link_to_scoreboard)) },
                        onClick = {
                        onCopyLink("$baseUrlFrontend/#matches/${matchId}/scoreboard")
                        expanded = false
                    })
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.copy_link_to_panel)) },
                        onClick = {
                        onCopyLink("$baseUrlFrontend/#matches/${matchId}")
                        expanded = false
                    })
                }
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(IconGroup.Default.Settings, contentDescription = stringResource(Res.string.navigate_to_settings))
            }
        }
    )
}
