package com.delta.mobileplatform.web.controller.localStorage

import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.web.controller.localStorage.DbRepository
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject


class LocalStorageController @Inject constructor(
    private val commandQueue: Channel<String>,
    private val dbRepository: DbRepository,
    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
) : JsonRpcScript {


    private suspend fun set(domain: String, params: LocalStorageJsInput) {

        val (key, value) = params

        value?.let { dbRepository.set(domain, key, it) }
        commandQueue.send(
            CommandExecuteResult.Success(
                methods = methodSet,
                data = dbRepository.get(domain, key)?.toLocalStateResponse()
            ).toJsResponse()
        )

    }

    private suspend fun getAll(domain: String) {

        val res = Json.encodeToJsonElement(
            dbRepository.getAll(domain).map { it.toLocalStateResponse() }
        )

        commandQueue.send(
            CommandExecuteResult.Success(
                methods = methodGetAll,
                data = res
            ).toJsResponse()
        )
    }

    private suspend fun get(domain: String, params: LocalStorageJsInput) {

        commandQueue.send(
            CommandExecuteResult.Success(
                methods = methodGet,
                data = dbRepository.get(domain, params.key)?.toLocalStateResponse()
            ).toJsResponse()
        )
    }

    private suspend fun remove(domain: String, params: LocalStorageJsInput) {

        val expectRemoveData = dbRepository.get(domain, params.key)
        dbRepository.remove(domain, params.key)

        commandQueue.send(
            CommandExecuteResult.Success(
                methods = methodRemove,
                data = expectRemoveData?.toLocalStateResponse()
            ).toJsResponse()
        )
    }

    private suspend fun clear(domain: String) {
        dbRepository.clear(domain)
        commandQueue.send(
            CommandExecuteResult.Success(methods = methodClear, data = JsonNull)
                .toJsResponse()
        )

    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            methodSet to { command ->
                command.toLocalStorageJsInput().also {
                    it.apply {
                        requireKeyNotNullOrBlank(methods = methodSet, key)
                        requireValueNotNullOrBlank(methods = methodSet, value)
                    }
                    scope.launch {
                        set(command.domain, it)
                    }
                }
            },
            methodGet to { command ->
                command.toLocalStorageJsInput().also {
                    it.apply {
                        requireKeyNotNullOrBlank(methods = methodGet, key)
                    }
                    scope.launch {
                        get(command.domain, it)
                    }
                }
            },
            methodRemove to { command ->
                command.toLocalStorageJsInput().also {
                    it.apply {
                        requireKeyNotNullOrBlank(methods = methodRemove, key)
                    }
                    scope.launch {
                        remove(command.domain, it)
                    }
                }
            },
            methodGetAll to { command ->
                scope.launch {
                    getAll(command.domain)
                }
            },
            methodClear to { command ->
                scope.launch {
                    clear(command.domain)
                }
            },
        )

    companion object {
        const val name = "LocalStorageInterface"
        const val methodGet = "$name.get"
        const val methodSet = "$name.set"
        const val methodGetAll = "$name.getAll"
        const val methodClear = "$name.clear"
        const val methodRemove = "$name.remove"
    }
}

fun JsCommand.toLocalStorageJsInput(): LocalStorageJsInput {
    val (_, methods, params) = this

    return params?.let {
        try {
            Json.decodeFromJsonElement<LocalStorageJsInput>(it)
        } catch (e: Exception) {
            throw CommandExecuteResult.Fail(
                methods = methods,
                message = "{key} is invalid",
                data = it
            )
        }
    } ?: throw CommandExecuteResult.Fail(
        methods = methods,
        message = "params is invalid"
    )

}

@Serializable
data class LocalStorageJsInput(
    val key: String,
    val value: String? = null
) {
    fun requireKeyNotNullOrBlank(methods: String, key: String) {
        if (key.isBlank()) throw CommandExecuteResult.Fail(
            methods = methods,
            data = Json.encodeToJsonElement(this),
            message = "{key} value is invalid"
        )
    }

    fun requireValueNotNullOrBlank(methods: String, value: String?) {
        if (value.isNullOrBlank()) throw CommandExecuteResult.Fail(
            methods = methods,
            data = Json.encodeToJsonElement(this),
            message = "{value} value is invalid"
        )
    }

}