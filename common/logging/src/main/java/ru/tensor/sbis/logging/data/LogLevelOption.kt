package ru.tensor.sbis.logging.data

import ru.tensor.sbis.logging.R

/**
 * Уровни логирования.
 */
enum class LogLevelOption(val descriptionRes: Int) {
    DISABLED(R.string.logging_settings_log_level_disabled),
    MINIMAL(R.string.logging_settings_log_level_minimal),
    STANDARD(R.string.logging_settings_log_level_standart),
    EXTENDED(R.string.logging_settings_log_level_extended),
    DEBUG(R.string.logging_settings_log_level_debug)
}