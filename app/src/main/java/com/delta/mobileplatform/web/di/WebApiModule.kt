package com.delta.mobileplatform.web.di

import com.delta.mobileplatform.web.controller.http.WebAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebApiModule {
    @Provides
    @Singleton
    fun provideWebApi(commandQueue: Channel<String>): WebAPI {
        return WebAPI(commandQueue)
    }
}