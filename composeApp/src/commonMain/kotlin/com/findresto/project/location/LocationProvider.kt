package com.findresto.project.location

import com.findresto.project.network.models.LocationData

interface LocationProvider {
    suspend fun getCurrentLocation(): LocationData?
}