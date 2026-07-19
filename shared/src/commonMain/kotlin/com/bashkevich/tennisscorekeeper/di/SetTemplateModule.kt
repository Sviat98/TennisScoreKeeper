package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateLocalDataSource
import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepositoryImpl
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val setTemplateModule = module {
    single<SetTemplateRepositoryImpl>().bind(SetTemplateRepository::class)

    single<SetTemplateRemoteDataSource>()
    single<SetTemplateLocalDataSource>()
}
