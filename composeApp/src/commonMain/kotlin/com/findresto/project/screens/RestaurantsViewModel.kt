package com.findresto.project.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findresto.project.network.ApiManager
import com.findresto.project.network.models.ErrorModel
import com.findresto.project.network.models.RestaurantModel
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.mobile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

sealed class RestaurantsUiState {
    data object Idle : RestaurantsUiState()
    data object Loading : RestaurantsUiState()
    data class Success(val data: List<RestaurantModel> = emptyList()) : RestaurantsUiState()
    data class Error(val error: ErrorModel) : RestaurantsUiState()
}

class RestaurantsViewModel(private val api: ApiManager) : ViewModel() {

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

    private val geoLocator: Geolocator = Geolocator.mobile()

    private val _uiState = MutableStateFlow<RestaurantsUiState>(RestaurantsUiState.Idle)
    val uiState: StateFlow<RestaurantsUiState> = _uiState.asStateFlow()

    private val filteredList = mutableListOf<RestaurantModel>()


    init {
        askForCurrentLocation()
    }

    private fun askForCurrentLocation() {
        viewModelScope.launch {
            when (val result: GeolocatorResult = geoLocator.current()) {
                is GeolocatorResult.Success -> {

                    val currentLocation = result.data
                    fetchRestaurants(
                        currentLocation.coordinates.latitude,
                        currentLocation.coordinates.longitude
                    )
                }

                is GeolocatorResult.Error -> {

                    fetchRestaurants(
                        null,
                        null
                    )
                }
            }
        }

    }

    private fun fetchRestaurants(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            _uiState.value = RestaurantsUiState.Loading

            //simulation loading data from server
            delay(3000)

            api.fetchRestaurants({ data ->
                //when change location must clear list to add new items
                filteredList.clear()
                if (latitude == null || longitude == null) {
                    filteredList.addAll(data)
                    _uiState.value = RestaurantsUiState.Success(data = data)
                } else {
                    filterWith3Km(data, latitude, longitude) { result ->
                        _uiState.value = RestaurantsUiState.Success(data = result)
                    }
                }

            }, { error ->
                _uiState.value = RestaurantsUiState.Error(error)
            })
        }
    }

    private fun filterWith3Km(
        data: List<RestaurantModel>,
        userLat: Double, userLon: Double,
        result: (List<RestaurantModel>) -> Unit
    ) {

        for (restaurant in data) {
            //33.585237,-7.638828, this casablanca location
            if (in3Km(userLat, userLon, restaurant.latitude, restaurant.longitude)) {
                filteredList.add(restaurant)
            }
        }

        result(filteredList)

    }

    private fun in3Km(
        userLat: Double, userLon: Double,
        restaurantLat: Double, restaurantLon: Double
    ): Boolean {
        val earthRadiusKm = 6371.0

        val dLat = (restaurantLat - userLat) * PI / 180.0
        val dLon = (restaurantLon - userLon) * PI / 180.0

        val a = sin(dLat / 2).pow(2) +
                cos(userLat * PI / 180) * cos(restaurantLat * PI / 180) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = earthRadiusKm * c
        return distance <= 3.0
    }

    fun setSearch(value: String) {
        _search.value = value
        val result = mutableListOf<RestaurantModel>()
        result.addAll(filteredList.filter {
            it.name.lowercase().contains(value.lowercase()) || it.description.lowercase()
                .contains(value.lowercase())
        })
        _uiState.value = RestaurantsUiState.Success(data = result)
    }

    fun refresh() {
        askForCurrentLocation()
    }


}