package com.delta.mobileplatform.web.di

import android.content.Context
import com.delta.mobileplatform.web.controller.barcode.BarcodeController
import com.delta.mobileplatform.web.controller.bluetooth.BluetoothController
import com.delta.mobileplatform.web.controller.localStorage.DbRepository
import com.delta.mobileplatform.web.controller.localStorage.LocalStorageController
import com.delta.mobileplatform.web.controller.media.MediaController
import com.delta.mobileplatform.web.controller.nfc.NfcController

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.channels.Channel

@Module
@InstallIn(ActivityComponent::class)
class UiControllerModule {

    @Provides
    fun provideLocalStorageController(
        commandQueue: Channel<String>,
        dbRepository: DbRepository,
    ): LocalStorageController {
        return LocalStorageController(commandQueue, dbRepository)
    }

    @Provides
    fun provideBluetoothController(
        @ActivityContext context: Context,
        commandQueue: Channel<String>,
    ): BluetoothController {
        return BluetoothController(context, commandQueue)
    }

    @Provides
    fun provideMediaController(
        @ActivityContext context: Context,
        commandQueue: Channel<String>,
    ): MediaController {
        return MediaController(context, commandQueue)
    }

    @Provides
    fun provideBarcodeController(
        commandQueue: Channel<String>,
    ): BarcodeController {
        return BarcodeController(commandQueue)
    }

    @Provides
    fun provideNfcController(
        @ActivityContext context: Context,
        commandQueue: Channel<String>
    ): NfcController {
        return NfcController(context = context, commandQueue = commandQueue)
    }
}