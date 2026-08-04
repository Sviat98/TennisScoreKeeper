package com.bashkevich.tennisscorekeeper.screens.settings.generatetheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.FileSelectionRow
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeNameField
import com.bashkevich.tennisscorekeeper.model.file.domain.ImageFile
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.getName
import com.mohamedrejeb.calf.io.readByteArray
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.add
import tennisscorekeeper.shared.generated.resources.generate
import tennisscorekeeper.shared.generated.resources.generate_theme_by_image
import tennisscorekeeper.shared.generated.resources.navigate_back
import tennisscorekeeper.shared.generated.resources.select_image_for_upload

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateThemeScreen(
    modifier: Modifier = Modifier,
    viewModel: GenerateThemeViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavHostController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    val imagePickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            scope.launch {
                files.firstOrNull()?.let { file ->
                    val image = ImageFile(
                        name = file.getName(context) ?: "image.png",
                        content = file.readByteArray(context)
                    )
                    viewModel.onEvent(GenerateThemeUiEvent.SelectImage(image))
                }
            }
        }
    )

    LaunchedUiEffectHandler(
        effect = state.action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = { viewModel.consumeAction() }
    ) { currentAction ->
        when (currentAction) {
            is GenerateThemeAction.ThemeSaved -> navController.navigateUp()
            is GenerateThemeAction.ShowUnauthorizedActionError ->
                snackbarHostState.showUnauthorizedActionSnackbar(navController = navController)
            is GenerateThemeAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    val hasImage = state.selectedImageName.isNotBlank()
    val hasName = viewModel.themeNameState.text.trim().toString().isNotBlank()
    val canGenerate = hasImage && !state.isGenerating && !state.isSaving
    val canAdd = state.generatedTheme != null && hasName && !state.isSaving

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.generate_theme_by_image)) },
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                FileSelectionRow(
                    fileName = state.selectedImageName,
                    placeholder = stringResource(Res.string.select_image_for_upload),
                    onFileStorageOpen = { imagePickerLauncher.launch() },
                    onClearFile = { viewModel.onEvent(GenerateThemeUiEvent.ClearImage) },
                    modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth(),
                )

                Button(
                    onClick = { viewModel.onEvent(GenerateThemeUiEvent.Generate) },
                    enabled = canGenerate,
                    modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.generate))
                }

                Box(
                    modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isGenerating) {
                        CircularProgressIndicator()
                    } else {
                        MatchDetailsScoreboardView(
                            match = DOUBLES_SAMPLE_MATCH,
                            theme = state.generatedTheme ?: ScoreboardTheme.DEFAULT
                        )
                    }
                }

                ThemeNameField(
                    themeNameState = viewModel.themeNameState,
                    oldName = ScoreboardTheme.DEFAULT.name,
                    showOldValue = false,
                )

                Button(
                    onClick = { viewModel.onEvent(GenerateThemeUiEvent.AddTheme) },
                    enabled = canAdd,
                    modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.add))
                }
            }

            if (state.isSaving) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
