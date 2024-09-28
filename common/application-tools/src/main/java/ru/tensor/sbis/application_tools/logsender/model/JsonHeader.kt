package ru.tensor.sbis.application_tools.logsender.model

import com.google.gson.annotations.SerializedName

/**
 * Модель заголовка файла логов
 *
 * @author us.bessonov
 */
internal data class JsonHeader(
    @SerializedName("device_id")
    val deviceId: String?,
    @SerializedName("device_name")
    val deviceName: String?,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("user_agent")
    val userAgent: String
)