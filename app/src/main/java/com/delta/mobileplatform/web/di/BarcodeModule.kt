package com.delta.mobileplatform.web.di

import com.delta.mobileplatform.web.controller.barcode.BarcodeReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.channels.Channel

@Module
@InstallIn(ActivityComponent::class)
class BarcodeModule {
    @Provides
    @ActivityScoped
    fun provideBarcodeReceiver(
        commandQueue: Channel<String>
    ): BarcodeReceiver {
        return BarcodeReceiver(commandQueue)
    }
}