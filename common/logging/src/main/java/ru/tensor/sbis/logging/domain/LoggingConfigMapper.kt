package ru.tensor.sbis.logging.domain

import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LogDelayOption
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.data.LogStorageIntervalOption
import ru.tensor.sbis.logging.data.LoggingConfigLocal
import ru.tensor.sbis.platform.generated.LogLevel
import ru.tensor.sbis.platform.logdelivery.generated.Config
import ru.tensor.sbis.platform.logdelivery.generated.WritingMode

/**
 * Класс для маппинга моделей контроллера в модели предстваления.
 *
 * @author av.krymov
 */
internal object LoggingConfigMapper {

    fun toPresentation(config: Config): LoggingConfigLocal {
        return LoggingConfigLocal(
            logLevelOption = matchLogLevel(config.logLevel),
            logDelayOption = matchWriteMode(config.writingMode),
            logStorageIntervalOption = matchStorageMode(config.holdPeriodDays),
            logUploadWifiOnlyOption = config.uploadWifiOnly,
            logQueryPlan = config.logQueryPlan,
            diagnosticOption = DiagnosticSettingsOption.fromMode(config.diagnosticMode)
        )
    }

    fun toController(localConfig: LoggingConfigLocal): Config {
        return Config(
            unmatchLogLevel(localConfig.logLevelOption),
            unmatchWritingMode(localConfig.logDelayOption),
            localConfig.logStorageIntervalOption.daysCount,
            localConfig.logUploadWifiOnlyOption,
            localConfig.logQueryPlan,
            localConfig.diagnosticMode
        )
    }

    private fun matchLogLevel(enum: LogLevel): LogLevelOption {
        return when (enum) {
            LogLevel.MINIMAL -> LogLevelOption.MINIMAL
            LogLevel.STANDARD -> LogLevelOption.STANDARD
            LogLevel.EXTENDED -> LogLevelOption.EXTENDED
            LogLevel.DEBUG -> LogLevelOption.DEBUG
            LogLevel.DISABLED -> LogLevelOption.DISABLED
        }
    }

    private fun matchWriteMode(enum: WritingMode): LogDelayOption {
        return when (enum) {
            WritingMode.DELAYED -> LogDelayOption.DELAYED
            WritingMode.IMMEDIATE -> LogDelayOption.IMMEDIATE
        }
    }

    fun unmatchLogLevel(enum: LogLevelOption): LogLevel {
        return when (enum) {
            LogLevelOption.MINIMAL -> LogLevel.MINIMAL
            LogLevelOption.STANDARD -> LogLevel.STANDARD
            LogLevelOption.EXTENDED -> LogLevel.EXTENDED
            LogLevelOption.DEBUG -> LogLevel.DEBUG
            LogLevelOption.DISABLED -> LogLevel.DISABLED
        }
    }

    fun unmatchWritingMode(enum: LogDelayOption): WritingMode {
        return when (enum) {
            LogDelayOption.DELAYED -> WritingMode.DELAYED
            LogDelayOption.IMMEDIATE -> WritingMode.IMMEDIATE
        }
    }

    private fun matchStorageMode(holdPeriodDays: Short): LogStorageIntervalOption {
        return when (holdPeriodDays) {
            1.toShort() -> LogStorageIntervalOption.ONE_DAY
            2.toShort() -> LogStorageIntervalOption.TWO_DAYS
            3.toShort() -> LogStorageIntervalOption.THREE_DAYS
            7.toShort() -> LogStorageIntervalOption.A_WEEK
            else -> throw IllegalStateException("unsupported holdPeriodDays param")
        }
    }
}