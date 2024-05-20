package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class UploadResponse(
    val code: Int,
    val header: JsonElement?,
    val body: UploadFiles?,
) {
}