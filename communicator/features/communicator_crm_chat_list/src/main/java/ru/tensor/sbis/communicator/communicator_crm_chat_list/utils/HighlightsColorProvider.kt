package ru.tensor.sbis.communicator.communicator_crm_chat_list.utils

import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * Провайдер цвета для подсветки текста при поиске.
 *
 * @author da.zhukov
 */
internal class HighlightsColorProvider(private val context: SbisThemedContext) {

    /** @SelfDocumented */
    fun getHighlightsColor() = context.getThemeColorInt(R.attr.textBackgroundColorDecoratorHighlight)
}