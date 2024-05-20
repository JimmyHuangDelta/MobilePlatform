package com.delta.mobileplatform.web.controller.messageQueue

import android.content.Context
import android.content.Intent
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import javax.inject.Inject

class MQTT @Inject constructor(
    @ActivityContext private val context: Context,
    private val commandQueue: Channel<String>,
) : JsonRpcScript {

    companion object {
        const val name = "MqttInterface"
        const val methodConnect = "$name.connect"
        const val methodDisconnect = "$name.disconnect"
        const val methodSubscribe = "$name.subscribe"
        const val methodUnsubscribe = "$name.unsubscribe"
        const val methodSendMessage = "$name.sendMessage"
    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            methodConnect to {
                Intent(context, MqttService::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(methodConnect, Json.encodeToString(it))
                }.also {
                    context.startForegroundService(it)
                    runBlocking {
                        commandQueue.send(
                            CommandExecuteResult.Success(
                                methods = methodConnect,
                                data = JsonNull
                            ).toJsResponse()
                        )
                    }
                }
            },
            methodDisconnect to {
                Intent(context, MqttService::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(methodDisconnect, Json.encodeToString(it))
                }.also {
                    context.startForegroundService(it)
                    runBlocking {
                        commandQueue.send(
                            CommandExecuteResult.Success(
                                methods = methodDisconnect,
                                data = JsonNull
                            ).toJsResponse()
                        )
                    }
                }
            },
            methodSubscribe to {
                Intent(context, MqttService::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(methodSubscribe, Json.encodeToString(it))
                }.also {
                    context.startForegroundService(it)
                    runBlocking {
                        commandQueue.send(
                            CommandExecuteResult.Success(
                                methods = methodSubscribe,
                                data = JsonNull
                            ).toJsResponse()
                        )
                    }

                }
            },
            methodUnsubscribe to {
                Intent(context, MqttService::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(methodUnsubscribe, Json.encodeToString(it))
                }.also {
                    context.startForegroundService(it)
                    runBlocking {
                        commandQueue.send(
                            CommandExecuteResult.Success(
                                methods = methodUnsubscribe,
                                data = JsonNull
                            ).toJsResponse()
                        )
                    }

                }
            },
        )

}
