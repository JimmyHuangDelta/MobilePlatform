package com.delta.mobileplatform.model.receive

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class JsCommand(
    val domain: String,
    val methods: String,
    val params: JsonElement? = null
)
