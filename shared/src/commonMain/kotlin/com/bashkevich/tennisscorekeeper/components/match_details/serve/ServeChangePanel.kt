package com.bashkevich.tennisscorekeeper.components.match_details.serve

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.current_player_to_serve
import tennisscorekeeper.shared.generated.resources.next_player_to_serve

@Composable
fun ServeChangePanel(
    modifier: Modifier = Modifier,
    match: Match,
    isServingNowPlayerChangeable: Boolean,
    isServingNextPlayerChangeable: Boolean,
    onPlayerChoose: (Int) -> Unit
) {
    val participantOptions = listOf(match.firstParticipant, match.secondParticipant)

    val servingParticipant = participantOptions.first { it.isServing }

    // группа центрируется целиком, а строки внутри выровнены по правому краю -
    // комбобоксы встают строго друг под другом
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FirstServePlayerInPairBlock(
            participantOptions = participantOptions,
            firstParticipantToServe = servingParticipant,
            firstPlayerLabel = stringResource(Res.string.current_player_to_serve),
            nextPlayerLabel = stringResource(Res.string.next_player_to_serve),
            firstPlayerEnabled = isServingNowPlayerChangeable,
            nextPlayerEnabled = isServingNextPlayerChangeable,
            onFirstPlayerInPairToServeChoose = onPlayerChoose
        )
    }
}
