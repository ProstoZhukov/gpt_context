package ru.tensor.sbis.design.view.input.base.utils.factory

import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig

/**
 * Фабрика для создания [TextLayout] с переданной конфигурацией.
 *
 * @author ps.smirnyh
 */
internal class TextLayoutFactory {

    /** @SelfDocumented */
    fun create(config: TextLayoutConfig): TextLayout = TextLayout(config)
}