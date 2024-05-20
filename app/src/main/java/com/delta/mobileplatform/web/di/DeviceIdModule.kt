package com.delta.mobileplatform.web.di

import android.content.Context
import com.delta.mobileplatform.web.controller.device.DeviceId
import com.delta.mobileplatform.web.controller.device.WebTheme
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.channels.Channel

@Module
@InstallIn(ActivityComponent::class)
class DeviceModule {

    @Provides
    @ActivityScoped
    fun provideDeviceId(
        @ActivityContext context: Context,
        commandQueue: Channel<String>
    ): DeviceId {
        return DeviceId(context, commandQueue)
    }

    @Provides
    @ActivityScoped
    fun provideTheme(
        @ActivityContext context: Context,
        commandQueue: Channel<String>
    ): WebTheme {
        return WebTheme(context, commandQueue)
    }
}