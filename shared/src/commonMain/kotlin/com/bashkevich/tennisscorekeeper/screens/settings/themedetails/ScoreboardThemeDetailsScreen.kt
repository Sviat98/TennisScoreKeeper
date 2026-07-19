package com.bashkevich.tennisscorekeeper.screens.settings.themedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Check
import com.bashkevich.tennisscorekeeper.components.modifier.refreshByKeyboard
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeColorList
import com.bashkevich.tennisscorekeeper.components.theme.ThemeNameField
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.edit_theme
import tennisscorekeeper.shared.generated.resources.navigate_back
import tennisscorekeeper.shared.generated.resources.save
import tennisscorekeeper.shared.generated.resources.try_again

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
                themeNameState = viewModel.themeNameState,
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
    themeNameState: TextFieldState,
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
            is ThemeDetailsAction.ShowUnauthorizedActionError ->
                snackbarHostState.showUnauthorizedActionSnackbar(navController = navController)
            is ThemeDetailsAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }
    val hasThemeNameChanged = themeNameState.text.trim().toString() != loadingState.oldTheme.name
    val hasThemeChanged = loadingState.editedTheme != loadingState.oldTheme

    val hasChanges = hasThemeNameChanged || hasThemeChanged


    Scaffold(
        modifier = Modifier.then(modifier).refreshByKeyboard { onEvent(ThemeDetailsUiEvent.Refresh) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.edit_theme)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = IconGroup.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                },
                actions = {
                    if (hasChanges) {
                        IconButton(
                            onClick = { onEvent(ThemeDetailsUiEvent.SaveTheme) },
                            enabled = !loadingState.isSaving
                        ) {
                            Icon(
                                imageVector = IconGroup.Default.Check,
                                contentDescription = stringResource(Res.string.save)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = loadingState.isRefreshing,
            onRefresh = { onEvent(ThemeDetailsUiEvent.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                MatchDetailsScoreboardView(
                    modifier = Modifier.widthIn(max = 360.dp),
                    match = DOUBLES_SAMPLE_MATCH,
                    theme = loadingState.editedTheme
                )

                ThemeNameField(
                    themeNameState = themeNameState,
                    oldName = loadingState.oldTheme.name,
                )

                ThemeColorList(
                    editedTheme = loadingState.editedTheme,
                    oldTheme = loadingState.oldTheme,
                    onColorSelected = { field, color ->
                        onEvent(ThemeDetailsUiEvent.UpdateColor(field, color))
                    },
                )
            }
        }
    }
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
