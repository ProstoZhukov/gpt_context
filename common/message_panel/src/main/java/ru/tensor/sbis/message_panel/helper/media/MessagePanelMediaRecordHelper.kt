package ru.tensor.sbis.message_panel.helper.media

import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.tensor.sbis.common_attachments.Attachment
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WaveformUtils
import java.nio.charset.StandardCharsets

/**
 * Вспомогательная реализация для создания метаинформации для отправки аудио и видео сообщений.
 *
 * @author vv.chekurda
 */
internal object MessagePanelMediaRecordHelper {

    /**
     * Создать мета-информацию для отправки медиа сообщения.
     *
     * @param data данные записи медиа сообщения.
     */
    suspend fun createMetaData(data: MediaRecordData): String =
        withContext(Dispatchers.Default) {
            when (data) {
                is AudioRecordData -> {
                    createAudioRecordMeta(
                        duration = data.duration,
                        waveform = data.waveform,
                        emotion = data.emotionCode ?: 0
                    )
                }
                is VideoRecordData -> {
                    createVideoRecordMeta(data.duration)
                }
            }
        }

    /**
     * Создать вложения сообщения по результату записи медиа данных [data].
     */
    fun createMediaMessageAttachment(data: MediaRecordData): Attachment {
        val uri = Uri.fromFile(data.file)
        return if (uri.scheme != "file") {
            throw IllegalArgumentException("Only file scheme supported $uri")
        } else {
            Attachment(uri.toString(), uri.lastPathSegment!!, data.fileType, null)
        }
    }

    /**
     * Создать метаинформацию для отправки аудиосообщения.
     *
     * @param duration продолжительность записи в секундах.
     * @param waveform осциллограмма.
     * @param emotion целочисленный код эмоции.
     */
    private fun createAudioRecordMeta(duration: Int, waveform: ByteArray, emotion: Int): String {
        val encodedWaveform = WaveformUtils.encodeWaveform(waveform)
        val waveform64 = Base64.encode(encodedWaveform, Base64.NO_WRAP)
        val waveformString = String(waveform64, StandardCharsets.UTF_8)
        return JSONObject()
            .put(RECORD_META_TYPE_KEY, RECORD_META_TYPE_AUDIO_MESSAGE)
            .put(RECORD_META_DURATION_KEY, duration)
            .put(RECORD_META_WAVEFORM_KEY, waveformString)
            .put(RECORD_META_SEND_TYPE_KEY, emotion)
            .toString()
    }

    /**
     * Создать метаинформацию для отправки видеосообщения.
     *
     * @param duration продолжительность записи в секундах.
     */
    private fun createVideoRecordMeta(duration: Int): String =
        JSONObject()
            .put(RECORD_META_TYPE_KEY, RECORD_META_TYPE_VIDEO_MESSAGE)
            .put(RECORD_META_DURATION_KEY, duration)
            .toString()
}

private const val RECORD_META_TYPE_KEY = "type"
private const val RECORD_META_DURATION_KEY = "duration"
private const val RECORD_META_WAVEFORM_KEY = "oscillogram"
private const val RECORD_META_SEND_TYPE_KEY = "emotion"
private const val RECORD_META_TYPE_AUDIO_MESSAGE = "audio_message"
private const val RECORD_META_TYPE_VIDEO_MESSAGE = "video_message"