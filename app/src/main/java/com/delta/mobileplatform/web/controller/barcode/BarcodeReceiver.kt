package com.delta.mobileplatform.web.controller.barcode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.delta.mobileplatform.model.response.BarcodeResponse
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class BarcodeReceiver @Inject constructor(
    private val commandQueue: Channel<String>
) : BroadcastReceiver() {

    companion object {
        const val ACTION_RECEIVE_DATA = "unitech.scanservice.data"
        const val TAG = "BarcodeReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ACTION_RECEIVE_DATA == intent?.action) {
            val bundle = intent.extras
            bundle?.let {
                val scanData = bundle.getString("text")
                scanData?.let {
                    Log.d(TAG, scanData)

                    val result = CommandExecuteResult.Success(
                        methods = BarcodeController.methodScan,
                        data = BarcodeResponse(scanData.replace("\n", ""))
                    ).toJsResponse()

                    runBlocking {
                        commandQueue.send(result)
                    }
                }
            }
        }
    }
}