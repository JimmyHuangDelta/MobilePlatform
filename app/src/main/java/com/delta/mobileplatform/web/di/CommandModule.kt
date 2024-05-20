package com.delta.mobileplatform.web.di

import com.delta.mobileplatform.web.controller.barcode.BarcodeController
import com.delta.mobileplatform.web.controller.ble.BleController
import com.delta.mobileplatform.web.controller.bluetooth.BluetoothController
import com.delta.mobileplatform.web.controller.device.DeviceId
import com.delta.mobileplatform.web.controller.device.WebTheme
import com.delta.mobileplatform.web.controller.http.WebAPI
import com.delta.mobileplatform.web.controller.jsCommand.executor.JsCommandExecutor
import com.delta.mobileplatform.web.controller.jsCommand.executor.JsCommandExecutorImpl
import com.delta.mobileplatform.web.controller.jsCommand.parser.CommandParserImpl
import com.delta.mobileplatform.web.controller.jsCommand.parser.JsCommandParser
import com.delta.mobileplatform.web.controller.log.JsLog
import com.delta.mobileplatform.web.controller.media.MediaController
import com.delta.mobileplatform.web.controller.messageQueue.MQTT
import com.delta.mobileplatform.web.controller.networkState.NetworkConnectivityImpl
import com.delta.mobileplatform.web.controller.nfc.NfcController
import com.delta.mobileplatform.web.controller.localStorage.LocalStorageController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class CommandModule {
    @Provides
    @ActivityScoped
    fun provideCommandParser(): JsCommandParser {
        return CommandParserImpl()
    }

    @Provides
    @ActivityScoped
    fun provideCommandExecute(
        localStorageController: LocalStorageController,
        nfcController: NfcController,
        bluetoothController: BluetoothController,
        mediaController: MediaController,
        webAPI: WebAPI,
        deviceId: DeviceId,
        webTheme: WebTheme,
        bleController: BleController,
        mqtt: MQTT,
        barcodeController: BarcodeController,
        jsLog: JsLog,
        network: NetworkConnectivityImpl
    ): JsCommandExecutor {
        return JsCommandExecutorImpl(
            localStorageController = localStorageController,
            nfcController = nfcController,
            bluetoothController = bluetoothController,
            mediaController = mediaController,
            webAPI = webAPI,
            deviceId = deviceId,
            webTheme = webTheme,
            bleController = bleController,
            mqtt = mqtt,
            barcodeController = barcodeController,
            jsLog = jsLog,
            network = network
        )
    }
}