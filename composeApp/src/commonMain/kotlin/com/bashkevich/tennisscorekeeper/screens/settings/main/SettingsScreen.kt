package com.bashkevich.tennisscorekeeper.screens.settings.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Palette
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Scoreboard
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Tune
import com.bashkevich.tennisscorekeeper.navigation.GeneralSettingsRoute
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardThemeListRoute
import com.bashkevich.tennisscorekeeper.navigation.SetTemplateListRoute
import com.bashkevich.tennisscorekeeper.model.auth.domain.LoggedInPlayer
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.general_settings
import tennisscorekeeper.composeapp.generated.resources.guest
import tennisscorekeeper.composeapp.generated.resources.log_in
import tennisscorekeeper.composeapp.generated.resources.logout
import tennisscorekeeper.composeapp.generated.resources.navigate_back
import tennisscorekeeper.composeapp.generated.resources.scoreboard_appearance
import tennisscorekeeper.composeapp.generated.resources.set_templates
import tennisscorekeeper.composeapp.generated.resources.settings

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = modifier,
        loggedInPlayer = state.loggedInPlayer,
        onEvent = { viewModel.onEvent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    loggedInPlayer: LoggedInPlayer,
    onEvent: (SettingsUiEvent) -> Unit,
) {
    val navController = LocalNavHostController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = IconGroup.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            ProfileCard(
                loggedInPlayer = loggedInPlayer,
                onLogout = { onEvent(SettingsUiEvent.Logout) },
                onLogin = { navController.navigate(LoginRoute) }
            )

            SettingsNavigationCard(
                icon = IconGroup.Default.Tune,
                title = stringResource(Res.string.general_settings),
                contentDescription = stringResource(Res.string.general_settings),
                onClick = { navController.navigate(GeneralSettingsRoute) }
            )

            SettingsNavigationCard(
                icon = IconGroup.Default.Palette,
                title = stringResource(Res.string.scoreboard_appearance),
                contentDescription = stringResource(Res.string.scoreboard_appearance),
                onClick = { navController.navigate(ScoreboardThemeListRoute) }
            )

            SettingsNavigationCard(
                icon = IconGroup.Default.Scoreboard,
                title = stringResource(Res.string.set_templates),
                contentDescription = stringResource(Res.string.set_templates),
                onClick = { navController.navigate(SetTemplateListRoute) }
            )
        }
    }
}

@Composable
private fun ProfileCard(
    loggedInPlayer: LoggedInPlayer,
    onLogout: () -> Unit,
    onLogin: () -> Unit,
) {
    val isAuthorized = LocalAuthorization.current

    val playerLabelText = if (isAuthorized) "${loggedInPlayer.name} ${loggedInPlayer.surname}"
    else stringResource(Res.string.guest)

    Card(
        modifier = Modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = playerLabelText,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isAuthorized) {
                TextButton(onClick = onLogout) {
                    Text(stringResource(Res.string.logout))
                }
            } else {
                TextButton(onClick = onLogin) {
                    Text(stringResource(Res.string.log_in))
                }
            }
        }
    }
}

@Composable
private fun SettingsNavigationCard(
    icon: ImageVector,
    title: String,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
