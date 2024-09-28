package ru.tensor.sbis.logging.settings.model

import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LogDelayOption
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.data.LogStorageIntervalOption
import ru.tensor.sbis.logging.data.LoggingConfigLocal

/**
 * Модель представления категории в списке.
 *
 */
interface CategoryVm {
    val headerTitle: Int
    val categoryName: Int
    val categoryValue: Int

    fun toChildVm(configLocal: LoggingConfigLocal): List<Any>
}

class Level(
    levelOption: LogLevelOption
) : CategoryVm {

    override val categoryName = R.string.logging_settings_level_label
    override val categoryValue = levelOption.descriptionRes
    override val headerTitle = R.string.logging_settings_header_title_level

    override fun toChildVm(configLocal: LoggingConfigLocal): List<Any> = availableLevelOptionValues().map {
        OptionVm.Level(
            option = it,
            isCurrent = configLocal.logLevelOption == it
        )
    }

    private fun availableLevelOptionValues(): Array<LogLevelOption> {
        return arrayOf(
            LogLevelOption.MINIMAL,
            LogLevelOption.STANDARD,
            LogLevelOption.EXTENDED,
            LogLevelOption.DEBUG
        )
    }
}

data class Delay(
    private val delayOption: LogDelayOption
) : CategoryVm {
    override val categoryName = R.string.logging_settings_delay_label
    override val categoryValue = delayOption.descriptionRes
    override val headerTitle = R.string.logging_settings_header_title_writing_mode

    override fun toChildVm(configLocal: LoggingConfigLocal): List<Any> = LogDelayOption.values().map {
        OptionVm.Delay(
            option = it,
            isCurrent = configLocal.logDelayOption == it
        )
    }
}

data class StorageInterval(
    private val logStorageIntervalOption: LogStorageIntervalOption
) : CategoryVm {
    override val categoryName = R.string.logging_settings_storage_interval_label
    override val categoryValue = logStorageIntervalOption.descriptionRes
    override val headerTitle = R.string.logging_settings_header_title_storage_interval
    override fun toChildVm(configLocal: LoggingConfigLocal): List<Any> = LogStorageIntervalOption.values().map {
        OptionVm.StorageInterval(
            option = it,
            isCurrent = configLocal.logStorageIntervalOption == it
        )
    }
}

data class DiagnosticSettings(
    private val diagnosticSettingsOption: DiagnosticSettingsOption
) : CategoryVm {
    override val categoryName = R.string.logging_settings_header_title_diagnostic_settings
    override val categoryValue = diagnosticSettingsOption.descriptionRes
    override val headerTitle = R.string.logging_settings_header_title_diagnostic_settings
    override fun toChildVm(configLocal: LoggingConfigLocal): List<Any> = DiagnosticSettingsOption.values().map {
        OptionVm.DiagnosticSettings(
            option = it,
            isCurrent = configLocal.diagnosticOption == it
        )
    }
}