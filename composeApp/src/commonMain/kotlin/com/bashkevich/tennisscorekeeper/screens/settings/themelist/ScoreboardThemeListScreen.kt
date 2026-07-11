package com.bashkevich.tennisscorekeeper.screens.settings.themelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.modifier.hoverScaleEffect
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardThemeDetailsRoute
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.navigate_back
import tennisscorekeeper.composeapp.generated.resources.scoreboard_appearance
import tennisscorekeeper.composeapp.generated.resources.try_again

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreboardThemeListScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreboardThemeListViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val loadingState = state.loadingState) {
        is ScoreboardThemeListLoadingState.Loading -> {
            ScoreboardThemeListLoading(modifier = modifier)
        }
        is ScoreboardThemeListLoadingState.Content -> {
            ScoreboardThemeListContent(
                modifier = modifier,
                loadingState = loadingState,
                action = state.action,
                onRefresh = { viewModel.onEvent(ScoreboardThemeListUiEvent.RefreshThemes) },
                onConsumeAction = { viewModel.consumeAction() },
            )
        }
        is ScoreboardThemeListLoadingState.Error -> {
            ScoreboardThemeListError(
                modifier = modifier,
                loadingState = loadingState,
                onRetry = { viewModel.onEvent(ScoreboardThemeListUiEvent.Retry) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScoreboardThemeListContent(
    modifier: Modifier = Modifier,
    loadingState: ScoreboardThemeListLoadingState.Content,
    action: ScoreboardThemeListAction?,
    onRefresh: () -> Unit,
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
            is ScoreboardThemeListAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.scoreboard_appearance)) },
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
        PullToRefreshBox(
            isRefreshing = loadingState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    loadingState.themes,
                    key = { it.id }
                ) { theme ->
                    ThemeCard(
                        modifier = Modifier
                            .widthIn(max = 360.dp)
                            .fillMaxWidth()
                            .hoverScaleEffect(),
                        theme = theme,
                        onThemeClick = { navController.navigate(ScoreboardThemeDetailsRoute(theme.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeCard(
    modifier: Modifier = Modifier,
    theme: com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme,
    onThemeClick: () -> Unit,
) {
    Card(
        onClick = onThemeClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = theme.name,
                style = MaterialTheme.typography.titleMedium
            )
            MatchDetailsScoreboardView(
                match = DOUBLES_SAMPLE_MATCH,
                theme = theme
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScoreboardThemeListLoading(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavHostController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.scoreboard_appearance)) },
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
private fun ScoreboardThemeListError(
    modifier: Modifier = Modifier,
    loadingState: ScoreboardThemeListLoadingState.Error,
    onRetry: () -> Unit,
) {
    val navController = LocalNavHostController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.scoreboard_appearance)) },
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
