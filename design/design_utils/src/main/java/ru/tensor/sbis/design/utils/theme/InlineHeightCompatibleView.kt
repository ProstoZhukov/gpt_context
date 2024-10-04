package ru.tensor.sbis.design.utils.theme

import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.models.InlineHeightModel

/**
 * Интерфейс для view, которые поддерживают размещение в строку
 *
 * @author ma.kolpakov
 */
interface InlineHeightCompatibleView<out HEIGHT : InlineHeightModel> {

    /**
     * Модель строчной высоты
     */
    val inlineHeight: HEIGHT

    /**
     * Устанавливает наиболее близкий к [height] размер из собственной реализации [HEIGHT]
     */
    fun setInlineHeight(height: InlineHeight)
}