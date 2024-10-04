package ru.tensor.sbis.design.message_panel.recorder_common.utils

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * Обертка микшера [MediaMuxer] для синхронизации нескольких дорожек.
 *
 * @property file файл, в который будет производиться запись.
 * @property encodersCount количество кодировщиков.
 * @param outputFormat формат выхода [MediaMuxer].
 * Использовать константы [MediaMuxer.OutputFormat] или [MUXER_OUTPUT_FLAC].
 *
 * @author vv.chekurda
 */
@SuppressLint("WrongConstant")
class MediaMuxerWrapper(
    private val file: File,
    private val encodersCount: Int = 1,
    outputFormat: Int = MUXER_OUTPUT_FLAC,
) {
    private val muxer: MediaMuxer?
    private val fileOutputStream: FileOutputStream?

    private var isRunning = false
    private var isWritingSuccessful = false

    private var startCallsCount: Int = 0

    init {
        if (outputFormat == MUXER_OUTPUT_FLAC) {
            fileOutputStream = FileOutputStream(file)
            muxer = null
        } else {
            fileOutputStream = null
            muxer = MediaMuxer(file.absolutePath, outputFormat)
        }
    }

    /**
     * Файл результата записи.
     * Если запись не закончилась или файл не был успешно записан - вернет null.
     */
    val resultFile: File?
        get() {
            return file.takeIf { it.exists() && isWritingSuccessful && !isRunning }
        }

    /**
     * Запустить микшер.
     */
    @Synchronized
    fun start() {
        if (isRunning || ++startCallsCount < encodersCount) return
        isRunning = true
        muxer?.start()
    }

    /**
     * Остановить микшер.
     */
    @Synchronized
    fun stop() {
        if (!isRunning) return
        isRunning = false
        try {
            fileOutputStream?.close()
            muxer?.stop()
            muxer?.release()
        } catch (ignore: Exception) {
            // Ошибки падают, если ничего не было записано, но остановить и зарелизить все-таки необходимо.
        }
    }

    /**
     * Добавить дорожку формата [format].
     */
    @Synchronized
    fun addTrack(format: MediaFormat): Int =
        muxer?.addTrack(format) ?: 1

    /**
     * Записать буффер [buffer] в микшер для дорожки [trackIndex].
     * @see MediaMuxer.writeSampleData
     */
    @Synchronized
    fun writeSampleData(trackIndex: Int, buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        if (!isRunning) return

        if (fileOutputStream != null) {
            val bytes = ByteArray(buffer.remaining())
            buffer[bytes]
            fileOutputStream.write(bytes)
        } else {
            muxer!!.writeSampleData(trackIndex, buffer, info)
        }
        isWritingSuccessful = true
    }
}

const val MUXER_OUTPUT_FLAC = -404