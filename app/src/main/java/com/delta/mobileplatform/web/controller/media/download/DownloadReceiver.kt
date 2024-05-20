package com.delta.mobileplatform.web.controller.media.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DownloadReceiver : BroadcastReceiver() {

    private lateinit var downloader: DownloadManager

    companion object {
        const val TAG = "DownloadReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != -1L) {
                Log.d(TAG, "download finished !! with id :$id")
                handleDownloadedFile(id, context)
            } else {
                Log.d(TAG,"download id not found")
            }
        }
    }

    private fun handleDownloadedFile(id: Long, context: Context?) {

    }

}