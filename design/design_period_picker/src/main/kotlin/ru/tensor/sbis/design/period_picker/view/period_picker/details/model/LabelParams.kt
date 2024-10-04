package ru.tensor.sbis.design.period_picker.view.period_picker.details.model

import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore

/**
 * Параметры для сайд-эффектов.
 *
 * @author mb.kruglova
 */
internal data class LabelParams(
    val isCompact: Boolean = false,
    val isFragment: Boolean = false,
    val hostStore: PeriodPickerStore? = null
)