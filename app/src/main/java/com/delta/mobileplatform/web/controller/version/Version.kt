package com.delta.mobileplatform.web.controller.version

import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand

class Version : JsonRpcScript {

    private fun getLatest() {
    }

    private fun download() {
    }

    companion object {
        const val name = "Version"
    }

    override val methods: Map<String, (JsCommand) -> Unit>
        get() = mapOf(
            "$name.getLatest" to { getLatest() },
            "$name.download" to { download() }
        )
}