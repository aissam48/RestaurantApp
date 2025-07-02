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

    @BeforeTest
    fun setUp() {
        val fakeApiManager = ApiManager()
        val fakeLocationProvider = object : LocationProvider {
            override suspend fun getCurrentLocation() = null
        }

        val repository = RestaurantsRepository(fakeApiManager, fakeLocationProvider)
        viewModel = RestaurantsViewModel(repository)

        val restaurents = Json.decodeFromString<List<RestaurantModel>>(mockRestaurantsJson)

        viewModel.filteredList.addAll(restaurents)

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
    fun example() {
        assertEquals(3, 1 + 2)
    }
}