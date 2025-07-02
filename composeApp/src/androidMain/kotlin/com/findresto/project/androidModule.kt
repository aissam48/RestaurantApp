package com.findresto.project

import com.findresto.project.location.AndroidLocationProvider
import com.findresto.project.location.LocationProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val androidModule = module {
    single<LocationProvider> { AndroidLocationProvider(androidApplication()) }
}