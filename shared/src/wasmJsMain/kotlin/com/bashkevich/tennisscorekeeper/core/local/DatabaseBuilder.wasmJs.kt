package com.bashkevich.tennisscorekeeper.core.local

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import org.w3c.dom.Worker

actual fun getDatabaseBuilder(
    platformConfiguration: PlatformConfiguration
): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(name = "tennis_db")
        .setDriver(WebWorkerSQLiteDriver(createWorker()))
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun createWorker(): Worker = js("""new Worker(new URL("sqlite-wasm-worker/worker.js", import.meta.url))""")
