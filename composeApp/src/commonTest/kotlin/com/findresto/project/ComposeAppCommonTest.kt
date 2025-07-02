package com.findresto.project

import com.findresto.project.location.LocationProvider
import com.findresto.project.network.ApiManager
import com.findresto.project.network.models.RestaurantModel
import com.findresto.project.resources.mockRestaurantsJson
import com.findresto.project.screens.RestaurantsRepository
import com.findresto.project.screens.RestaurantsUiState
import com.findresto.project.screens.RestaurantsViewModel
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeAppCommonTest {

    private lateinit var viewModel: RestaurantsViewModel
    private lateinit var repository: RestaurantsRepository


    @BeforeTest
    fun setUp() {
        val fakeApiManager = ApiManager()
        val fakeLocationProvider = object : LocationProvider {
            override suspend fun getCurrentLocation() = null
        }

        repository = RestaurantsRepository(fakeApiManager, fakeLocationProvider)
        viewModel = RestaurantsViewModel(repository)

        val restaurants = Json.decodeFromString<List<RestaurantModel>>(mockRestaurantsJson)

        viewModel.filteredList.addAll(restaurants)

    }

    @Test
    fun failedTestSetSearchFiltersByNameAndDescription() {
        viewModel.setSearch("pizza")
        val state = viewModel.uiState.value
        val result = (state as RestaurantsUiState.Success).data

        assertEquals(0, result.size)
    }

    @Test
    fun successTestSetSearchFiltersByNameAndDescription() {
        viewModel.setSearch("Le Gourmet")
        val state = viewModel.uiState.value
        val result = (state as RestaurantsUiState.Success).data

        assertEquals(1, result.size)
        assertEquals("Le Gourmet Casablanca", result[0].name)
    }

    @Test
    fun restaurantsIn3KM(){

        // my current location in casablanca
        //33.5854580006882, -7.6393468700222575
        val lat = 33.5854580006882
        val lon = -7.6393468700222575

        val nearbyRestaurants = repository.filterNearbyRestaurants(viewModel.filteredList, lat, lon, 3.0)

        println("nearbyRestaurants ${nearbyRestaurants.size}")
        assertEquals(2, nearbyRestaurants.size)

    }


    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }
}