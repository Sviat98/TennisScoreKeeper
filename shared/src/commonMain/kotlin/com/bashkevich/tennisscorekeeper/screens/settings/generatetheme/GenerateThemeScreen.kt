package com.bashkevich.tennisscorekeeper.screens.settings.generatetheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.FileSelectionRow
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Close
import com.bashkevich.tennisscorekeeper.components.scoreboard.match_details.MatchDetailsScoreboardView
import com.bashkevich.tennisscorekeeper.components.showUnauthorizedActionSnackbar
import com.bashkevich.tennisscorekeeper.components.theme.ThemeColorList
import com.bashkevich.tennisscorekeeper.components.theme.ThemeNameField
import com.bashkevich.tennisscorekeeper.model.file.domain.ImageFile
import com.bashkevich.tennisscorekeeper.model.match.domain.DOUBLES_SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.theme.domain.uniqueColors
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
import tennisscorekeeper.shared.generated.resources.cancel
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
    val hasGeneratedTheme = state.editedTheme != null
    val hasName = viewModel.themeNameState.text.trim().toString().isNotBlank()
    val canGenerate = hasImage && !state.isGenerating && !state.isSaving
    val canAdd = hasGeneratedTheme && hasName && !state.isSaving

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
                // До выбора картинки — компонент выбора файла
                if (!hasImage) {
                    FileSelectionRow(
                        fileName = state.selectedImageName,
                        placeholder = stringResource(Res.string.select_image_for_upload),
                        onFileStorageOpen = { imagePickerLauncher.launch() },
                        onClearFile = { viewModel.onEvent(GenerateThemeUiEvent.ClearImage) },
                        modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth(),
                    )
                } else {
                    // Картинка выбрана: показываем её превью.
                    // Крестик отмены — только пока тема не сгенерирована
                    Box(
                        modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = state.imageFile.content,
                            contentDescription = state.imageFile.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit,
                        )
                        if (!hasGeneratedTheme) {
                            // Крестик отмены прямо в верхнем правом углу изображения
                            IconButton(
                                onClick = { viewModel.onEvent(GenerateThemeUiEvent.ClearImage) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = IconGroup.Default.Close,
                                    contentDescription = stringResource(Res.string.cancel),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Кнопка генерации — только пока тема не сгенерирована
                    if (!hasGeneratedTheme) {
                        Box(
                            modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isGenerating) {
                                CircularProgressIndicator()
                            } else {
                                Button(
                                    onClick = { viewModel.onEvent(GenerateThemeUiEvent.Generate) },
                                    enabled = canGenerate,
                                    modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth()
                                ) {
                                    Text(stringResource(Res.string.generate))
                                }
                            }
                        }
                    }
                }

                // Превью темы, поле имени и палитра — только после генерации
                val editedTheme = state.editedTheme
                val originalTheme = state.originalTheme
                if (editedTheme != null && originalTheme != null) {
                    MatchDetailsScoreboardView(
                        match = DOUBLES_SAMPLE_MATCH,
                        theme = editedTheme
                    )

                    ThemeNameField(
                        themeNameState = viewModel.themeNameState,
                        oldName = ScoreboardTheme.DEFAULT.name,
                        showOldValue = false,
                    )

                    // Палитра цветов темы с сохранением оригинальных агентских значений:
                    // смена цвета — ColorPicker (клик по ColorBox), dropdown палитры или Undo
                    ThemeColorList(
                        editedTheme = editedTheme,
                        oldTheme = originalTheme,
                        onColorSelected = { field, color ->
                            viewModel.onEvent(GenerateThemeUiEvent.UpdateColor(field, color))
                        },
                        paletteColors = editedTheme.uniqueColors,
                    )

                    Button(
                        onClick = { viewModel.onEvent(GenerateThemeUiEvent.AddTheme) },
                        enabled = canAdd,
                        modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.add))
                    }
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
