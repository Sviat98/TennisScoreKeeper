package com.bashkevich.tennisscorekeeper.components.match_details.serve

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.participant.domain.PARTICIPANT_IN_SINGLES_MATCH_DEFAULT
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch

@Composable
fun ChooseServePanel(
    modifier: Modifier = Modifier,
    match: Match,
    onFirstParticipantToServeChoose: (String) -> Unit,
    onFirstPlayerInPairToServeChoose: (String) -> Unit
) {
    val firstParticipant = match.firstParticipant
    val secondParticipant = match.secondParticipant

    val participantOptions = listOf(firstParticipant, secondParticipant)

    val firstParticipantToServe =
        participantOptions.firstOrNull { it.isServing } ?: PARTICIPANT_IN_SINGLES_MATCH_DEFAULT

    val isDoublesMatch = firstParticipant is ParticipantInDoublesMatch && secondParticipant is ParticipantInDoublesMatch

    Box(modifier = Modifier.then(modifier)){
        Column(
            modifier = Modifier.align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            val firstParticipantToServeLabel = if (isDoublesMatch) "First pair to serve" else "First player to serve"
            FirstServeParticipantComponent(
                label = firstParticipantToServeLabel,
                participantOptions = participantOptions,
                currentParticipant = firstParticipantToServe,
                onParticipantChange = { participantId -> onFirstParticipantToServeChoose(participantId) }
            )
            if (isDoublesMatch) {
                FirstServePlayerInPairBlock(
                    participantOptions = participantOptions,
                    firstParticipantToServe = firstParticipantToServe,
                    onFirstPlayerInPairToServeChoose = onFirstPlayerInPairToServeChoose
                )
            }
        }
    }

}