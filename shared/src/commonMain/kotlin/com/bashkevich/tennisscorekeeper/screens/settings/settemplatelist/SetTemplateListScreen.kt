package com.bashkevich.tennisscorekeeper.screens.settings.settemplatelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.set_template.SetTemplateCombobox
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.TiebreakMode
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.games_to_win
import tennisscorekeeper.shared.generated.resources.has_deciding_point
import tennisscorekeeper.shared.generated.resources.has_tiebreak_deciding_point
import tennisscorekeeper.shared.generated.resources.navigate_back
import tennisscorekeeper.shared.generated.resources.set_template_not_selected
import tennisscorekeeper.shared.generated.resources.set_templates
import tennisscorekeeper.shared.generated.resources.tiebreak_mode
import tennisscorekeeper.shared.generated.resources.tiebreak_mode_early
import tennisscorekeeper.shared.generated.resources.tiebreak_mode_late
import tennisscorekeeper.shared.generated.resources.tiebreak_mode_no
import tennisscorekeeper.shared.generated.resources.tiebreak_points_to_win

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTemplateListScreen(
    viewModel: SetTemplateListViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navController = LocalNavHostController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.set_templates)) },
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
    ) { paddingValues ->
        val componentState = state.setTemplateComponentState

        val selectedSetTemplate =
            (componentState.selectedSetState as? SetComponentState.SelectedSetState.Idle)?.setTemplate

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 400.dp).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    SetTemplateCombobox(
                        enabled = true,
                        setComponentState = componentState,
                        onSetTemplatesFetch = { viewModel.onEvent(SetTemplateListUiEvent.FetchSetTemplates) },
                        onSetTemplateChange = { setTemplate ->
                            viewModel.onEvent(SetTemplateListUiEvent.SelectSetTemplate(setTemplate))
                        },
                        onRetrySelectedSet = {},
                    )
                }

                if (selectedSetTemplate == null) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(Res.string.set_template_not_selected),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    item {
                        ReadOnlyValueRow(
                            label = stringResource(Res.string.games_to_win),
                            value = selectedSetTemplate.gamesToWin.toString(),
                        )
                    }

                    item {
                        DisabledComboboxField(
                            value = tiebreakModeLabel(selectedSetTemplate),
                            label = stringResource(Res.string.tiebreak_mode),
                        )
                    }

                    item {
                        ReadOnlyValueRow(
                            label = stringResource(Res.string.tiebreak_points_to_win),
                            value = selectedSetTemplate.tiebreakPointsToWin.toString(),
                        )
                    }

                    item {
                        DisabledCheckboxRow(
                            label = stringResource(Res.string.has_deciding_point),
                            checked = selectedSetTemplate.hasDecidingPoint,
                        )
                    }

                    item {
                        DisabledCheckboxRow(
                            label = stringResource(Res.string.has_tiebreak_deciding_point),
                            checked = selectedSetTemplate.hasTiebreakDecidingPoint,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun tiebreakModeLabel(setTemplate: SetTemplate): String = when (setTemplate.tiebreakMode) {
    TiebreakMode.NO -> stringResource(Res.string.tiebreak_mode_no)
    TiebreakMode.EARLY -> stringResource(
        Res.string.tiebreak_mode_early,
        setTemplate.gamesToWin - 1,
        setTemplate.gamesToWin - 1,
    )
    TiebreakMode.LATE -> stringResource(
        Res.string.tiebreak_mode_late,
        setTemplate.gamesToWin,
        setTemplate.gamesToWin,
    )
}

@Composable
private fun lockedTextFieldColors() = TextFieldDefaults.colors(
    disabledIndicatorColor = Color.Transparent,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
private fun ReadOnlyValueRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "$label:")

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = value)
    }
}

@Composable
private fun DisabledComboboxField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)

        TextField(
            modifier = Modifier.width(220.dp),
            state = TextFieldState(value),
            readOnly = true,
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = IconGroup.Default.ArrowDropDown,
                    contentDescription = null,
                )
            },
            colors = lockedTextFieldColors(),
        )
    }

}

@Composable
private fun DisabledCheckboxRow(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)

        Checkbox(
            checked = checked,
            onCheckedChange = null,
            enabled = false,
        )
    }
}
