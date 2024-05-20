package com.delta.mobileplatform.activity

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.delta.mobileplatform.R
import com.delta.mobileplatform.databinding.ActivityWebBinding
import com.delta.mobileplatform.web.controller.barcode.BarcodeReceiver
import com.delta.mobileplatform.web.controller.jsCommand.executor.JsCommandExecutor
import com.delta.mobileplatform.web.controller.jsCommand.parser.JsCommandParser
import com.delta.mobileplatform.web.controller.log.JsLog
import com.delta.mobileplatform.web.controller.log.Logger
import com.delta.mobileplatform.web.controller.messageQueue.MQTT
import com.delta.mobileplatform.web.controller.messageQueue.Notify
import com.delta.mobileplatform.web.controller.networkState.NetworkConnectivityImpl
import com.delta.mobileplatform.web.controller.utils.string.addSlashes
import com.delta.mobileplatform.model.response.CommandExecuteResult
import com.delta.mobileplatform.model.response.toJsResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

@AndroidEntryPoint
class WebActivity : AppCompatActivity() {

    @Inject
    lateinit var networkConnectivity: NetworkConnectivityImpl

    @Inject
    lateinit var jsCommandParser: JsCommandParser

    @Inject
    lateinit var jsCommandExecutor: JsCommandExecutor

    @Inject
    lateinit var commandQueue: Channel<String>

    @Inject
    lateinit var barcodeReceiver: BarcodeReceiver

    @Inject
    lateinit var logger: Logger

    lateinit var url:String
    lateinit var title:String

    private lateinit var binding: ActivityWebBinding
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        onMessageDataFromIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        if (intent != null) {
            url = intent.getStringExtra("url").toString()
            title = intent.getStringExtra("title").toString()
            // 使用参数值进行后续操作
        }
        networkConnectivity.startDetect()

        binding.editTitleBar.setTitle(title)
        binding.editTitleBar.leftIcon1.visibility = View.VISIBLE;
        binding.editTitleBar.setLeftIcon1(R.drawable.ic_back, View.OnClickListener {
            // 點擊圖標後的操作
           finish()// 這裡假設你想要返回上一頁
        })




        binding.web.apply {
            addJavascriptInterface(JsCommandBridge(), "Android")
        }
        lifecycleScope.launch {
            commandQueue.receiveAsFlow()
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .onStart { logger.removeRecordOlderThanFiveDays() }
                .collect {
                    Json.decodeFromString<JsonElement>(it).let { result ->
                        result.jsonObject["methods"]?.let { method ->
                            if (method.toString() != "\"${JsLog.methodGetLog}\"") {
                                logger.osLog(Json.encodeToJsonElement(it))
                            }
                        }
                    }
                    binding.web.executeJs(it)
                }
        }

        ContextCompat.registerReceiver(
            this,
            barcodeReceiver,
            IntentFilter(BarcodeReceiver.ACTION_RECEIVE_DATA),
            ContextCompat.RECEIVER_EXPORTED
        )

//        requestPermission(allPermission)
        disablePDAScanToKey()
        onMessageDataFromIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        if (binding.web.url == null) {
            synchronized(binding.web) {
                updateCachePolicyByNetwork()
                binding.web.loadUrl(url)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(barcodeReceiver)
        networkConnectivity.closeDetect()
    }

    private fun updateCachePolicyByNetwork() {
        if (networkConnectivity.isNetworkAvailable()) {
            binding.web.clearCache(true)
            binding.web.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        } else {
            binding.web.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
    }

    private fun onMessageDataFromIntent(intent: Intent?) {
        val messageRawData = intent?.getStringExtra(Notify.MESSAGE_DATA)
        messageRawData?.let {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    commandQueue.send(
                        CommandExecuteResult.Success(
                            methods = MQTT.methodSendMessage,
                            data = addSlashes(it)
                        ).toJsResponse().apply { println(this) }
                    )
                }
            }
        }
    }

    private fun runCommand(command: String) {
        try {
            jsCommandParser.parse(command.trimIndent()).let {
                Log.d("JSCommand", it.toString())
                jsCommandExecutor.execute(it)
            }
        } catch (e: Exception) {
            when (e) {
                is CommandExecuteResult.Fail -> {
                    lifecycleScope.launch {
                        Log.e("JSCommand", e.toJsResponse())
                        binding.web.executeJs(e.toJsResponse())
                    }
                }

                else -> {
                    Log.e("JSCommand", e.message ?: "")
                }
            }

        }
    }

    private fun permissionGrant(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permissions: Array<String>) {
        permissions.filterNot(::permissionGrant).toTypedArray().let {
            if (it.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, it, 1)
            }
        }
    }

    private fun disablePDAScanToKey() {
        val intent = Intent().apply {
            action = "unitech.scanservice.scan2key_setting"
            putExtra("scan2key", false)
        }
        sendBroadcast(intent)
    }

    inner class JsCommandBridge {

        @JavascriptInterface
        fun message(command: String) {
            Log.d("JSCommand", command)
//            logger.osLog(Json.encodeToJsonElement(command))
            runCommand(command)
        }
    }


}
