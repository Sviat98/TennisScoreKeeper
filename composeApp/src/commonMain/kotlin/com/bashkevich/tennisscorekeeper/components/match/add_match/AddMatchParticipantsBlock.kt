package com.bashkevich.tennisscorekeeper.components.match.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.components.participant.AddMatchParticipantComponent
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch

@Composable
fun AddMatchParticipantsBlock(
    modifier: Modifier = Modifier,
    participantOptions: List<TennisParticipant>,
    firstParticipant: TennisParticipantInMatch,
    secondParticipant: TennisParticipantInMatch,
    onParticipantsFetch: () -> Unit,
    onParticipantChange: (Int, TennisParticipant) -> Unit,
    onColorPickerOpen: (Int, Int) -> Unit,
    onToggleSecondaryColor: (Int, Color?) -> Unit
) {

    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    val firstParticipantOptions =
        participantOptions.filter { it.id != secondParticipant.id }
    val secondParticipantOptions =
        participantOptions.filter { it.id != firstParticipant.id }
    Box(modifier = Modifier.then(modifier)) {
        if (windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            Row(
                modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth().align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AddMatchParticipantComponent(
                    modifier = Modifier.weight(weight = 1f),
                    participantOptions = firstParticipantOptions,
                    participant = firstParticipant,
                    onParticipantsFetch = onParticipantsFetch,
                    onParticipantChange = { participant ->
                        onParticipantChange(1, participant)
                    },
                    participantPrimaryColor = firstParticipant.primaryColor,
                    participantSecondaryColor = firstParticipant.secondaryColor,
                    onColorPickerOpen = { colorNumber ->
                        onColorPickerOpen(1, colorNumber)
                    },
                    onToggleSecondaryColor = { color ->
                        onToggleSecondaryColor(1, color)
                    }
                )

                AddMatchParticipantComponent(
                    modifier = Modifier.weight(weight = 1f),
                    participantOptions = secondParticipantOptions,
                    participant = secondParticipant,
                    onParticipantsFetch = onParticipantsFetch,
                    onParticipantChange = { participant ->
                        onParticipantChange(2, participant)
                    },
                    participantPrimaryColor = secondParticipant.primaryColor,
                    participantSecondaryColor = secondParticipant.secondaryColor,
                    onColorPickerOpen = { colorNumber ->
                        onColorPickerOpen(2, colorNumber)
                    },
                    onToggleSecondaryColor = { color ->
                        onToggleSecondaryColor(2, color)
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddMatchParticipantComponent(
                    participantOptions = firstParticipantOptions,
                    participant = firstParticipant,
                    onParticipantsFetch = onParticipantsFetch,
                    onParticipantChange = { participant ->
                        onParticipantChange(1, participant)
                    },
                    participantPrimaryColor = firstParticipant.primaryColor,
                    participantSecondaryColor = firstParticipant.secondaryColor,
                    onColorPickerOpen = { colorNumber ->
                        onColorPickerOpen(1, colorNumber)
                    },
                    onToggleSecondaryColor = { color ->
                        onToggleSecondaryColor(1, color)
                    }
                )

                AddMatchParticipantComponent(
                    participantOptions = secondParticipantOptions,
                    participant = secondParticipant,
                    onParticipantsFetch = onParticipantsFetch,
                    onParticipantChange = { participant ->
                        onParticipantChange(2, participant)
                    },
                    participantPrimaryColor = secondParticipant.primaryColor,
                    participantSecondaryColor = secondParticipant.secondaryColor,
                    onColorPickerOpen = { colorNumber ->
                        onColorPickerOpen(2, colorNumber)
                    },
                    onToggleSecondaryColor = { color ->
                        onToggleSecondaryColor(2, color)
                    }
                )
            }
        }
    }
}