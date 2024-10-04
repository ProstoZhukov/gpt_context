package ru.tensor.sbis.design.rating.utils

import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig

/**
 * @SelfDocumented
 *
 * @author ps.smirnyh
 */
internal class RatingTextLayoutFactory {

    /** @SelfDocumented */
    internal fun create(config: TextLayoutConfig) = TextLayout(config)
}