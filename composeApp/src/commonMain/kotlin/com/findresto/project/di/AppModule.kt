package com.findresto.project.di



import com.findresto.project.network.ApiManager
import com.findresto.project.screens.RestaurantsViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { ApiManager() }
    viewModel { RestaurantsViewModel(api = get()) }

}
