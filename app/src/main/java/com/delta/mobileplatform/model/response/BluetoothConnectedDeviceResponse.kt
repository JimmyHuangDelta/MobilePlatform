package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BluetoothConnectedDeviceResponse(
    val name: String,
    val address: String,
)