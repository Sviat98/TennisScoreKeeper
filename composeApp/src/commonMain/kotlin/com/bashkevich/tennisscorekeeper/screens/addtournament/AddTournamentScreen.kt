package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.AddTournamentAppBar
import com.bashkevich.tennisscorekeeper.components.add_match.set_template.SetTemplateCombobox
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.remote.mapToDisplayedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentScreen(
    modifier: Modifier = Modifier,
    viewModel: AddTournamentViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }

    val navController = LocalNavHostController.current

    AddTournamentContent(
        modifier = Modifier.then(modifier),
        state = state,
        onEvent = { event -> viewModel.onEvent(event) },
        onNavigateUp = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentContent(
    modifier: Modifier = Modifier,
    state: AddTournamentState,
    onEvent: (AddTournamentUiEvent) -> Unit,
    onNavigateUp: () -> Unit = {},
) {
    val tournamentAddingSubstate = state.tournamentAddingSubstate

    if (tournamentAddingSubstate is TournamentAddingSubstate.Success) {
        onNavigateUp()
    }

    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val isWideScreen = windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        modifier = modifier,
        topBar = {
            AddTournamentAppBar(onBack = onNavigateUp)
        }
    ) { paddingValues ->
        val tournamentName = state.tournamentName
        val tournamentType = state.tournamentType
        val regularTemplates = state.setTemplateOptions.filter { it.isRegular }
        val decidingTemplates = state.setTemplateOptions.filter { it.isDeciding }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isWideScreen) {
                // Row 1: Tournament name + Tournament type
                Row(
                    modifier = Modifier
                        .widthIn(max = 1000.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        state = tournamentName,
                        placeholder = { Text("Tournament name") },
                    )
                    TournamentTypeCombobox(
                        modifier = Modifier.weight(1f),
                        currentTournamentType = tournamentType,
                        onTournamentTypeChange = { type ->
                            onEvent(AddTournamentUiEvent.SelectTournamentType(type))
                        }
                    )
                }

                // Row 2: Regular set template + Deciding set template
                Row(
                    modifier = Modifier
                        .widthIn(max = 1000.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    SetTemplateCombobox(
                        modifier = Modifier.weight(1f),
                        setTemplateOptions = regularTemplates,
                        enabled = true,
                        currentSetTemplate = state.regularSetTemplate,
                        onSetTemplatesFetch = { onEvent(AddTournamentUiEvent.FetchSetTemplates) },
                        onSetTemplateChange = { template ->
                            onEvent(AddTournamentUiEvent.SelectRegularSetTemplate(template))
                        }
                    )
                    SetTemplateCombobox(
                        modifier = Modifier.weight(1f),
                        setTemplateOptions = decidingTemplates,
                        enabled = true,
                        currentSetTemplate = state.decidingSetTemplate,
                        onSetTemplatesFetch = { onEvent(AddTournamentUiEvent.FetchSetTemplates) },
                        onSetTemplateChange = { template ->
                            onEvent(AddTournamentUiEvent.SelectDecidingSetTemplate(template))
                        }
                    )
                }

                // Row 3: Theme (under regular set template, same width)
                Row(
                    modifier = Modifier
                        .widthIn(max = 1000.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    ThemeDropdownMenu(
                        modifier = Modifier.weight(1f),
                        themes = state.themeOptions,
                        selectedTheme = state.selectedTheme,
                        onThemeSelected = { theme ->
                            onEvent(AddTournamentUiEvent.SelectTheme(theme))
                        },
                        onThemesFetch = { onEvent(AddTournamentUiEvent.FetchThemes) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                // Narrow screen: vertical layout
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = tournamentName,
                    placeholder = { Text("Tournament name") },
                )

                TournamentTypeCombobox(
                    modifier = Modifier.fillMaxWidth(),
                    currentTournamentType = tournamentType,
                    onTournamentTypeChange = { type ->
                        onEvent(AddTournamentUiEvent.SelectTournamentType(type))
                    }
                )

                SetTemplateCombobox(
                    modifier = Modifier.fillMaxWidth(),
                    setTemplateOptions = regularTemplates,
                    enabled = true,
                    currentSetTemplate = state.regularSetTemplate,
                    onSetTemplatesFetch = { onEvent(AddTournamentUiEvent.FetchSetTemplates) },
                    onSetTemplateChange = { template ->
                        onEvent(AddTournamentUiEvent.SelectRegularSetTemplate(template))
                    }
                )

                SetTemplateCombobox(
                    modifier = Modifier.fillMaxWidth(),
                    setTemplateOptions = decidingTemplates,
                    enabled = true,
                    currentSetTemplate = state.decidingSetTemplate,
                    onSetTemplatesFetch = { onEvent(AddTournamentUiEvent.FetchSetTemplates) },
                    onSetTemplateChange = { template ->
                        onEvent(AddTournamentUiEvent.SelectDecidingSetTemplate(template))
                    }
                )

                ThemeDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    themes = state.themeOptions,
                    selectedTheme = state.selectedTheme,
                    onThemeSelected = { theme ->
                        onEvent(AddTournamentUiEvent.SelectTheme(theme))
                    },
                    onThemesFetch = { onEvent(AddTournamentUiEvent.FetchThemes) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add button
            val isButtonEnabled =
                (state.tournamentType != null
                        && tournamentName.text.isNotBlank()
                        && state.regularSetTemplate.id != "0"
                        && state.decidingSetTemplate.id != "0"
                        && state.selectedTheme != null
                        ) && tournamentAddingSubstate !is TournamentAddingSubstate.Loading

            Button(
                onClick = {
                    onEvent(
                        AddTournamentUiEvent.AddTournament(
                            tournamentName = tournamentName.text.toString(),
                            tournamentType = tournamentType!!,
                            defaultSetTemplateId = state.regularSetTemplate.id,
                            decidingSetTemplateId = state.decidingSetTemplate.id,
                            themeId = state.selectedTheme!!.id,
                        )
                    )
                },
                enabled = isButtonEnabled
            ) {
                if (tournamentAddingSubstate is TournamentAddingSubstate.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Add")
                }
            }

            if (tournamentAddingSubstate is TournamentAddingSubstate.Error) {
                Text(text = tournamentAddingSubstate.message, color = Color.Red)
            }
        }
    }
}

@Composable
fun TournamentTypeCombobox(
    modifier: Modifier = Modifier,
    currentTournamentType: TournamentType?,
    onTournamentTypeChange: (TournamentType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = TournamentType.entries

    val tournamentTypeText = currentTournamentType?.mapToDisplayedString() ?: ""

    val tournamentTypeState = TextFieldState(tournamentTypeText)

    Box(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = tournamentTypeState,
            placeholder = { Text("Tournament type") },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        imageVector = IconGroup.Default.ArrowDropDown,
                        contentDescription = "Open dropdown",
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.mapToDisplayedString()) },
                    onClick = {
                        onTournamentTypeChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeDropdownMenu(
    modifier: Modifier = Modifier,
    themes: List<ScoreboardTheme>,
    selectedTheme: ScoreboardTheme?,
    onThemeSelected: (ScoreboardTheme) -> Unit,
    onThemesFetch: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedText = selectedTheme?.name ?: ""

    Box(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = TextFieldState(selectedText),
            placeholder = { Text("Select theme") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    expanded = true
                    onThemesFetch()
                }) {
                    Icon(
                        imageVector = IconGroup.Default.ArrowDropDown,
                        contentDescription = "Open dropdown",
                    )
                }
            },
            leadingIcon = selectedTheme?.let { theme ->
                {
                    ColorCircle(color = theme.backgroundColor)
                }
            },
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            themes.forEach { theme ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ColorCircle(color = theme.backgroundColor)
                            Text(
                                text = theme.name,
                                color = theme.textColor
                            )
                        }
                    },
                    onClick = {
                        onThemeSelected(theme)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun ColorCircle(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
    )
}
