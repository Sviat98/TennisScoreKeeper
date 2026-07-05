package com.bashkevich.tennisscorekeeper.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import com.bashkevich.tennisscorekeeper.navigation.ProfileRoute
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.guest
import tennisscorekeeper.composeapp.generated.resources.log_in
import tennisscorekeeper.composeapp.generated.resources.navigate_back
import tennisscorekeeper.composeapp.generated.resources.profile
import tennisscorekeeper.composeapp.generated.resources.settings

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = modifier,
        playerId = state.playerId,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    playerId: String,
) {
    val navController = LocalNavHostController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(IconGroup.Default.ArrowBack, contentDescription = stringResource(Res.string.navigate_back))
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
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (playerId.isEmpty()) {
                        Text(stringResource(Res.string.guest))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate(LoginRoute) }) {
                            Text(stringResource(Res.string.log_in))
                        }
                    } else {
                        Text(playerId)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate(ProfileRoute) }) {
                            Text(stringResource(Res.string.profile))
                        }
                    }
                }
            }
        }
    }
}
