package ru.tensor.sbis.design.period_picker.decl

import ru.tensor.sbis.design.period_picker.feature.SbisCompactPeriodPickerFragmentFeatureImpl
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl

/**
 * Ленивая инициализация фичи компонента Выбор периода.
 *
 * @author mb.kruglova
 */
fun createSbisPeriodPickerFeature(): Lazy<SbisPeriodPickerFeature> = lazy { SbisPeriodPickerFeatureImpl() }

fun createSbisCompactPeriodPickerFragmentFeature(): Lazy<SbisCompactPeriodPickerFragmentFeature> = lazy {
    SbisCompactPeriodPickerFragmentFeatureImpl()
}