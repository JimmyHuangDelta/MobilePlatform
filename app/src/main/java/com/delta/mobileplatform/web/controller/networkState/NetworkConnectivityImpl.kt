package com.delta.mobileplatform.web.controller.networkState

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.NetworkStateResponse
import com.delta.mobileplatform.model.response.toJsResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val commandQueue: Channel<String>
) : Connectivity, JsonRpcScript {

    private val mutex = Mutex(locked = true)
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Volatile
    private var _connectStateFlow = Connectivity.Status.Unavailable

    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            synchronized(_connectStateFlow) {
                _connectStateFlow = Connectivity.Status.Available
            }

        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            _connectStateFlow = Connectivity.Status.Losing

        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _connectStateFlow = Connectivity.Status.Lost
        }

        override fun onUnavailable() {
            super.onUnavailable()
            _connectStateFlow = Connectivity.Status.Unavailable
        }
    }

    override fun startDetect() {
        synchronized(_connectStateFlow) {
            connectivityManager.registerDefaultNetworkCallback(callback)
            if (mutex.isLocked) mutex.unlock()
        }
    }

    override fun closeDetect() {
        connectivityManager.unregisterNetworkCallback(callback)
    }


    override tailrec fun isNetworkAvailable(): Boolean {
        return if (!mutex.isLocked) connectStateFlow == Connectivity.Status.Available else isNetworkAvailable()
    }

    override val connectStateFlow: Connectivity.Status
        get() = _connectStateFlow

    private fun sentIsNetwork() {
        runBlocking {
            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = getIsInternetMethod,
                    data = NetworkStateResponse(
                        isInternet = isNetworkAvailable()
                    )
                ).toJsResponse()
            )
        }
    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            getIsInternetMethod to { sentIsNetwork() }
        )

    companion object {
        const val name = "NativeInterface"
        const val getIsInternetMethod = "$name.getIsInternet"
    }

}