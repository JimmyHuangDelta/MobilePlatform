package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable

@Serializable
data class NetworkStateResponse(
    val isInternet: Boolean
)