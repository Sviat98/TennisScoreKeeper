package com.bashkevich.tennisscorekeeper.components.match_details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.match_details.serve.ChooseServePanel
import com.bashkevich.tennisscorekeeper.components.match_details.serve.ServeChangeBanner
import com.bashkevich.tennisscorekeeper.components.match_details.serve.ServeChangePanel
import com.bashkevich.tennisscorekeeper.model.match.domain.Match
import com.bashkevich.tennisscorekeeper.model.match.remote.SpecialSetMode
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.domain.SAMPLE_MATCH
import com.bashkevich.tennisscorekeeper.model.participant.domain.ParticipantInDoublesMatch
import com.bashkevich.tennisscorekeeper.screens.matchdetails.ConnectionState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.connection_with_scoreboard_lost

@Composable
fun ScoreboardControlPanel(
    modifier: Modifier = Modifier,
    connectionState: ConnectionState,
    match: Match,
    onEvent: (MatchDetailsUiEvent) -> Unit
) {
    Box(modifier = Modifier.then(modifier)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MatchStatusButton(
                match = match,
                onStatusChange = { status ->
                    onEvent(
                        MatchDetailsUiEvent.ChangeMatchStatus(
                            status = status
                        )
                    )
                }
            )

            when (match.status) {
                MatchStatus.NOT_STARTED -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ChooseServePanel(
                        modifier = Modifier.fillMaxWidth(),
                        match = match,
                        onFirstParticipantToServeChoose = { participantId ->
                            onEvent(
                                MatchDetailsUiEvent.SetFirstParticipantToServe(
                                    participantId = participantId
                                )
                            )
                        },
                        onFirstPlayerInPairToServeChoose = { playerId ->
                            onEvent(
                                MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                    playerId = playerId
                                )
                            )
                        }
                    )
                }

                MatchStatus.IN_PROGRESS -> {
                    val isDoublesMatch = match.firstParticipant is ParticipantInDoublesMatch &&
                            match.secondParticipant is ParticipantInDoublesMatch

                    // Окно смены подающего в парном матче (зеркало правил DoublesMatchService.updateServeInPair
                    // на бэкенде): текущего подающего можно менять, пока его пара не подала в этом сете
                    // (в супер-тайбрейке - пока не сыгран второй розыгрыш), следующего - пока его пара
                    // не начала подавать (в обычном сете - до начала второго гейма). В обычном сете games
                    // в currentSet - выигранные геймы сета, в супер-тайбрейке - сыгранные розыгрыши;
                    // currentGame не равен null только внутри незавершенного гейма обычного сета
                    val currentSetScoreSum = (match.currentSet?.firstParticipantGamesWon ?: 0) +
                            (match.currentSet?.secondParticipantGamesWon ?: 0)
                    val isServingNowPlayerChangeable = currentSetScoreSum <= 1 &&
                            (match.currentSetMode == SpecialSetMode.SUPER_TIEBREAK || match.currentGame == null)
                    val isServingNextPlayerChangeable = currentSetScoreSum == 0

                    val isServeChangeWindowOpen = isDoublesMatch && (
                            isServingNowPlayerChangeable || isServingNextPlayerChangeable
                            )

                    var isServeChangePanelExpanded by remember { mutableStateOf(false) }

                    // окно смены закрылось (в т.ч. розыгрыш сыграли с другого устройства) -
                    // прячем панель и баннер анимированно
                    LaunchedEffect(isServeChangeWindowOpen) {
                        if (!isServeChangeWindowOpen) {
                            isServeChangePanelExpanded = false
                        }
                    }

                    Column {
                        ServeChangeBanner(
                            modifier = Modifier.fillMaxWidth(),
                            visible = isServeChangeWindowOpen,
                            expanded = isServeChangePanelExpanded,
                            onToggleClick = { isServeChangePanelExpanded = !isServeChangePanelExpanded }
                        )

                        AnimatedContent(
                            targetState = isServeChangeWindowOpen && isServeChangePanelExpanded,
                            transitionSpec = {
                                (fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearOutSlowInEasing
                                    )
                                ) + expandVertically(
                                    animationSpec = tween(
                                        durationMillis = 350,
                                        easing = FastOutSlowInEasing
                                    ),
                                    expandFrom = Alignment.Top
                                )) togetherWith
                                        (fadeOut(
                                            animationSpec = tween(
                                                durationMillis = 200,
                                                easing = FastOutLinearInEasing
                                            )
                                        ) + shrinkVertically(
                                            animationSpec = tween(
                                                durationMillis = 350,
                                                easing = FastOutSlowInEasing
                                            ),
                                            shrinkTowards = Alignment.Top
                                        ))
                            },
                            label = "serveChangePanelSwitch"
                        ) { isServeChangePanelShown ->
                            if (isServeChangePanelShown) {
                                ServeChangePanel(
                                    modifier = Modifier.fillMaxWidth(),
                                    match = match,
                                    isServingNowPlayerChangeable = isServingNowPlayerChangeable,
                                    isServingNextPlayerChangeable = isServingNextPlayerChangeable,
                                    onPlayerChoose = { playerId ->
                                        onEvent(
                                            MatchDetailsUiEvent.SetFirstPlayerInPairToServe(
                                                playerId = playerId
                                            )
                                        )
                                    }
                                )
                            } else {
                                ParticipantsPointsControlPanel(
                                    modifier = Modifier.fillMaxWidth(),
                                    match = match,
                                    onUpdateScore = { participantId, scoreType ->
                                        onEvent(
                                            MatchDetailsUiEvent.UpdateScore(
                                                participantId = participantId,
                                                scoreType = scoreType
                                            )
                                        )
                                    },
                                    onUndoPoint = { onEvent(MatchDetailsUiEvent.UndoPoint) },
                                    onRedoPoint = { onEvent(MatchDetailsUiEvent.RedoPoint) }
                                )
                            }
                        }
                    }
                }

                MatchStatus.PAUSED -> {
                    RetireParticipantPanel(
                        modifier = Modifier.fillMaxWidth(),
                        match = match,
                        onParticipantRetire = { participantId ->
                            onEvent(
                                MatchDetailsUiEvent.SetParticipantRetired(
                                    participantId = participantId
                                )
                            )
                        }
                    )
                }

                else -> {}
            }
        }
        if (connectionState == ConnectionState.Disconnected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = RoundedCornerShape(32.dp))
                    .border(
                        border = BorderStroke(width = 1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.connection_with_scoreboard_lost),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScoreboardControlPanelDisconnectedPreview() {
    ScoreboardControlPanel(
        connectionState = ConnectionState.Disconnected,
        match = SAMPLE_MATCH,
        onEvent = {}
    )
}