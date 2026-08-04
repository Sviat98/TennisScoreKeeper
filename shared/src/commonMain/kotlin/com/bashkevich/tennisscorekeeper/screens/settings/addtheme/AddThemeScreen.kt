package com.bashkevich.tennisscorekeeper.screens.settings.addtheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeColorList
import com.bashkevich.tennisscorekeeper.components.theme.ThemeNameField
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.add
import tennisscorekeeper.shared.generated.resources.add_theme
import tennisscorekeeper.shared.generated.resources.navigate_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddThemeScreen(
    modifier: Modifier = Modifier,
    viewModel: AddThemeViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavHostController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedUiEffectHandler(
        effect = state.action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = { viewModel.consumeAction() }
    ) { currentAction ->
        when (currentAction) {
            is AddThemeAction.ThemeSaved -> navController.navigateUp()
            is AddThemeAction.ShowUnauthorizedActionError ->
                snackbarHostState.showUnauthorizedActionSnackbar(navController = navController)
            is AddThemeAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    val hasName = viewModel.themeNameState.text.trim().toString().isNotBlank()
    val canAdd = hasName && !state.isSaving

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.add_theme)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = IconGroup.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            MatchDetailsScoreboardView(
                modifier = Modifier.widthIn(max = 360.dp),
                match = DOUBLES_SAMPLE_MATCH,
                theme = state.editedTheme
            )

            ThemeNameField(
                themeNameState = viewModel.themeNameState,
                oldName = ScoreboardTheme.DEFAULT.name,
                showOldValue = false,
            )

            ThemeColorList(
                editedTheme = state.editedTheme,
                oldTheme = ScoreboardTheme.DEFAULT,
                onColorSelected = { field, color ->
                    viewModel.onEvent(AddThemeUiEvent.UpdateColor(field, color))
                },
                showOldValue = false,
            )

            Button(
                onClick = { viewModel.onEvent(AddThemeUiEvent.AddTheme) },
                enabled = canAdd,
                modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth()
            ) {
                Text(stringResource(Res.string.add))
            }
        }
    }
}
