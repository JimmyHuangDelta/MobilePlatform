package com.delta.mobileplatform.web.controller.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.delta.mobileplatform.activity.WebActivity
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.web.controller.permission.blePermission
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.BleResponse
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser
import com.neovisionaries.bluetooth.ble.advertising.IBeacon
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonNull
import javax.inject.Inject


class BleController @Inject constructor(
    @ActivityContext private val context: Context,
    private val commandQueue: Channel<String>
) : JsonRpcScript {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    private val scanSettings: ScanSettings by lazy {
        ScanSettings.Builder()
            .setMatchMode(SCAN_MODE_LOW_LATENCY)
            .setReportDelay(1000)
            .build()
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, errorCode.toString())
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {

            val structures = results?.map {
                ADPayloadParser.getInstance().parse(it.scanRecord?.bytes)
            }?.flatten()
            Log.d(TAG, structures?.filterIsInstance<IBeacon>()?.size.toString())

            runBlocking {
                commandQueue.send(
                    CommandExecuteResult.Success(
                        methods = methodStart,
                        data = structures?.filterIsInstance<IBeacon>()?.map {
                            BleResponse(
                                uuid = it.uuid.toString(),
                                major = it.major.toString(),
                                minor = it.minor.toString()
                            )
                        }?.distinct()
                    ).toJsResponse()
                )
            }
        }
    }

    private fun startScan() {
        if (Build.VERSION.SDK_INT >= 31 && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "permission not grant")

            (context as WebActivity).apply {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN
                    ), 990
                )
            }
        } else {
            Log.d(TAG, "permission grant")
            bluetoothLeScanner?.startScan(null, scanSettings, leScanCallback)
        }
    }

    private fun stopScan() {
        if (Build.VERSION.SDK_INT >= 31 && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            (context as WebActivity).requestPermissions(blePermission, 10010)
            return
        } else {
            bluetoothLeScanner?.stopScan(leScanCallback)
        }

        runBlocking {
            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = methodStop,
                    data = JsonNull
                ).toJsResponse()
            )
        }
    }

    companion object {
        private const val TAG = "IBeaconInterface"
        private const val name = "IBeaconInterface"
        private const val methodStart = "$name.startScan"
        private const val methodStop = "$name.stopScan"
    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            methodStart to { startScan() },
            methodStop to { stopScan() }
        )
}

