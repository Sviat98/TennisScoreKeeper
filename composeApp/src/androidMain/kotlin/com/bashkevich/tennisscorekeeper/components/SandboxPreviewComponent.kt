package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.match.add_match.SetsToWinBlock
import com.bashkevich.tennisscorekeeper.components.participant.ParticipantListComponent
import com.bashkevich.tennisscorekeeper.model.participant.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.player.domain.PlayerInParticipant
import com.bashkevich.tennisscorekeeper.screens.participantlist.ParticipantListContent
import com.bashkevich.tennisscorekeeper.screens.participantlist.ParticipantListState
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentState


@Composable
@Preview
fun SandboxPreviewComponent() {
    SandboxComponent()
}

@Composable
@Preview
fun SampleCanvas(
    diameter: Dp = 128.dp
) {
    Canvas(
        modifier = Modifier.requiredSize(diameter)
    ) {
        val canvasSize = this.size.minDimension
        val center = Offset(canvasSize / 2, canvasSize / 2)
        val radius = canvasSize / 2
        val path = Path().apply {
            arcTo(
                rect = Rect(
                    center.x - radius / 2,
                    0f,
                    center.x + radius / 2,
                    center.y
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
        }
        drawPath(path, Color.White)
    }
}

@Composable
@Preview(device = "spec:width=1920dp,height=1080dp,dpi=160")
fun ParticipantListContentPreview() {
    ParticipantListContent(
        state = TournamentState.initial().copy(
            isLoading = false, participantListState = ParticipantListState.initial().copy(
//                participants = listOf(
//                    SinglesParticipant("1", 1, PlayerInParticipant("1", "Djokovic", "Novak")),
//                    SinglesParticipant("2", 10, PlayerInParticipant("1", "Djokovic", "Novak")),
//                    SinglesParticipant("3", null, PlayerInParticipant("1", "Djokovic", "Novak"))
//                )
                participants = listOf(
                    DoublesParticipant(
                        "1",
                        1,
                        firstPlayer = PlayerInParticipant("1", "Djokovic", "Novak"),
                        secondPlayer = PlayerInParticipant("1", "Nadal", "Rafael")
                    ),
                    DoublesParticipant(
                        "2",
                        10,
                        firstPlayer = PlayerInParticipant("1", "Vennegoor of Hesselink", "Jan"),
                        secondPlayer = PlayerInParticipant("1", "Vennegoor of Hesselink", "Lucas")
                    ),
                    DoublesParticipant(
                        "3",
                        null,
                        firstPlayer = PlayerInParticipant("1", "Auger-Aliassime", "Felix"),
                        secondPlayer = PlayerInParticipant("1", "Davidivich Fokina", "Alejandro")
                    ),
                )
            )
        ),
        onEvent = {}
    )
}

@Composable
@Preview(device = "spec:width=411dp,height=891dp")
fun ParticipantListComponentPreview() {
    ParticipantListComponent(
        participants = listOf(
            DoublesParticipant(
                "1",
                1,
                firstPlayer = PlayerInParticipant("1", "Djokovic", "Novak"),
                secondPlayer = PlayerInParticipant("1", "Nadal", "Rafael")
            ),
            DoublesParticipant(
                "2",
                10,
                firstPlayer = PlayerInParticipant("1", "Vennegoor of Hesselink", "Jan"),
                secondPlayer = PlayerInParticipant("1", "Vennegoor of Hesselink", "Lucas")
            ),
            DoublesParticipant(
                "3",
                null,
                firstPlayer = PlayerInParticipant("1", "Auger-Aliassime", "Felix"),
                secondPlayer = PlayerInParticipant("1", "Davidivich Fokina", "Alejandro")
            ),
        )
    )
}


@Composable
@Preview
fun SetsToWinComponentPreview() {
    SetsToWinBlock(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        setsToWin = 1,
        onValueChange = {}
    )
}
