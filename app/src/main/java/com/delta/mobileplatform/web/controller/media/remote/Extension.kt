package com.delta.mobileplatform.web.controller.media.remote

import android.util.Log
import com.delta.mobileplatform.web.controller.media.MediaController
import com.delta.mobileplatform.web.controller.utils.string.addSlashes
import com.delta.mobileplatform.model.response.ApiResponse
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.UploadFiles
import com.delta.mobileplatform.model.response.UploadResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.Headers
import okhttp3.Response


fun Headers.getFilaName(): String {
    val disposition = this.get("Content-Disposition")

    disposition?.let {
        val parts = disposition.split("; ")
        var filename = ""
        for (part in parts) {
            if (part.startsWith("filename=")) {
                filename = part.substring("filename=".length)
                break
            }
        }
        return filename
    } ?: throw CommandExecuteResult.Fail(
        methods = MediaController.methodDownload,
        message = "Content-Disposition is null"
    )
}

fun Response.toApiResponse(): ApiResponse {
    val parseBody = try {
        Json.encodeToJsonElement(this.body)
    } catch (e: Exception) {
        null
    }
    val headersMap: Map<String, List<String>> = this.headers.toMultimap()

    val jsonHeaders = JsonObject(headersMap.mapValues { (_, values) ->
        Json.encodeToJsonElement(
            addSlashes(values.joinToString(","))
        )
    }) as? JsonElement

    Log.d("body", parseBody.toString())

    return ApiResponse(
        code = this.code,
        body = parseBody,
        header = jsonHeaders
    )
}

fun Response.toUploadResponse(): UploadResponse {
    val responseBodyString = this.body?.string() ?: ""

    val parseBody = try {
        Json.decodeFromString<UploadFiles>(responseBodyString)
    } catch (e: Exception) {
        Log.e("JsonParsingError", "Error parsing JSON: $responseBodyString", e)
        null
    }

    val headersMap: Map<String, List<String>> = this.headers.toMultimap()

    val jsonHeaders = JsonObject(headersMap.mapValues { (_, values) ->
        Json.encodeToJsonElement(
            addSlashes(values.joinToString(","))
        )
    }) as? JsonElement

    return UploadResponse(
        code = this.code,
        body = parseBody,
        header = jsonHeaders
    )
}


