package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiResponse(
    val code: Int,
    val header: JsonElement?,
    val body: JsonElement?
)
