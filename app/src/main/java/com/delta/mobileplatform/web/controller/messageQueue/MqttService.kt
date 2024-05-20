package com.delta.mobileplatform.web.controller.messageQueue

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import com.delta.mobileplatform.R
import com.delta.mobileplatform.web.controller.log.Logger
import com.delta.mobileplatform.model.receive.JsCommand
import com.delta.mobileplatform.model.receive.MqttConnectConfig
import com.delta.mobileplatform.model.receive.MqttMessageData
import com.delta.mobileplatform.model.receive.MqttTopic
import com.delta.mobileplatform.model.response.CommandExecuteResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import javax.inject.Inject

@AndroidEntryPoint
class MqttService : Service() {
    @Inject
    lateinit var notify: Notify

    @Inject
    lateinit var logger: Logger

    private val topics: Set<String> = setOf()
    override fun onBind(intent: Intent?): IBinder {
        return MqttBinder()
    }

    inner class MqttBinder : Binder() {
        fun getService(): MqttService = this@MqttService
    }

    private val handleAppCrash =
        Thread.UncaughtExceptionHandler { thread, ex ->
            try {
                logger.osLog(Json.encodeToJsonElement(ex.toString()))
            } finally {
                stopSelf()
            }
        }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash)
        val notification = foregroundNotification(
            title = "message queue connection",
            content = "running message queue in background"
        )
        createForegroundNotification(notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("mqttservice", "onStartCommand")
        if (intent != null && intent.action === "stop") {
            stopSelf()
        }
        intent?.getStringExtra(MQTT.methodConnect)?.let {
            startConnection(
                Json.decodeFromString<JsCommand>(it).toMqttConnectConfig()
            )
            Log.d("mqttservice", "methodConnect")
        }
        intent?.getStringExtra(MQTT.methodDisconnect)?.let {
            stopSelf()
            Log.d("mqttservice", "methodDisconnect")
        }
        intent?.getStringExtra(MQTT.methodSubscribe)?.let {
            subscribe(
                Json.decodeFromString<JsCommand>(it).toMqttTopic(MQTT.methodSubscribe)
            )
            Log.d("mqttservice", "methodSubscribe")
        }
        intent?.getStringExtra(MQTT.methodUnsubscribe)?.let {
            Log.d("mqttservice", "methodUnsubscribe")
            unsubscribe(
                Json.decodeFromString<JsCommand>(it).toMqttTopic(MQTT.methodUnsubscribe)
            )
        }
        return START_STICKY
    }


    override fun onDestroy() {
        Log.d("mqttservice", "onDestroy")

        notify.showNotify(
            title = "close service",
            content = "close mqtt service for ${mqttClient?.clientId}",
            group = "topic.toString()",
            data = null
        )
        closeConnection()
        super.onDestroy()
    }


    private var mqttClient: MqttClient? = null
    private fun createForegroundNotification(notification: Notification) {
//        if (mqttClient != null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            startForeground(
                FOREGROUND_NOTIFICATION_ID, // Notification ID, cannot be 0
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
        } else {
            this.startForeground(
                FOREGROUND_NOTIFICATION_ID, // Notification ID, cannot be 0
                notification
            )
        }
    }

    private fun foregroundNotificationChannelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Foreground",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            channel.id
        } else "Foreground"
    }

    private fun foregroundNotification(title: String, content: String): Notification {
        val intentStop = Intent(this, this::class.java)
        intentStop.action = "stop"
        return NotificationCompat.Builder(this, foregroundNotificationChannelId())
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_launcher_background, "stop mqtt",
                PendingIntent.getService(
                    this, 0, intentStop,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setSilent(true)
            .setVibrate(longArrayOf(0))
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .build()

    }

    private fun startConnection(
        config: MqttConnectConfig
    ) {
        synchronized(this) {
            if (mqttClient?.clientId == config.username) {
                return
            } else {
                mqttClient?.disconnect()
            }
        }

        val persistence = MemoryPersistence()
        mqttClient = MqttClient(config.ip, config.username, persistence)
        Log.d("mqtt", mqttClient?.clientId.toString())

        val connOpts = MqttConnectOptions()
        connOpts.userName = config.username
        connOpts.password = config.password.toCharArray()
        connOpts.apply {
            isCleanSession = false
            isAutomaticReconnect = true
        }
        mqttClient?.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                println("mqtt connected to $serverURI")
                val notification = foregroundNotification(
                    title = "connecting to ${config.username}",
                    content = "running message queue in background"
                )
                createForegroundNotification(notification)
            }

            override fun connectionLost(cause: Throwable?) {
                println("mqtt disconnect")
                println(cause?.message)
                println(cause?.localizedMessage)
                println(cause?.cause)
                cause?.printStackTrace()
                val notification = foregroundNotification(
                    title = "message queue connectionLost",
                    content = cause?.message ?: cause?.localizedMessage ?: ""
                )
                createForegroundNotification(notification)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                println(message.toString())
                notify.showNotify(
                    title = topic.toString(),
                    content = Html.fromHtml(
                        Json.decodeFromString<MqttMessageData>(message.toString()).msgBody.content,
                        Html.FROM_HTML_MODE_LEGACY
                    ).toString(),
                    group = topic.toString(),
                    data = message.toString()
                )
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })

        try {
            mqttClient?.connect(connOpts)
        } catch (e: MqttException) {
            println("mqtt error")
            e.printStackTrace()
//            stopSelf()
        }
    }

    private fun subscribe(mqttTopic: MqttTopic) {
        mqttClient?.subscribe(mqttTopic.topic, mqttTopic.qos.code)
        mqttClient?.let {
            topics.plus(mqttTopic.topic)
        }
    }

    private fun unsubscribe(mqttTopic: MqttTopic) {
        mqttClient?.unsubscribe(mqttTopic.topic)
        topics.minus(mqttTopic.topic)
    }


    private fun closeConnection() {
        if (mqttClient?.isConnected == true && mqttClient != null) {
            mqttClient?.disconnect()
            mqttClient = null
        }
    }

    companion object {
        const val FOREGROUND_NOTIFICATION_ID = 100
    }
}


private fun JsCommand.toMqttConnectConfig(): MqttConnectConfig {
    this.params?.let {
        return Json.decodeFromJsonElement<MqttConnectConfig>(it)
    } ?: throw CommandExecuteResult.Fail(
        methods = MQTT.methodConnect,
        message = "params mismatch"
    )
}

private fun JsCommand.toMqttTopic(methodName: String): MqttTopic {
    this.params?.let {
        return Json.decodeFromJsonElement<MqttTopic>(it)
    } ?: throw CommandExecuteResult.Fail(
        methods = methodName,
        message = "missing params"
    )
}

