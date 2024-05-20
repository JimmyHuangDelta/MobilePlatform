package com.delta.mobileplatform.web.controller.media.download

interface DownLoader {

    fun downloadFile(url: String, fileName:String, token:String): Long
}