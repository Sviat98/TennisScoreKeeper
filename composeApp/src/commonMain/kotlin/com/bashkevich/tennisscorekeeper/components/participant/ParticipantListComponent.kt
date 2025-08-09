package com.bashkevich.tennisscorekeeper.components.participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantListItem
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.toDisplayFormat

@Composable
fun ParticipantListComponent(
    modifier: Modifier = Modifier,
    participants: List<TennisParticipant>
){
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val isCompact =
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    var maxSeedWidth by remember(isCompact) { mutableStateOf(0.dp) }
    var maxDisplayNameWidth by remember(isCompact){ mutableStateOf(0.dp) }

    val textStyle = LocalTextStyle.current

    // Предварительное вычисление всех размеров
    val participantListItems =
        participants.map { participant ->
            val seedNumber = participant.seed?.toString() ?: ""

            val currentSeedWidth = with(density) {
                textMeasurer.measure(
                    text = seedNumber,
                    style = textStyle
                ).size.width.toDp()
            }

            if(currentSeedWidth>maxSeedWidth){
                maxSeedWidth = currentSeedWidth
            }

            val displayName = participant.toDisplayFormat(isCompact = isCompact)

            val currentDisplayNameWidth = with(density) {
                textMeasurer.measure(
                    text = displayName,
                    style =textStyle,
                ).size.width.toDp()
            }

            if(currentDisplayNameWidth>maxDisplayNameWidth){
                maxDisplayNameWidth = currentDisplayNameWidth
            }

            ParticipantListItem(
                id = participant.id,
                seedNumber = seedNumber,
                displayName = displayName
            )
        }

    LazyColumn(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = participantListItems,
            key = { it.id }) { participant ->
            ParticipantCard(
                participant = participant,
                maxSeedWidth = maxSeedWidth,
                maxDisplayNameWidth = maxDisplayNameWidth
            )
        }
    }
}