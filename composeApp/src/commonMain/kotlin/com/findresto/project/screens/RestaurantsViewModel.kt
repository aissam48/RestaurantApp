package com.findresto.project.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findresto.project.network.models.ErrorModel
import com.findresto.project.network.models.RestaurantModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RestaurantsUiState {
    data object Idle : RestaurantsUiState()
    data object Loading : RestaurantsUiState()
    data class Success(val data: List<RestaurantModel> = emptyList()) : RestaurantsUiState()
    data class Error(val error: ErrorModel) : RestaurantsUiState()
}

class RestaurantsViewModel(
    private val repository: RestaurantsRepository
) : ViewModel() {

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()


    private val _uiState = MutableStateFlow<RestaurantsUiState>(RestaurantsUiState.Idle)
    val uiState: StateFlow<RestaurantsUiState> = _uiState.asStateFlow()

    val filteredList = mutableListOf<RestaurantModel>()

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            askForCurrentLocation()
        }else{
            fetchRestaurants(null, null)

        }
    }

    private fun askForCurrentLocation() {
        viewModelScope.launch {
            val location = repository.getCurrentLocation()
            fetchRestaurants(location?.latitude, location?.longitude)
        }
    }

    private fun fetchRestaurants(lat: Double?, lon: Double?) {
        viewModelScope.launch {
            _uiState.value = RestaurantsUiState.Loading

            delay(3000)

            repository.fetchRestaurants({ data ->
                filteredList.clear()
                val finalList = if (lat != null && lon != null) {
                    repository.filterNearbyRestaurants(data, lat, lon, radiusKm = 3.0)
                } else {
                    data
                }

                filteredList.addAll(finalList)
                _uiState.value = RestaurantsUiState.Success(finalList)
            }, { error ->
                _uiState.value = RestaurantsUiState.Error(error)

            })

        }
    }

    fun setSearch(query: String) {
        _search.value = query
        val result = filteredList.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
        _uiState.value = RestaurantsUiState.Success(result)
    }

    fun refresh() {
        askForCurrentLocation()
    }


}