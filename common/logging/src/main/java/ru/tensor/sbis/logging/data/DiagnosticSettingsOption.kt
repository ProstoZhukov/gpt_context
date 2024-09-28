package ru.tensor.sbis.logging.data

import androidx.annotation.StringRes
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.platform.logdelivery.generated.DiagnosticMode

/**
 * Настройки данных для диагностики.
 *
 * @author av.krymov
 */
enum class DiagnosticSettingsOption(@StringRes val descriptionRes: Int, val mode: DiagnosticMode) {
    /** Только логи */
    LOGS_ONLY(R.string.logging_settings_diagnostic_settings_only_logs, DiagnosticMode.LOGS_ONLY),

    /** Полные */
    FULL(R.string.logging_settings_diagnostic_settings_full, DiagnosticMode.FULL),

    /** Выборочные */
    SELECTIVE(R.string.logging_settings_diagnostic_settings_selective, DiagnosticMode.SELECTIVE);

    companion object {
        fun fromMode(enum: DiagnosticMode): DiagnosticSettingsOption {
            return when (enum) {
                DiagnosticMode.LOGS_ONLY -> LOGS_ONLY
                DiagnosticMode.SELECTIVE -> SELECTIVE
                DiagnosticMode.FULL -> FULL
            }
        }
    }
}