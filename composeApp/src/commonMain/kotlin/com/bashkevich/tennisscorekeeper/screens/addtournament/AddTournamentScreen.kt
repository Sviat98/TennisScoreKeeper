package com.bashkevich.tennisscorekeeper.screens.addtournament

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.remote.mapToDisplayedString

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
        onDismissRequest = { navController.navigateUp() }
    )
}

@Composable
fun AddTournamentContent(
    modifier: Modifier = Modifier,
    state: AddTournamentState,
    onEvent: (AddTournamentUiEvent) -> Unit,
    onDismissRequest: () -> Unit = {},
) {
    val tournamentAddingSubstate = state.tournamentAddingSubstate

    if (tournamentAddingSubstate is TournamentAddingSubstate.Success){
        onDismissRequest()
    }

    Column(
        modifier = Modifier.then(modifier).background(MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val tournamentName = state.tournamentName
        val tournamentType = state.tournamentType

        TextField(
            state = tournamentName,
            placeholder = { Text("Tournament name") },
        )
        TournamentTypeCombobox(
            currentTournamentType = tournamentType,
            onTournamentTypeChange = { type ->
                onEvent(AddTournamentUiEvent.SelectTournamentType(type))
            }
        )

        val isButtonEnabled = (state.tournamentType !=null && tournamentName.text.isNotBlank()) && tournamentAddingSubstate !is TournamentAddingSubstate.Loading
        Button(
            onClick = {
                onEvent(AddTournamentUiEvent.AddTournament(tournamentName.text.toString(), tournamentType!!))
            },
            enabled = isButtonEnabled
        ) {
            if (tournamentAddingSubstate is TournamentAddingSubstate.Loading){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            }else{
                Text("Add")
            }
        }
        if (tournamentAddingSubstate is TournamentAddingSubstate.Error){
            Text(text = tournamentAddingSubstate.message, color = Color.Red)
        }
    }
}

@Composable
fun TournamentTypeCombobox(
    currentTournamentType: TournamentType?,
    onTournamentTypeChange: (TournamentType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = TournamentType.entries

    val tournamentTypeText = currentTournamentType?.mapToDisplayedString() ?: ""

    val tournamentTypeState = TextFieldState(tournamentTypeText)

    Box {
        // Поле ввода с заблокированным редактированием
        TextField(
            state = tournamentTypeState,
            placeholder = { Text("Tournament type") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open dropdown",
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledTrailingIconColor = Color.Gray
            )
        )

        // Выпадающее меню
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onTournamentTypeChange(option)
                        expanded = false
                    }
                ) {
                    Text(text = option.mapToDisplayedString())
                }
            }
        }
    }
}

