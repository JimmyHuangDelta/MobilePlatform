package com.delta.mobileplatform.web.controller.networkState


interface Connectivity {
    fun startDetect()
    fun closeDetect()
    fun isNetworkAvailable(): Boolean
    val connectStateFlow: Status

    enum class Status {
        Available, Unavailable, Losing, Lost
    }

}