package com.delta.mobileplatform.model.receive

import kotlinx.serialization.Serializable

@Serializable
data class MediaParams(
    val url: String = "",
    val type: List<String> = emptyList(),
    val header: MediaHeader,
    val ticket: String = "",
)

