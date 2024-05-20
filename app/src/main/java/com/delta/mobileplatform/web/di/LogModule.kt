package com.delta.mobileplatform.web.di

import android.content.Context
import com.delta.mobileplatform.web.controller.log.JsLog
import com.delta.mobileplatform.web.controller.log.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LogModule {
    @Provides
    @Singleton
    fun provideLogger(@ApplicationContext context: Context): Logger {
        return Logger(context)
    }

    @Provides
    @Singleton
    fun provideJsLog(
        logger: Logger,
        commandQueue: Channel<String>
    ): JsLog {
        return JsLog(fileLogger = logger, commandQueue = commandQueue)
    }
}