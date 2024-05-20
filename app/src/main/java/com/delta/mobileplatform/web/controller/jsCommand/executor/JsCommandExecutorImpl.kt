package com.delta.mobileplatform.web.controller.jsCommand.executor

import com.delta.mobileplatform.web.controller.barcode.BarcodeController
import com.delta.mobileplatform.web.controller.ble.BleController
import com.delta.mobileplatform.web.controller.bluetooth.BluetoothController
import com.delta.mobileplatform.web.controller.device.DeviceId
import com.delta.mobileplatform.web.controller.device.WebTheme
import com.delta.mobileplatform.web.controller.http.WebAPI
import com.delta.mobileplatform.web.controller.log.JsLog
import com.delta.mobileplatform.web.controller.media.MediaController
import com.delta.mobileplatform.web.controller.messageQueue.MQTT
import com.delta.mobileplatform.web.controller.networkState.NetworkConnectivityImpl
import com.delta.mobileplatform.web.controller.nfc.NfcController
import com.delta.mobileplatform.web.controller.version.Version
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.web.controller.localStorage.LocalStorageController
import javax.inject.Inject

class JsCommandExecutorImpl @Inject constructor(
    private val localStorageController: LocalStorageController,
    private val nfcController: NfcController,
    private val bluetoothController: BluetoothController,
    private val mediaController: MediaController,
    private val webAPI: WebAPI,
    private val deviceId: DeviceId,
    private val webTheme: WebTheme,
    private val bleController: BleController,
    private val mqtt: MQTT,
    private val barcodeController: BarcodeController,
    private val jsLog: JsLog,
    private val network: NetworkConnectivityImpl
) : JsCommandExecutor() {
    override val runnableMethods: Map<String, (JsCommand) -> Unit> by lazy {
        nfcController.methods +
                bluetoothController.methods +
                mediaController.methods +
                barcodeController.methods +
                mqtt.methods +
                Version().methods +
                localStorageController.methods +
                deviceId.methods +
                webTheme.methods +
                bleController.methods +
                jsLog.methods +
                network.methods
    }
    override val similarMethods: Map<String, (JsCommand) -> Unit> by lazy {
        webAPI.methods
    }

}