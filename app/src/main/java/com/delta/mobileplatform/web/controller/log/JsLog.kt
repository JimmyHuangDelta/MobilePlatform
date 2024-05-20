package com.delta.mobileplatform.web.controller.log

import android.os.Build
import androidx.annotation.RequiresApi
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.web.controller.utils.string.addSlashes
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.LogFormat
import com.delta.mobileplatform.model.response.toJsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject


class JsLog @Inject constructor(
    private val fileLogger: Logger,
    private val commandQueue: Channel<String>
) : JsonRpcScript {
    private val scope = CoroutineScope(Job())
    override val methods: Map<String, (JsCommand) -> Unit>
        @RequiresApi(Build.VERSION_CODES.O)
        get() = mapOf(
            methodLogger to { it.also(::logger) },
            methodGetLog to { getLog() }
        )

    private fun logger(command: JsCommand) {
        command.params?.let {
            val record = Json.decodeFromJsonElement<LogFormat>(it)
//            fileLogger.appendLog(record)
        } ?: {
            scope.launch {
                commandQueue.send(
                    CommandExecuteResult.Fail(
                        methods = methodLogger,
                        message = "missing parameter"
                    ).toJsResponse()
                )
            }
        }

        scope.launch {
            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = methodLogger,
                    data = JsonNull
                ).toJsResponse()
            )
        }
    }

    private fun getLog() {
        scope.launch {
            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = methodGetLog,
                    data = Json.decodeFromString<List<LogFormat>>(fileLogger.getLogRecord()).map {
                        it.copy(
                            body = Json.encodeToJsonElement(
                                addSlashes(
                                    Json.encodeToString(it.body)
                                )
                            )
                        )
                    }
                ).toJsResponse()
            )
        }
    }

    companion object {
        const val name = "LogInterface"
        const val methodLogger = "${name}.logger"
        const val methodGetLog = "${name}.getLog"
    }

}