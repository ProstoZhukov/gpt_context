package ru.tensor.sbis.design.utils.theme

import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.theme.models.AbstractHeightModel

/**
 * Интерфейс для view, которые поддерживают размещение в строку, без привязки к конкретной линейке размеров.
 *
 * @author ra.geraskin
 */
interface AbstractHeightCompatibilityView<HEIGHT : AbstractHeightModel> {

    /**
     * Модель строчной высоты
     */
    val inlineHeight: HEIGHT

    /**
     * Устанавливает наиболее близкий к [height] размер из собственной реализации [HEIGHT]
     */
    fun setInlineHeight(height: AbstractHeight)

}