package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MediaResponse(
    val uri: String,
    val stream: String,
)

fun MediaResponse.splitMediaResponse(): List<MediaResponse> {
    val totalLength = stream.length
    val desiredNumParts = 10
    val partLength = totalLength / desiredNumParts
    val responses = mutableListOf<MediaResponse>()

    var startIndex = 0
    for (i in 0 until desiredNumParts) {
        val endIndex = startIndex + partLength
        val streamPart = stream.substring(startIndex, endIndex)
        responses.add(MediaResponse(uri, streamPart))
        startIndex = endIndex
    }

    // Add any remaining characters
    if (startIndex < totalLength) {
        val remainingPart = stream.substring(startIndex)
        responses.add(MediaResponse(uri, remainingPart))
    }

    return responses
}





