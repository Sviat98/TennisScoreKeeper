package com.bashkevich.tennisscorekeeper.model.match.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import com.bashkevich.tennisscorekeeper.model.match.remote.MatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.ShortMatchDto
import com.bashkevich.tennisscorekeeper.model.match.remote.TennisSetDto

@Entity(
    tableName = "match_sets",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["match_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["match_id", "set_number"]
)
data class MatchSetEntity(
    @ColumnInfo(name = "match_id")
    val matchId: Int,
    @ColumnInfo(name = "set_number")
    val setNumber: Int,
    @ColumnInfo(name = "is_current")
    val isCurrent: Boolean,
    @ColumnInfo(name = "first_participant_games")
    val firstParticipantGames: Int,
    @ColumnInfo(name = "second_participant_games")
    val secondParticipantGames: Int,
)

fun TennisSetDto.toMatchSetEntity(matchId: Int, setNumber: Int, isCurrent: Boolean) = MatchSetEntity(
    matchId = matchId,
    setNumber = setNumber,
    isCurrent = isCurrent,
    firstParticipantGames = firstParticipantGames,
    secondParticipantGames = secondParticipantGames,
)

fun ShortMatchDto.toMatchSetEntities(): List<MatchSetEntity> {
    val entities = mutableListOf<MatchSetEntity>()
    previousSets.forEachIndexed { index, setDto ->
        entities.add(setDto.toMatchSetEntity(matchId = id.toInt(), setNumber = index + 1, isCurrent = false))
    }
    currentSet?.let {
        entities.add(it.toMatchSetEntity(matchId = id.toInt(), setNumber = previousSets.size + 1, isCurrent = true))
    }
    return entities
}

fun MatchDto.toMatchSetEntities(): List<MatchSetEntity> {
    val entities = mutableListOf<MatchSetEntity>()
    previousSets.forEachIndexed { index, setDto ->
        entities.add(setDto.toMatchSetEntity(matchId = id.toInt(), setNumber = index + 1, isCurrent = false))
    }
    currentSet?.let {
        entities.add(it.toMatchSetEntity(matchId = id.toInt(), setNumber = previousSets.size + 1, isCurrent = true))
    }
    return entities
}
