package com.delta.mobileplatform.web.di

import android.content.Context
import com.delta.mobileplatform.web.controller.networkState.Connectivity
import com.delta.mobileplatform.web.controller.networkState.NetworkConnectivityImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {

    @Provides
    @Singleton
    fun provideNetworkConnectivityImpl(
        @ApplicationContext context: Context,
        commandQueue: Channel<String>
    ): Connectivity {
        return NetworkConnectivityImpl(context = context, commandQueue = commandQueue)
    }

}