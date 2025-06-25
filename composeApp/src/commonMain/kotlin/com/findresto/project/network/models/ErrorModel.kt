package com.findresto.project.network.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorModel(
    val code: Int = 200,
    val message: String = ""
)