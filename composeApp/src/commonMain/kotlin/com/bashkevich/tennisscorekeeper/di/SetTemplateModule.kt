package com.bashkevich.tennisscorekeeper.di

import com.bashkevich.tennisscorekeeper.model.set_template.remote.SetTemplateRemoteDataSource
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val setTemplateModule = module {
    singleOf(::SetTemplateRepositoryImpl){
        bind<SetTemplateRepository>()
    }

    singleOf(::SetTemplateRemoteDataSource)
}