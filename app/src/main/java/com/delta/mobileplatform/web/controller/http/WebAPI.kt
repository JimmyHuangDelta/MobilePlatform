package com.delta.mobileplatform.web.controller.http

import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.web.controller.utils.string.addSlashes
import com.delta.mobileplatform.model.receive.HttpParams
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.ApiResponse
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebAPI @Inject constructor(
    private val commandQueue: Channel<String>
) : JsonRpcScript {
    private val apiHandler = CoroutineExceptionHandler { _, error ->
        runBlocking {
            commandQueue.send(
                CommandExecuteResult.Fail(
                    methods = name,
                    message = error.message ?: error.localizedMessage
                ).toJsResponse()
            )
        }
    }
    private val apiScope = CoroutineScope(SupervisorJob() + apiHandler)
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
        }
    }

    private fun request(command: JsCommand) {
        val params = command.toHttpParams()
        apiScope.launch {

            val res = client.request {
                this.method = httpMethodFinder(params.httpMethod)
                this.url(params.url)
                this.headers {
                    params.header?.let {
                        it.toPairs().forEach { (key, value) ->
                            if (value == null) return@forEach
                            append(key, value)
                        }
                    }
                }
                params.body?.let {
                    when (params.datatype) {
                        "x-www-form-urlencoded" -> this.setBody(bodyFormBuilder(it))
                        "raw json" -> this.setBody(bodyRawJsonBuilder(it))
                    }
                }
            }
            val parseBody = try {
                res.body() as? JsonElement
            } catch (e: Exception) {
                null
            }
            val headersMap: Map<String, List<String>> = res.headers.toMap()

            val jsonHeaders = JsonObject(headersMap.mapValues { (_, values) ->
                Json.encodeToJsonElement(
                    addSlashes(values.joinToString(","))
                )
            }) as? JsonElement

            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = command.methods,
                    data = ApiResponse(
                        code = res.status.value,
                        body = parseBody,
                        header = jsonHeaders
                    )
                ).toJsResponse()
            )

        }

    }

    private fun bodyFormBuilder(body: JsonElement): FormDataContent {
        return FormDataContent(Parameters.build {
            body.toPairs().forEach { (key, value) ->
                if (value == null) return@forEach
                append(key, value)
            }
        })
    }

    private fun bodyRawJsonBuilder(body: JsonElement): TextContent {
        return TextContent(Json.encodeToString(body), ContentType.Application.Json)
    }

    private fun httpMethodFinder(httpMethod: String): HttpMethod {
        return when (httpMethod) {
            "POST" -> HttpMethod.Post
            "GET" -> HttpMethod.Get
            else -> throw Exception()
        }
    }

    private fun JsonElement.toPairs() =
        this.jsonObject.mapNotNull { it.key to Json.decodeFromJsonElement<String?>(it.value) }
            .filterNot { (key, value) -> key.isBlank() || value.isNullOrBlank() }


    companion object {
        const val name = "WebApiInterface"
    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            name to { request(it) }
        )

    private fun JsCommand.toHttpParams(): HttpParams {
        return params?.let {
            Json.decodeFromJsonElement<HttpParams>(it)
        } ?: throw CommandExecuteResult.Fail(
            methods = methods,
            message = "params is invalid"
        )
    }

}