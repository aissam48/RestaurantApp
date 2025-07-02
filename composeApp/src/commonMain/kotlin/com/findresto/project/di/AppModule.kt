package com.findresto.project.di

import com.findresto.project.network.ApiManager
import com.findresto.project.screens.RestaurantsRepository
import com.findresto.project.screens.RestaurantsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {

    single { ApiManager() }
    singleOf(::RestaurantsRepository)
    singleOf(::RestaurantsViewModel)

}
