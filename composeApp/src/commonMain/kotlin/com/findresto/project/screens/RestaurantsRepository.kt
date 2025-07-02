package com.findresto.project.screens

import com.findresto.project.location.LocationProvider
import com.findresto.project.network.ApiManager
import com.findresto.project.network.models.ErrorModel
import com.findresto.project.network.models.RestaurantModel
import kotlin.math.pow
import kotlin.math.sqrt

class RestaurantsRepository(
    private val api: ApiManager,
    private val locationProvider: LocationProvider
) {

    suspend fun getCurrentLocation() = locationProvider.getCurrentLocation()

    suspend fun fetchRestaurants(
        onSuccess: (List<RestaurantModel>) -> Unit,
        onFailure: (ErrorModel) -> Unit
    ) {
        api.fetchRestaurants({ data ->
            onSuccess(data)
        }, { error ->
            onFailure(error)
        })
    }

    fun filterNearbyRestaurants(
        data: List<RestaurantModel>,
        lat: Double,
        lon: Double,
        radiusKm: Double
    ): List<RestaurantModel> {
        // my current location in casablanca
        //33.5854580006882, -7.6393468700222575
        return data.filter { restaurant ->
            isIn3km(lat, lon, restaurant.latitude, restaurant.longitude, radiusKm)
            //isIn3km(33.5854580006882, -7.6393468700222575, restaurant.latitude, restaurant.longitude, radiusKm)
        }
    }

    private fun isIn3km(
        userLatitude: Double, userLongitude: Double,
        restaurantLatitude: Double, restaurantLongitude: Double,
        radiusKm: Double
    ): Boolean {

        val latDiff = userLatitude - restaurantLatitude
        val lonDiff = userLongitude - restaurantLongitude

        val distance = sqrt((latDiff * 111).pow(2) + (lonDiff * 111).pow(2))

        println("distance  $distance")
        return distance <= radiusKm
    }
}
