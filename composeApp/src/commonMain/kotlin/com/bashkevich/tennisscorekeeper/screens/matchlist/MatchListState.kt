package com.bashkevich.tennisscorekeeper.screens.matchlist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.match.domain.ShortMatch

import com.bashkevich.tennisscorekeeper.mvi.UiState

@Immutable
data class MatchListState(
    val matches: List<ShortMatch>
) : UiState {
    companion object {
        fun initial() = MatchListState(
            matches = emptyList()
        )
    }
}