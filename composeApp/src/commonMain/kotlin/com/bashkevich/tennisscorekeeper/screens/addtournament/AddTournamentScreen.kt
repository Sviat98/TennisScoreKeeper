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
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.theme.ScoreboardTheme
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

    Scaffold(
        modifier = modifier,
        topBar = {
            AddTournamentAppBar(onBack = onNavigateUp)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val tournamentName = state.tournamentName
            val tournamentType = state.tournamentType

            // Tournament name
            TextField(
                modifier = Modifier.fillMaxWidth(),
                state = tournamentName,
                placeholder = { Text("Tournament name") },
            )

            // Tournament type
            TournamentTypeCombobox(
                modifier = Modifier.fillMaxWidth(),
                currentTournamentType = tournamentType,
                onTournamentTypeChange = { type ->
                    onEvent(AddTournamentUiEvent.SelectTournamentType(type))
                }
            )

            // Set templates section
            Text(
                text = "Set Templates",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.setTemplatesLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                val regularTemplates = state.setTemplateOptions.filter { it.isRegular }
                val decidingTemplates = state.setTemplateOptions.filter { it.isDeciding }

                // Regular set template
                SetTemplateCombobox(
                    modifier = Modifier.fillMaxWidth(),
                    setTemplateOptions = regularTemplates,
                    enabled = true,
                    currentSetTemplate = state.regularSetTemplate,
                    onSetTemplatesFetch = {},
                    onSetTemplateChange = { template ->
                        onEvent(AddTournamentUiEvent.SelectRegularSetTemplate(template))
                    }
                )

                // Deciding set template
                SetTemplateCombobox(
                    modifier = Modifier.fillMaxWidth(),
                    setTemplateOptions = decidingTemplates,
                    enabled = true,
                    currentSetTemplate = state.decidingSetTemplate,
                    onSetTemplatesFetch = {},
                    onSetTemplateChange = { template ->
                        onEvent(AddTournamentUiEvent.SelectDecidingSetTemplate(template))
                    }
                )
            }

            // Theme section
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.themesLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                ThemeDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    themes = state.themeOptions,
                    selectedTheme = state.selectedTheme,
                    onThemeSelected = { theme ->
                        onEvent(AddTournamentUiEvent.SelectTheme(theme))
                    }
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
    onThemeSelected: (ScoreboardTheme) -> Unit
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
                IconButton(onClick = { expanded = true }) {
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
