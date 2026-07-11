package com.bashkevich.tennisscorekeeper.screens.settings.themedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.ColorPickerDialog
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.navigate_back
import tennisscorekeeper.composeapp.generated.resources.old_value
import tennisscorekeeper.composeapp.generated.resources.save
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_game_background
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_game_text
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_set_background
import tennisscorekeeper.composeapp.generated.resources.theme_color_current_set_text
import tennisscorekeeper.composeapp.generated.resources.theme_color_main_background
import tennisscorekeeper.composeapp.generated.resources.theme_color_main_text
import tennisscorekeeper.composeapp.generated.resources.theme_color_previous_set_lose
import tennisscorekeeper.composeapp.generated.resources.theme_color_previous_set_win
import tennisscorekeeper.composeapp.generated.resources.theme_color_serve
import tennisscorekeeper.composeapp.generated.resources.theme_name
import tennisscorekeeper.composeapp.generated.resources.try_again

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreboardThemeDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreboardThemeDetailsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val loadingState = state.loadingState) {
        is ThemeDetailsLoadingState.Loading -> {
            ThemeDetailsLoading(modifier = modifier)
        }
        is ThemeDetailsLoadingState.Content -> {
            ThemeDetailsContent(
                modifier = modifier,
                loadingState = loadingState,
                action = state.action,
                onEvent = { viewModel.onEvent(it) },
                onConsumeAction = { viewModel.consumeAction() }
            )
        }
        is ThemeDetailsLoadingState.Error -> {
            ThemeDetailsError(
                modifier = modifier,
                loadingState = loadingState,
                onRetry = { viewModel.onEvent(ThemeDetailsUiEvent.Retry) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDetailsContent(
    modifier: Modifier = Modifier,
    loadingState: ThemeDetailsLoadingState.Content,
    action: ThemeDetailsAction?,
    onEvent: (ThemeDetailsUiEvent) -> Unit,
    onConsumeAction: () -> Unit,
) {
    val navController = LocalNavHostController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedUiEffectHandler(
        effect = action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = onConsumeAction
    ) { currentAction ->
        when (currentAction) {
            is ThemeDetailsAction.ThemeSaved -> navController.navigateUp()
            is ThemeDetailsAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(loadingState.editedTheme.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = IconGroup.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                },
                actions = {
                    if (loadingState.hasChanges) {
                        if (loadingState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(2.dp)
                            )
                        } else {
                            IconButton(onClick = { onEvent(ThemeDetailsUiEvent.SaveTheme) }) {
                                Text(
                                    text = stringResource(Res.string.save),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = loadingState.isSaving,
            onRefresh = { onEvent(ThemeDetailsUiEvent.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchDetailsScoreboardView(
                    modifier = Modifier.widthIn(max = 360.dp),
                    match = DOUBLES_SAMPLE_MATCH,
                    theme = loadingState.editedTheme
                )

                OutlinedTextField(
                    value = loadingState.editedTheme.name,
                    onValueChange = { onEvent(ThemeDetailsUiEvent.UpdateName(it)) },
                    label = { Text(stringResource(Res.string.theme_name)) },
                    modifier = Modifier.fillMaxWidth().widthIn(max = 360.dp),
                    singleLine = true
                )

                ThemeColorField.entries.forEach { field ->
                    var showColorPicker by remember { mutableStateOf(false) }
                    val currentColor = field.getColor(loadingState.editedTheme)
                    val oldColor = field.getColor(loadingState.oldTheme)
                    val hasChanged = currentColor != oldColor

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 360.dp)
                            .clickable { showColorPicker = true }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = field.displayName(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                ColorBox(color = currentColor)
                            }
                            if (hasChanged) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(Res.string.old_value),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    ColorBox(color = oldColor)
                                }
                            }
                        }
                    }

                    if (showColorPicker) {
                        ColorPickerDialog(
                            initialColor = currentColor,
                            onDismissRequest = { showColorPicker = false },
                            onColorSelected = {
                                showColorPicker = false
                                onEvent(ThemeDetailsUiEvent.UpdateColor(field, it))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorBox(
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
    )
}

@Composable
private fun ThemeColorField.displayName(): String = when (this) {
    ThemeColorField.MAIN_BACKGROUND_COLOR -> stringResource(Res.string.theme_color_main_background)
    ThemeColorField.MAIN_TEXT_COLOR -> stringResource(Res.string.theme_color_main_text)
    ThemeColorField.SERVE_COLOR -> stringResource(Res.string.theme_color_serve)
    ThemeColorField.PREVIOUS_SET_WIN_TEXT_COLOR -> stringResource(Res.string.theme_color_previous_set_win)
    ThemeColorField.PREVIOUS_SET_LOSE_TEXT_COLOR -> stringResource(Res.string.theme_color_previous_set_lose)
    ThemeColorField.CURRENT_SET_BACKGROUND_COLOR -> stringResource(Res.string.theme_color_current_set_background)
    ThemeColorField.CURRENT_SET_TEXT_COLOR -> stringResource(Res.string.theme_color_current_set_text)
    ThemeColorField.CURRENT_GAME_BACKGROUND_COLOR -> stringResource(Res.string.theme_color_current_game_background)
    ThemeColorField.CURRENT_GAME_TEXT_COLOR -> stringResource(Res.string.theme_color_current_game_text)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDetailsLoading(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavHostController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("") },
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDetailsError(
    modifier: Modifier = Modifier,
    loadingState: ThemeDetailsLoadingState.Error,
    onRetry: () -> Unit,
) {
    val navController = LocalNavHostController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("") },
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(loadingState.message)
            Button(onClick = onRetry) {
                Text(stringResource(Res.string.try_again))
            }
        }
    }
}
