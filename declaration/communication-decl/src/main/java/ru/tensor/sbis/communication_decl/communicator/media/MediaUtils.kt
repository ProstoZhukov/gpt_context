package ru.tensor.sbis.communication_decl.communicator.media

import org.json.JSONObject

/**
 * Полчуить сервисный обьект.
 */
fun getServiceObject(serviceObject: String): JSONObject? {
    if (serviceObject.isEmpty()) return null
    return JSONObject(serviceObject)
}

/**
 * Является ли это сообщение аудиосообщением.
 */
fun JSONObject?.isAudioMessage(): Boolean =
    this?.optString("type") == "audio_message"

/**
 * Является ли это сообщение видеосообщением.
 */
fun JSONObject?.isVideoMessage(): Boolean =
    this?.optString("type") == "video_message"

/**
 * Получить статус распознанности текста медиасообщения.
 */
fun JSONObject.getRecognized(): Boolean? =
    if (has(RECOGNIZED_KEY)) optBoolean(RECOGNIZED_KEY) else null

/**
 * Получить продолжительность медиасообщения.
 */
fun JSONObject.getDuration(): Int =
    optInt(DURATION_KEY, 0)

private const val RECOGNIZED_KEY = "recognized"
private const val DURATION_KEY = "duration"