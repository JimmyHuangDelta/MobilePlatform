package com.delta.mobileplatform.model.receive

import kotlinx.serialization.Serializable

@Serializable
data class PrintParams(
    val address: String,
    val printData: String,
)