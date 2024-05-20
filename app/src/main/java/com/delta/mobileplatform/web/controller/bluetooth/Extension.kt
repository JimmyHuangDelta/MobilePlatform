package com.delta.mobileplatform.web.controller.bluetooth


import com.delta.mobileplatform.model.receive.PrintParams
import com.delta.mobileplatform.model.response.CommandExecuteResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.Exception


fun JsonElement.toPrintParams(): PrintParams {
    return try {
        Json.decodeFromJsonElement(this)
    } catch (e: Exception) {
        throw CommandExecuteResult.Fail(
            methods = BluetoothController.methodPrint,
            message = "$this is invalid"
        )
    }
}

