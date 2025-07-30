package com.bashkevich.tennisscorekeeper.model.match.remote

import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.participant.remote.ParticipantInShortMatchDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ShortMatchDto(
    @SerialName("id")
    val id: String,
    @SerialName("first_participant")
    val firstParticipant: ParticipantInShortMatchDto,
    @SerialName("second_participant")
    val secondParticipant: ParticipantInShortMatchDto,
    @SerialName("status")
    val status: MatchStatus,
    @SerialName("previous_sets")
    val previousSets: List<TennisSetDto>,
    @SerialName("current_set")
    val currentSet: TennisSetDto?=null,
    @SerialName("current_game")
    val currentGame: TennisGameDto?=null,
)