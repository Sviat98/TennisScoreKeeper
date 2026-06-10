package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.AddTournamentAppBar
import com.bashkevich.tennisscorekeeper.components.add_match.MatchScoringAndThemeSettingsBlock
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.remote.mapToDisplayedString
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import com.bashkevich.tennisscorekeeper.navigation.ProfileRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentScreen(
    modifier: Modifier = Modifier,
    viewModel: AddTournamentViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavHostController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isAuthorized = LocalAuthorization.current

    LaunchedEffect(state.action) {
        val action = state.action ?: return@LaunchedEffect
        when (action) {
            is AddTournamentAction.TournamentAdded -> {
                navController.navigateUp()
            }

            is AddTournamentAction.ShowAddError -> {
                snackbarHostState.showSnackbar(message = action.message)
            }

            is AddTournamentAction.ShowUnauthorizedError -> {
                snackbarHostState.showSnackbar(message = "You need to login for this action")
            }
        }
        viewModel.consumeAction()
    }

    AddTournamentContent(
        modifier = Modifier.then(modifier),
        state = state,
        tournamentNameState = viewModel.tournamentNameState,
        onEvent = { event -> viewModel.onEvent(event) },
        snackbarHostState = snackbarHostState,
        isAuthorized = isAuthorized,
        onBack = { navController.navigateUp() },
        onNavigateToLoginOrProfile = {
            if (isAuthorized) {
                navController.navigate(ProfileRoute)
            } else {
                navController.navigate(LoginRoute)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTournamentContent(
    modifier: Modifier = Modifier,
    state: AddTournamentState,
    tournamentNameState: TextFieldState,
    onEvent: (AddTournamentUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    isAuthorized: Boolean = false,
    onBack: () -> Unit = {},
    onNavigateToLoginOrProfile: () -> Unit = {},
) {
    val tournamentType = state.tournamentType

    val regularSetTemplate = (state.regularSetComponentState.selectedSetState as? SetComponentState.SelectedSetState.Idle)?.setTemplate
    val decidingSetTemplate = (state.decidingSetComponentState.selectedSetState as? SetComponentState.SelectedSetState.Idle)?.setTemplate
    val selectedTheme = (state.themeComponentState.selectedTheme as? ThemeComponentState.SelectedThemeState.Idle)?.theme

    Scaffold(
        modifier = modifier,
        topBar = {
            AddTournamentAppBar(
                onBack = onBack,
                isAuthorized = isAuthorized,
                onNavigateToLoginOrProfile = onNavigateToLoginOrProfile,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            // Tournament name + Tournament type
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    state = tournamentNameState,
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

            // Scoring and theme settings block
            MatchScoringAndThemeSettingsBlock(
                setsToWin = state.setsToWin,
                regularSetComponentState = state.regularSetComponentState,
                decidingSetComponentState = state.decidingSetComponentState,
                themeComponentState = state.themeComponentState,
                onSetsToWinChange = { delta ->
                    onEvent(AddTournamentUiEvent.ChangeSetsToWin(delta))
                },
                onSetTemplatesFetch = { filter ->
                    onEvent(AddTournamentUiEvent.FetchSetTemplates(filter))
                },
                onRegularSetTemplateChange = { template ->
                    onEvent(AddTournamentUiEvent.SelectRegularSetTemplate(template))
                },
                onDecidingSetTemplateChange = { template ->
                    onEvent(AddTournamentUiEvent.SelectDecidingSetTemplate(template))
                },
                onThemeSelected = { theme ->
                    onEvent(AddTournamentUiEvent.SelectTheme(theme))
                },
                onThemesFetch = {
                    onEvent(AddTournamentUiEvent.FetchThemes)
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add button
            val needsRegularSet = state.setsToWin > 1
            val isButtonEnabled =
                state.tournamentType != null
                        && tournamentNameState.text.toString().isNotBlank()
                        && decidingSetTemplate != null && decidingSetTemplate.id != "0"
                        && selectedTheme != null
                        && (!needsRegularSet || (regularSetTemplate != null && regularSetTemplate.id != "0"))
                        && !state.isAdding

            Button(
                onClick = {
                    onEvent(
                        AddTournamentUiEvent.AddTournament(
                            tournamentName = tournamentNameState.text.toString(),
                            tournamentType = tournamentType!!,
                            defaultSetTemplateId = regularSetTemplate!!.id,
                            decidingSetTemplateId = decidingSetTemplate!!.id,
                            themeId = selectedTheme!!.id,
                            setsToWin = state.setsToWin,
                        )
                    )
                },
                enabled = isButtonEnabled
            ) {
                Text("Add")
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
