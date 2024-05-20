package com.delta.mobileplatform.web.controller.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.BluetoothConnectedDeviceResponse
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.ConnectionException
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import javax.inject.Inject

class BluetoothController @Inject constructor(
    @ActivityContext val context: Context,
    private val commandQueue: Channel<String>,
    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
) : JsonRpcScript {
    private val bluetoothManager by lazy { context.getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter by lazy { bluetoothManager.adapter }

    companion object {
        const val name = "BluetoothInterface"
        const val methodPrint = "$name.print"
        const val methodJumpToSettings = "$name.jumpToSettings"
        const val methodGetConnectDevice = "$name.getConnectDevice"
        const val PARAMS_NULL = "Params is invalid or null"
    }

    private val activity by lazy { context as AppCompatActivity }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            methodPrint to { print(it.params) },
            methodJumpToSettings to { jumpToSettings() },
            methodGetConnectDevice to { getConnectDevice() },
        )

    private fun print(params: JsonElement?) {
        params?.let {
            val receiveData = it.toPrintParams()
            val connection = BluetoothConnection(receiveData.address)

            try {
                Log.d(name, "open")
                connection.open()
            } catch (e: ConnectionException) {
                throw CommandExecuteResult.Fail(
                    methods = methodPrint,
                    message = e.message.toString()
                )
            }

            try {
                Log.d(name, "print")
                connection.write(receiveData.printData.toByteArray())
                val result = CommandExecuteResult.Success(
                    methods = methodPrint,
                    data = JsonNull
                ).toJsResponse()
                scope.launch { commandQueue.send(result) }
            } catch (e: ConnectionException) {
                throw CommandExecuteResult.Fail(
                    methods = methodPrint,
                    message = e.message.toString()
                )
            }

            try {
                Log.d(name, "close")
                connection.close()
            } catch (e: ConnectionException) {
                throw CommandExecuteResult.Fail(
                    methods = methodPrint,
                    message = e.message.toString()
                )
            }
        } ?: throw CommandExecuteResult.Fail(
            methods = methodPrint,
            message = PARAMS_NULL
        )
    }

    private fun getConnectDevice() {
        val connectedList = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw CommandExecuteResult.Fail(
                methods = methodPrint,
                message = "need to granted the BLUETOOTH_CONNECT permission"
            )
        } else {
            bluetoothAdapter.bondedDevices
                .filter { isDeviceConnected(it) }
                .map {
                    BluetoothConnectedDeviceResponse(
                        name = it.name,
                        address = it.address
                    )
                }
        }

        val result = CommandExecuteResult.Success(
            methods = methodGetConnectDevice, data = connectedList
        ).toJsResponse()

        scope.launch { commandQueue.send(result) }
    }

    private fun jumpToSettings() {
        activity.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }

    private fun isDeviceConnected(device: BluetoothDevice): Boolean {
        return try {
            device.javaClass.getMethod("isConnected").invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

}


