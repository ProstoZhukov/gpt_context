package ru.tensor.sbis.design.message_panel.video_recorder.view.contract

import java.io.File

/**
 * Результат записи видеосообщения.
 *
 * @param videoFile видеофайл.
 * @param duration продолжительность записи в секундах.
 */
data class VideoRecordResultData(
    val videoFile: File,
    val duration: Int
)