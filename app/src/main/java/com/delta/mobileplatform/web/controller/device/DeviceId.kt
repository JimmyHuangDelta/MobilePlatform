package com.delta.mobileplatform.web.controller.device

import android.content.Context
import android.content.SharedPreferences
import com.delta.mobileplatform.activity.WebActivity
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.DeviceIdResponse
import com.delta.mobileplatform.model.response.toJsResponse
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class DeviceId @Inject constructor(
    @ActivityContext context: Context,
    private val commandQueue: Channel<String>
) : JsonRpcScript {
    private val GUID = "GUIDPrefsFile" // Name of the SharedPreferences file
    private val DEVICE_ID = "device_id" // Key for storing the unique ID
    private val scope = CoroutineScope(SupervisorJob())
    private val activity by lazy {
        context as WebActivity
    }

    private fun getDeviceId() {
        val sharedPreferences: SharedPreferences =
            activity.getSharedPreferences(GUID, Context.MODE_PRIVATE)

        // Check if the unique ID exists in SharedPreferences
        if (!sharedPreferences.contains(DEVICE_ID)) {
            // If it doesn't exist, generate a new unique ID and store it
            val uniqueID = UUID.randomUUID().toString()
            val editor = sharedPreferences.edit()
            editor.putString(DEVICE_ID, uniqueID)
            editor.apply()
        }

        // Retrieve the unique ID from SharedPreferences
        scope.launch {
            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = getDeviceMethod,
                    data = DeviceIdResponse(id = sharedPreferences.getString(DEVICE_ID, "") ?: "")
                ).toJsResponse()
            )
        }
    }

    override val methods: Map<String, (JsCommand) -> Unit> = mapOf(
        getDeviceMethod to { getDeviceId() },
    )

    companion object {
        const val name = "NativeInterface"
        const val getDeviceMethod = "$name.getDeviceId"
    }
}