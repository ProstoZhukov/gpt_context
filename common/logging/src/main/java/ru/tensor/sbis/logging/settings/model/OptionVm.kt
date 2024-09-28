package ru.tensor.sbis.logging.settings.model

import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LogDelayOption
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.data.LogStorageIntervalOption

/**
 * Модель представления опции в списке.
 *
 * @param isCurrent опция выбрана.
 *
 * @author av.krymov
 */
sealed class OptionVm(val isCurrent: Boolean, val optionName: Int) {

    class Level(
        isCurrent: Boolean,
        val option: LogLevelOption
    ) : OptionVm(isCurrent, option.descriptionRes)

    class Delay(
        isCurrent: Boolean,
        val option: LogDelayOption
    ) : OptionVm(isCurrent, option.descriptionRes)

    class StorageInterval(
        isCurrent: Boolean,
        val option: LogStorageIntervalOption
    ) : OptionVm(isCurrent, option.descriptionRes)

    class DiagnosticSettings(
        isCurrent: Boolean,
        val option: DiagnosticSettingsOption
    ) : OptionVm(isCurrent, option.descriptionRes)
}