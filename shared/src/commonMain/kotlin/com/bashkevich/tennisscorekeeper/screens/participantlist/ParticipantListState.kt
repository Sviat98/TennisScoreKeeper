package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.file.domain.EMPTY_EXCEL_FILE
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant

import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
data class ParticipantListState(
    val isUploadInProgress: Boolean,
    val participants: List<TennisParticipant>,
    val participantsFile: ExcelFile
) : UiState {
    companion object {
        fun initial() = ParticipantListState(
            isUploadInProgress = false,
            participants = emptyList(),
            participantsFile = EMPTY_EXCEL_FILE
        )
    }
}