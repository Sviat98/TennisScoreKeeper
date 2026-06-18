package com.bashkevich.tennisscorekeeper.core.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantDao
import com.bashkevich.tennisscorekeeper.model.participant.local.ParticipantEntity
import com.bashkevich.tennisscorekeeper.model.player.local.PlayerEntity
import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateDao
import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateEntity
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeDao
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeEntity
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentDao
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentEntity

@Database(
    entities = [
        TournamentEntity::class,
        SetTemplateEntity::class,
        ThemeEntity::class,
        PlayerEntity::class,
        ParticipantEntity::class,
    ],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
    abstract fun setTemplateDao(): SetTemplateDao
    abstract fun themeDao(): ThemeDao
    abstract fun participantDao(): ParticipantDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
