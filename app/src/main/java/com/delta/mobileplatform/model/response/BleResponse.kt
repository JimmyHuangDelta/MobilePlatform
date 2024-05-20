package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BleResponse(
    val uuid: String,
    val major: String,
    val minor: String
)
