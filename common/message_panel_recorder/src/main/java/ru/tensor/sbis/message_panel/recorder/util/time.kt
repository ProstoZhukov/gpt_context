/**
 * Инструменты для работы со временем в панели записи аудио сообщения
 *
 * @author vv.chekurda
 * Создан 8/2/2019
 */
package ru.tensor.sbis.message_panel.recorder.util

/**
 * Ограничение времени обусловленное выбранным форматом (предел 99:59)
 */
internal const val TIME_FORMAT_LIMIT = 5999L
internal const val DEFAULT_TIME = "00:00"

/**
 * Преобразование времени **в секундах** в форматированную строку
 *
 * @exception IllegalArgumentException при выходе из диапазона `[0, TIME_FORMAT_LIMIT]`
 *
 * @see TIME_FORMAT_LIMIT
 */
internal fun Long.toTimeString(): String =
    if (this in 0..TIME_FORMAT_LIMIT)
        String.format("%02d:%02d", this / 60L, this % 60L)
    else
        throw IllegalArgumentException("$this out of range [0, $TIME_FORMAT_LIMIT]")