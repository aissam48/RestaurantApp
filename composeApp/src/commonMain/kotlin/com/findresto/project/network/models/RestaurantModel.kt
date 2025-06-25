package com.findresto.project.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantModel(
    val name: String,
    val description: String,
    @SerialName("image_url")
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val city: String
)