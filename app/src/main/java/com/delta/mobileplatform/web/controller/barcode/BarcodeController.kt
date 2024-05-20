package com.delta.mobileplatform.web.controller.barcode

import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

class BarcodeController @Inject constructor(
    private val commandQueue: Channel<String>,
    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
) : JsonRpcScript {
    companion object {
        const val name = "BarcodeInterface"
        const val methodScan = "$name.startScan"
    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            methodScan to { startScan() }
        )

    // For android devices, please using hardware barcode scanner provide by unitech
    private fun startScan() {
        val result = CommandExecuteResult.Fail(
            methods = methodScan,
            message = " For android devices, please using hardware barcode scanner provide by unitech"
        ).toJsResponse()

        scope.launch { commandQueue.send(result) }
    }
}