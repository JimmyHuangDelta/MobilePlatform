package com.delta.mobileplatform.web.controller.nfc

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.delta.mobileplatform.activity.WebActivity
import com.delta.mobileplatform.web.controller.jsCommand.JsonRpcScript
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.NFCResponse
import com.delta.mobileplatform.model.response.toJsResponse
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull
import javax.inject.Inject

class NfcController @Inject constructor(
    @ActivityContext val context: Context,
    private val commandQueue: Channel<String>
) : JsonRpcScript, NfcAdapter.ReaderCallback {


    private val activity by lazy {
        context as WebActivity
    }

    private val nfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(context)
    }

    override val methods = mapOf<String, (JsCommand) -> Unit>(
        methodStart to { startScan() },
        methodStop to { stopScan() }
    )


    private fun startScan() {
        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if (nfcAdapter == null) {
                nfcNotSupport()
            } else {
                if (nfcAdapter?.isEnabled == false) {
                    nfcNotEnable()
                } else {
                    nfcAdapter?.enableReaderMode(
                        activity,
                        this,
                        NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B,
                        null
                    )
                }
            }
        }
    }

    private fun nfcNotSupport() {
        activity.lifecycleScope.launch {
            commandQueue.send(
                CommandExecuteResult.Fail(
                    methods = methodStart,
                    message = "NfC is not supported in this device"
                ).toJsResponse()
            )
        }
    }

    private fun nfcNotEnable() {
        activity.lifecycleScope.launch {
            commandQueue.send(
                CommandExecuteResult.Fail(
                    methods = methodStart,
                    message = "NfC is not enable"
                ).toJsResponse()
            )
        }
    }

    private fun stopScan() {
        nfcAdapter?.disableReaderMode(activity)
        activity.lifecycleScope.launch {
            commandQueue.send(
                CommandExecuteResult.Success(
                    methods = methodStop,
                    data = JsonNull,
                ).toJsResponse()
            )
        }
    }

    override fun onTagDiscovered(p0: Tag?) {
        // Get the tag's ID.
        p0?.let {
            Log.d("NFC", "Tag ID: ${nfcTagReadSerialNumber(tag = it)}")
            activity.lifecycleScope.launch {
                commandQueue.send(
                    CommandExecuteResult.Success(
                        methods = methodStart,
                        data = NFCResponse(id = nfcTagReadSerialNumber(tag = it)),
                    ).toJsResponse()
                )
            }
        }
    }

    private fun nfcTagReadSerialNumber(tag: Tag): String {
        val tagId = MifareClassic.get(tag).tag.id
        val hexdump = buildString {
            for (i in tagId.indices) {
                var x = Integer.toHexString(tagId[i].toInt() and 0xff)
                if (x.length == 1) {
                    x = "0$x"
                }
                this.append(x)
            }
        }
        return hexdump
    }

    companion object {
        const val name = "NfcInterface"
        const val methodStart = "$name.startScan"
        const val methodStop = "$name.stopScan"
    }
}