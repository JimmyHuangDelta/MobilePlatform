package com.delta.mobileplatform.web.controller.device

import android.content.Context
import android.content.res.Configuration
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.ThemeMode
import com.delta.mobileplatform.model.response.UITheme
import com.delta.mobileplatform.model.response.toJsResponse
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebTheme @Inject constructor(
    @ActivityContext private val context: Context,
    private val commandQueue: Channel<String>
) : JsonRpcScript {

    private val themeScope = CoroutineScope(Job())
    private fun isDarkModeEnabled() {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val theme = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            ThemeMode.dark else ThemeMode.light
        themeScope.launch {
            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = getThemeMethod,
                    data = UITheme(theme)
                ).toJsResponse()
            )
        }
    }

    override val methods: Map<String, (JsCommand) -> Unit> = mapOf(
        getThemeMethod to { isDarkModeEnabled() }
    )

    companion object {
        const val name = "NativeInterface"
        const val getThemeMethod = "$name.getTheme"
    }
}