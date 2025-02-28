package com.bashkevich.tennisscorekeeper.screens.addcounterdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import com.bashkevich.tennisscorekeeper.screens.addcounterdialog.AddCounterDialogViewModel

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
import com.bashkevich.tennisscorekeeper.components.InteractiveButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddCounterDialogScreen(
    modifier: Modifier = Modifier,
    viewModel: AddCounterDialogViewModel = koinViewModel(),
    onDismissRequest:()->Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }


    val buttonBackgroundColor = MaterialTheme.colors.primary

     AddCounterDialogContent(
         state = state,
         onEvent = {viewModel.onEvent(it)},
         onDismissRequest = onDismissRequest
     )
}

@Composable
fun AddCounterDialogContent(
    modifier: Modifier = Modifier,
    state: AddCounterDialogState,
    onEvent: (AddCounterDialogUiEvent)->Unit,
    onDismissRequest: () -> Unit= {},
    ) {

    val counterName = rememberTextFieldState()

    Column(
        modifier = Modifier.then(modifier).background(MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            state = counterName
        )
        Button(
            onClick = {
                onEvent(AddCounterDialogUiEvent.AddCounter(counterName.text.toString()))
                onDismissRequest()
            },
        ) {
            Text("Add")
        }
    }
}