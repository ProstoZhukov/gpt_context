package ru.tensor.sbis.segmented_control.item.models

import ru.tensor.sbis.design.SbisMobileIcon

/**
 * @author ps.smirnyh
 */
sealed class SbisSegmentedControlIcon {

    /**
     * Размер иконки. Если не задан, применяется размер от стиля сегмент-контрола.
     */
    abstract val size: SbisSegmentedControlIconSize?

    /** Иконка. */
    abstract val icon: Char

    /**
     * Модель текстовой иконки
     *
     * @param icon Иконка из шрифта
     */
    data class SbisSegmentedControlTextIcon(
        override val icon: Char,
        override val size: SbisSegmentedControlIconSize? = null
    ) : SbisSegmentedControlIcon() {

        constructor(
            fontIcon: SbisMobileIcon.Icon,
            size: SbisSegmentedControlIconSize? = null
        ) : this(fontIcon.character, size)
    }
}
