package com.delta.mobileplatform.web.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsCommandQueueModule {

    @Provides
    @Singleton
    fun provideCommandQueue(): Channel<String> {
        return Channel()
    }
}