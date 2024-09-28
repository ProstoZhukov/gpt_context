package ru.tensor.sbis.logging.data

import ru.tensor.sbis.logging.R

/**
 * Режимы записи логов.
 *
 * @author av.krymov
 */
enum class LogDelayOption(
    val descriptionRes: Int
) {
    DELAYED(R.string.logging_settings_log_delay_delayed),
    IMMEDIATE(R.string.logging_settings_log_delay_immediate);
}