package com.bashkevich.tennisscorekeeper.components.add_tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.model.tournament.domain.UpcomingTournament
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.error_loading_lt_tournaments
import tennisscorekeeper.shared.generated.resources.open_dropdown
import tennisscorekeeper.shared.generated.resources.select_lt_tournament
import tennisscorekeeper.shared.generated.resources.start_date

private val upcomingTournamentDateFormat = LocalDate.Format {
    day()
    char('.')
    monthNumber()
    char('.')
    year()
}

@Composable
fun UpcomingTournamentCombobox(
    modifier: Modifier = Modifier,
    upcomingTournamentComponentState: UpcomingTournamentComponentState,
    onUpcomingTournamentsFetch: () -> Unit,
    onUpcomingTournamentChange: (UpcomingTournament) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val windowSize = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWideScreen = windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val selectedTournament = upcomingTournamentComponentState.selectedTournament
    val text = selectedTournament?.let { tournament ->
        val dateText =
            stringResource(Res.string.start_date, upcomingTournamentDateFormat.format(tournament.dateStart))
        if (isWideScreen) "${tournament.name}    $dateText" else "${tournament.name}\n$dateText"
    } ?: ""

    Box(
        modifier = Modifier
            .widthIn(max = if (isWideScreen) 600.dp else 300.dp)
            .then(modifier)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = TextFieldState(text),
            placeholder = {
                Text(
                    text = stringResource(Res.string.select_lt_tournament),
                    textAlign = TextAlign.Center
                )
            },
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = true
                        onUpcomingTournamentsFetch()
                    }
                ) {
                    Icon(
                        imageVector = IconGroup.Default.ArrowDropDown,
                        contentDescription = stringResource(Res.string.open_dropdown),
                    )
                }
            },
            lineLimits = if (isWideScreen) {
                TextFieldLineLimits.SingleLine
            } else {
                TextFieldLineLimits.MultiLine(minHeightInLines = 2, maxHeightInLines = 3)
            },
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = if (isWideScreen) Modifier.widthIn(min = 600.dp) else Modifier,
        ) {
            when (val optionsState = upcomingTournamentComponentState.upcomingTournamentsOptionsState) {
                is UpcomingTournamentComponentState.UpcomingTournamentsOptionsState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }

                is UpcomingTournamentComponentState.UpcomingTournamentsOptionsState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = stringResource(Res.string.error_loading_lt_tournaments))
                    }
                }

                is UpcomingTournamentComponentState.UpcomingTournamentsOptionsState.Idle -> {
                    optionsState.tournaments.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                UpcomingTournamentOptionText(
                                    upcomingTournament = option,
                                    isWideScreen = isWideScreen,
                                )
                            },
                            onClick = {
                                onUpcomingTournamentChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingTournamentOptionText(
    upcomingTournament: UpcomingTournament,
    isWideScreen: Boolean,
) {
    val dateText = stringResource(
        Res.string.start_date,
        upcomingTournamentDateFormat.format(upcomingTournament.dateStart)
    )

    if (isWideScreen) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = upcomingTournament.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            Text(text = dateText)
        }
    } else {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = upcomingTournament.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = dateText)
        }
    }
}
