package ru.tensor.sbis.message_panel.helper.media

import ru.tensor.sbis.common.util.FileUtil
import java.io.File

/**
 * Данные записи медиа файлов.
 *
 * @property file записанный файл.
 * @property fileType тип файла.
 * @property duration продолжительность записи.
 *
 * @author vv.chekurda
 */
sealed interface MediaRecordData {
    val file: File
    val fileType: FileUtil.FileType
    val duration: Int
}

/**
 * Данные записи аудиосообщения.
 *
 * @property waveform осциллограмма.
 * @property emotionCode код эмоции.
 */
class AudioRecordData(
    override val file: File,
    override val duration: Int,
    val waveform: ByteArray,
    val emotionCode: Int? = null
) : MediaRecordData {
    override val fileType = FileUtil.FileType.AUDIO
}

/**
 * Данные записи видеосообщения.
 */
class VideoRecordData(
    override val file: File,
    override val duration: Int
) : MediaRecordData {
    override val fileType = FileUtil.FileType.VIDEO
}