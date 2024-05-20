package com.delta.mobileplatform.model.receive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MqttMessageData(
    @SerialName("MsgBody")
    val msgBody: MsgBody,
    @SerialName("MsgHead")
    val msgHead: MsgHead
)

@Serializable
data class MsgBody(
    @SerialName("Content")
    val content: String,
    @SerialName("ExtraInfo")
    val extraInfo: String? = null,
    @SerialName("SenderName")
    val senderName: String? = null,
    @SerialName("Topic")
    val topic: String
)

@Serializable
data class MsgHead(
    @SerialName("CategoryId")
    val categoryId: String,
    @SerialName("CategoryType")
    val categoryType: String,
    @SerialName("MessageId")
    val messageId: String,
    @SerialName("System")
    val system: String,
    @SerialName("Version")
    val version: String? = null
)