package com.findresto.project

import com.findresto.project.location.IosLocationProvider
import com.findresto.project.location.LocationProvider
import org.koin.dsl.module


val iosModule = module {
    single<LocationProvider> { IosLocationProvider() }
}