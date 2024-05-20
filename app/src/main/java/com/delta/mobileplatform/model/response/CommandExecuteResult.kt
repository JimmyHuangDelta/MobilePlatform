package com.delta.mobileplatform.model.response

import com.delta.mobileplatform.model.ExecuteStatusCode
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
sealed interface CommandExecuteResult {
    @Serializable
    data class Success<T> @OptIn(ExperimentalSerializationApi::class) constructor(
        val methods: String,
        val `data`: T,
        @EncodeDefault
        val code: Int = ExecuteStatusCode.success,
        @EncodeDefault
        val from: String = "Android",
        @EncodeDefault
        val message: String = "",
        @EncodeDefault
        val status: String = "success"
    ) : CommandExecuteResult

    @Serializable
    data class Fail @OptIn(ExperimentalSerializationApi::class) constructor(
        val methods: String,
        @EncodeDefault
        val data: JsonElement? = null,
        @EncodeDefault
        val code: Int = ExecuteStatusCode.unProcessable,
        @EncodeDefault
        val from: String = "Android",
        @EncodeDefault
        override val message: String = "",
        @EncodeDefault
        val status: String = "fail"
    ) : CommandExecuteResult, Exception(message)
}


inline fun <reified T> CommandExecuteResult.Success<T>.toJsResponse(): String {
    return Json.encodeToString(this)
}

fun CommandExecuteResult.Fail.toJsResponse(): String {
    return Json.encodeToString(this)
}