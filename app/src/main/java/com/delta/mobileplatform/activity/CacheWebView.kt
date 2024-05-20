package com.delta.mobileplatform.activity

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.delta.mobileplatform.web.controller.utils.link_list.LinkedList


@SuppressLint("SetJavaScriptEnabled")
class CacheWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    private var isLoading = true
    private val commandListBuffer = LinkedList<String>()

    init {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowContentAccess = true
            mediaPlaybackRequiresUserGesture = false
        }
        webViewClient = CacheWebViewClient()
        webChromeClient = CacheWebChromeClient()

        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        requestFocusFromTouch()
    }

    fun executeJs(params: String) {
        if (isLoading) {
            commandListBuffer.append(params)
        } else {
            this.evaluateJavascript(
                "receiveData(`${params}`)", null
            )
        }

    }

    class CacheWebChromeClient : WebChromeClient() {
        override fun onConsoleMessage(message: ConsoleMessage): Boolean {
            Log.d(
                "CacheWeb", "${message.message()} -- From line " +
                        "${message.lineNumber()} of ${message.sourceId()}"
            )
            return true
        }

        override fun onPermissionRequest(request: PermissionRequest) {
            request.grant(request.resources)
        }
    }

    inner class CacheWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            isLoading = false
            for (command in commandListBuffer) {
                command?.let { executeJs(it) }
                commandListBuffer.setNextToFirst()
            }
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?,
        ) {
            super.onReceivedError(view, request, error)
            Log.d(
                "CacheWeb", "onReceivedError"
            )
            Log.d(
                "CacheWeb", error?.description.toString()
            )
            request?.requestHeaders?.forEach {
                Log.d(
                    "CacheWeb", "${it.key} : ${it.value}"
                )
            }
            Log.d(
                "CacheWeb", request?.isForMainFrame.toString()
            )
            Log.d(
                "CacheWeb", error?.errorCode.toString()
            )
        }
    }

}