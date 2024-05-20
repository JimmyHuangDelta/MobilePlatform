package com.delta.mobileplatform.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalStorageResponse(
    val key: String,
    val value: String
)
