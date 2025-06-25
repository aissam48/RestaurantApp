package com.findresto.project.network

import com.findresto.project.network.models.ErrorModel
import com.findresto.project.network.models.RestaurantModel
import com.findresto.project.resources.mockRestaurantsJson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiManager() {

    private val baseUrl = ""

    private val client = HttpClient(CIO) {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun fetchRestaurants(
        onSuccess: (List<RestaurantModel>) -> Unit,
        onFailure: (ErrorModel) -> Unit
    ){
            try {

                // this if there is an api that provide list of restaurants

                /*val response: HttpResponse = client.get("") {
                    contentType(ContentType.Application.Json)
                    headers { append(HttpHeaders.Authorization, "Bearer $token") }
                }
                if (response.status.isSuccess()) {
                    val responseBody: List<RestaurantModel> = response.body()
                    onSuccess(responseBody.posts)
                } else {
                    val errorMessage: ErrorModel =response.body()
                    onFailure(errorMessage)
                }*/

                onSuccess(parseMockRestaurants())

            } catch (e: Exception) {

            }

    }

    private fun parseMockRestaurants(): List<RestaurantModel> {
        return Json.decodeFromString(mockRestaurantsJson)
    }

}

