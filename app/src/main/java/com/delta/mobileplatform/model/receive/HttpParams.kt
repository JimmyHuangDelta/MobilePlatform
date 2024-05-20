package com.delta.mobileplatform.model.receive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
data class HttpParams(
    val body: JsonElement? = null,
    val datatype: String? = null,
    val header: JsonElement? = null,
    @SerialName("http-method")
    val httpMethod: String = "",
    val url: String = ""
)