package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable


@Serializable
data class UploadFiles(
    val ticket: String,
    val infos: List<Info>,
)

@Serializable
data class Info(
    val name: String,
)