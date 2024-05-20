package com.delta.mobileplatform.model.receive

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class MqttConnectConfig(
    val username: String,
    val password: String,
    val ip: String
)

@Serializable
data class MqttTopic(
    val topic: String,
    val qos: MqttQos = MqttQos.AtMostOnce
)

@Serializable(with = MqttQosSerializer::class)
enum class MqttQos(val code: Int) {
    AtMostOnce(0), AtLeastOnce(1), ExactlyOnce(2)
}


object MqttQosSerializer : KSerializer<MqttQos> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MqttQos", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: MqttQos) {
        encoder.encodeInt(value.code)
    }

    override fun deserialize(decoder: Decoder): MqttQos {
        val code = decoder.decodeInt()
        return MqttQos.values().first { it.code == code }
    }
}