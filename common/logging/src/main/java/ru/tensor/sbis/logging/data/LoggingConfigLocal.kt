package ru.tensor.sbis.logging.data

import ru.tensor.sbis.platform.logdelivery.generated.DiagnosticMode

/**
 * Конфигурация настроек логирования.
 *
 * @author av.krymov
 */
data class LoggingConfigLocal(
    val logLevelOption: LogLevelOption,
    val logDelayOption: LogDelayOption,
    val logStorageIntervalOption: LogStorageIntervalOption,
    val logUploadWifiOnlyOption: Boolean,
    val logQueryPlan: Boolean,
    val diagnosticOption: DiagnosticSettingsOption = DiagnosticSettingsOption.LOGS_ONLY
) {
    val diagnosticMode: DiagnosticMode = diagnosticOption.mode
}