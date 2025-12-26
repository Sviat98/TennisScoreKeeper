package com.bashkevich.tennisscorekeeper.components.match_details.serve

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch

@Composable
fun FirstServeParticipantComponent(
    modifier: Modifier = Modifier,
    label: String,
    participantOptions: List<TennisParticipantInMatch>,
    currentParticipant: TennisParticipantInMatch,
    onParticipantChange: (String) -> Unit,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        Row(
            modifier = Modifier.then(modifier),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label
            )
            FirstServeParticipantCombobox(
                modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
                participantOptions = participantOptions,
                currentParticipant = currentParticipant,
                onParticipantChange = { participant -> onParticipantChange(participant.id) }
            )
        }

    } else {
        Column(
            modifier = Modifier.then(modifier).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 300.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label
                )
                FirstServeParticipantCombobox(
                    modifier = Modifier.fillMaxWidth(),
                    participantOptions = participantOptions,
                    currentParticipant = currentParticipant,
                    onParticipantChange = { participant -> onParticipantChange(participant.id) }
                )
            }
        }
    }
}