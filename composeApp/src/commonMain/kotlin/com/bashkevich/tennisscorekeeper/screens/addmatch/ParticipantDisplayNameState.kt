package com.bashkevich.tennisscorekeeper.screens.addmatch

import androidx.compose.foundation.text.input.TextFieldState
import com.bashkevich.tennisscorekeeper.model.participant.domain.DoublesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.SinglesParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant

sealed class ParticipantDisplayNameState

data class SinglesParticipantDisplayNameState(
    val playerDisplayName: TextFieldState
) : ParticipantDisplayNameState()

data class DoublesParticipantDisplayNameState(
    val firstPlayerDisplayName: TextFieldState,
    val secondPlayerDisplayName: TextFieldState
) : ParticipantDisplayNameState()

val EMPTY_SINGLES_PARTICIPANT_DISPLAY_NAME =
    SinglesParticipantDisplayNameState(playerDisplayName = TextFieldState(""))
val EMPTY_DOUBLES_PARTICIPANT_DISPLAY_NAME = DoublesParticipantDisplayNameState(
    firstPlayerDisplayName = TextFieldState(""),
    secondPlayerDisplayName = TextFieldState("")
)


fun TennisParticipant.toDisplayNameState() = when (this) {
    is SinglesParticipant -> SinglesParticipantDisplayNameState(
        playerDisplayName = TextFieldState(
            this.player.surname.uppercase()
        )
    )

    is DoublesParticipant -> DoublesParticipantDisplayNameState(
        firstPlayerDisplayName = TextFieldState(
            this.firstPlayer.surname.uppercase()
        ), secondPlayerDisplayName = TextFieldState(this.secondPlayer.surname.uppercase())
    )
}