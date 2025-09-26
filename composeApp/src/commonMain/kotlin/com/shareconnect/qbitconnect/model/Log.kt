package com.shareconnect.qbitconnect.model

import com.shareconnect.qbitconnect.model.serializers.InstantSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

@Serializable
data class Log(
    val id: Int,
    val message: String,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    @Serializable(with = LogTypeSerializer::class)
    val type: LogType,
)

private object LogTypeSerializer : KSerializer<LogType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LogType", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: LogType) {
        throw UnsupportedOperationException()
    }

    override fun deserialize(decoder: Decoder): LogType {
        return when (val intValue = decoder.decodeInt()) {
            1 -> LogType.NORMAL
            2 -> LogType.INFO
            4 -> LogType.WARNING
            8 -> LogType.CRITICAL
            else -> throw IllegalStateException("Unknown log type: $intValue")
        }
    }
}

enum class LogType {
    NORMAL,
    INFO,
    WARNING,
    CRITICAL,
}
