package com.delta.mobileplatform.model.receive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaHeader(
    @SerialName("Authorization")
    val token: String,
    @SerialName("Accept-Language")
    val language: String,
)