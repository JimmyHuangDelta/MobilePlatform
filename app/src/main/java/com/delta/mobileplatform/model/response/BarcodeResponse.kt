package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeResponse(
    val label: String
)