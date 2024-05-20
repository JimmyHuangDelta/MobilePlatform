package com.delta.mobileplatform.model.response

import kotlinx.serialization.Serializable

enum class ThemeMode {
    dark, light
}

@Serializable
data class UITheme(
    val theme: ThemeMode
)