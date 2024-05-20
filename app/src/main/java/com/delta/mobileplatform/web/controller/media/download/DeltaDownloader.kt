package com.delta.mobileplatform.web.controller.media.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class DeltaDownloader(
    private val context: Context,
) : DownLoader {

    private val downloaderManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String, fileName: String, token: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("application/octet-stream")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .addRequestHeader("Authorization", token)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
        return downloaderManager.enqueue(request)
    }
}