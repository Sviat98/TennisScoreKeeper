package com.bashkevich.tennisscorekeeper.core

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.bashkevich.tennisscorekeeper.model.set_template.local.room.SetTemplateDao
import com.bashkevich.tennisscorekeeper.model.set_template.local.room.SetTemplateEntity
import com.bashkevich.tennisscorekeeper.model.theme.local.room.ThemeDao
import com.bashkevich.tennisscorekeeper.model.theme.local.room.ThemeEntity
import com.bashkevich.tennisscorekeeper.model.tournament.local.room.TournamentDao
import com.bashkevich.tennisscorekeeper.model.tournament.local.room.TournamentEntity

@Database(
    entities = [TournamentEntity::class, SetTemplateEntity::class, ThemeEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
    abstract fun setTemplateDao(): SetTemplateDao
    abstract fun themeDao(): ThemeDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
