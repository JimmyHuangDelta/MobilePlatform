package com.delta.mobileplatform.web.controller.jsCommand

import com.delta.mobileplatform.model.receive.JsCommand

interface JsonRpcScript {
    val methods: Map<String, (JsCommand) -> Unit>
}