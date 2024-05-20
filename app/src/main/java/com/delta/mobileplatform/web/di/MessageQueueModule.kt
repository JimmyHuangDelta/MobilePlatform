package com.delta.mobileplatform.web.di

import android.content.Context
import com.delta.mobileplatform.web.controller.messageQueue.MQTT
import com.delta.mobileplatform.web.controller.messageQueue.Notify
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel

@Module
@InstallIn(ActivityComponent::class)
class MessageQueueModule {

    @Provides
    fun provideMqtt(
        @ActivityContext context: Context,
        commandQueue: Channel<String>,
    ): MQTT {
        return MQTT(
            context = context,
            commandQueue = commandQueue,
        )
    }

}

@Module
@InstallIn(ServiceComponent::class)
class MqttServiceModule {

    @Provides
    fun provideNotify(
        @ApplicationContext context: Context
    ): Notify {
        return Notify(context)
    }
}
