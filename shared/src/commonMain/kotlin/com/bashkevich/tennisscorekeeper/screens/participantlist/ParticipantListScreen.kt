package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Autorenew
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Download
import com.bashkevich.tennisscorekeeper.components.participant.ParticipantListComponent
import com.bashkevich.tennisscorekeeper.components.participant.SeededPlayersCountComponent
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.ParticipantListLoadingState
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentTabMaxWidth
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.couldnt_load_data
import tennisscorekeeper.shared.generated.resources.load
import tennisscorekeeper.shared.generated.resources.participant_list_empty
import tennisscorekeeper.shared.generated.resources.pull_down_to_update
import tennisscorekeeper.shared.generated.resources.seeded_players_count
import tennisscorekeeper.shared.generated.resources.update

@Composable
fun ParticipantListScreen(
    modifier: Modifier = Modifier,
    participantListLoadingState: ParticipantListLoadingState,
    tournament: Tournament?,
    onDownloadParticipants: (Int) -> Unit,
    onSeedAmountChange: (Int) -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        when (participantListLoadingState) {
            is ParticipantListLoadingState.Loading -> {
                // тут НЕ делаем verticalScroll, в состояни Loading refresh не должен быть доступен
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ParticipantListLoadingState.InitialError -> {
                Box(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                    ) {
                        Text(stringResource(Res.string.couldnt_load_data), color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            stringResource(Res.string.pull_down_to_update),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is ParticipantListLoadingState.Content -> {
                ParticipantListContent(
                    // widthIn ДО fillMaxSize: иначе ширина фиксируется на весь экран
                    // и ограничение TabWidth не действует
                    modifier = Modifier.widthIn(max = TournamentTabMaxWidth)
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    participantListLoadingState = participantListLoadingState,
                    tournament = tournament,
                    onDownloadParticipants = onDownloadParticipants,
                    onSeedAmountChange = onSeedAmountChange
                )
            }
        }
    }
}

@Composable
private fun ParticipantListContent(
    modifier: Modifier = Modifier,
    participantListLoadingState: ParticipantListLoadingState.Content,
    tournament: Tournament?,
    onDownloadParticipants: (Int) -> Unit,
    onSeedAmountChange: (Int) -> Unit
) {
    val tournamentStatus = tournament?.status ?: TournamentStatus.NOT_STARTED

    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (participantListLoadingState.participants.isEmpty()) {
            if (tournamentStatus != TournamentStatus.COMPLETED) {
                SeededPlayersCountComponent(
                    modifier = Modifier.fillMaxWidth(),
                    seedAmount = participantListLoadingState.seedAmount,
                    onValueChange = onSeedAmountChange
                )
            } else {
                Text(text = "${stringResource(Res.string.seeded_players_count)}: ${participantListLoadingState.seedAmount}")
            }
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Text(text = stringResource(Res.string.participant_list_empty))
                if (tournamentStatus != TournamentStatus.COMPLETED) {
                    Button(
                        onClick = { onDownloadParticipants(participantListLoadingState.seedAmount) },
                        enabled = !participantListLoadingState.isDownloadInProgress
                    ) {
                        if (participantListLoadingState.isDownloadInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 1.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = IconGroup.Default.Download,
                                contentDescription = stringResource(Res.string.load),
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(Res.string.load))
                    }
                }
            }
        } else {
            if (tournamentStatus != TournamentStatus.COMPLETED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SeededPlayersCountComponent(
                        modifier = Modifier.weight(1f),
                        seedAmount = participantListLoadingState.seedAmount,
                        onValueChange = onSeedAmountChange
                    )
                    Button(
                        onClick = { onDownloadParticipants(participantListLoadingState.seedAmount) },
                        enabled = !participantListLoadingState.isDownloadInProgress
                    ) {
                        if (participantListLoadingState.isDownloadInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 1.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = IconGroup.Default.Autorenew,
                                contentDescription = stringResource(Res.string.update),
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(Res.string.update))
                    }
                }
            }

            ParticipantListComponent(
                participants = participantListLoadingState.participants
            )
        }
    }
}
