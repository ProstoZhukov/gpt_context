package ru.tensor.sbis.message_panel.recorder

/**
 * @author vv.chekurda
 * @since 7/25/2019
 */

/**
 * Время отображения подсказки об аудиозаписи
 */
internal const val RECORDER_HINT_HIDE_DELAY = 3000L

/**
 * Максимальный размер файла для записи (100 MB). Нужно следить за тем, чтобы размер не превышал внешние ограничения,
 * например допустимый размер вложения
 */
internal const val RECORDER_MAX_FILE_SIZE = 104857600L

/**
 * Максимальная длина записи 3 минуты
 */
internal const val RECORDER_MAX_DURATION = 180_000