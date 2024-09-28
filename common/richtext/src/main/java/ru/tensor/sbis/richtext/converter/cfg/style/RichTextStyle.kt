package ru.tensor.sbis.richtext.converter.cfg.style

import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

private typealias DesignAttr = ru.tensor.sbis.design.R.attr

/**
 * Стили богатого текста.
 *
 * @author am.boldinov
 */

internal class InlineCodeStyle(
    private val context: SbisThemedContext
) {
    val backgroundColor
        get() = ThemeTokensProvider.getColorInt(context, DesignAttr.paleColor)

    val textColor get() = StyleColor.DANGER.getTextColor(context)
}

internal class BlockStyle(
    private val context: SbisThemedContext
) {
    val iconColor get() = StyleColor.SECONDARY.getIconColor(context)

    val iconSize get() = IconSize.XL.getDimenPx(context)
}