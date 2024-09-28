package ru.tensor.sbis.richtext.converter.handler.view

import android.content.Context
import android.text.Editable
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.converter.TagAttributes
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM
import ru.tensor.sbis.richtext.span.view.block.BlockAttributesVM
import ru.tensor.sbis.richtext.RichTextPlugin
import ru.tensor.sbis.richtext.converter.cfg.style.BlockStyle

/**
 * Обработчик тегов для богатого текста, визуально отделенного в отдельный блок,
 * имеющий в начале блока иконку и выделение фоном. Пример - инфоблок.
 *
 * @author am.boldinov
 */
open class BlockTagHandler private constructor(
    private val context: SbisThemedContext,
    private val icon: IIcon?
) : ContentViewTagHandler(context) {

    constructor(context: Context, icon: IIcon? = null) : this(
        RichTextPlugin.themedContext(context),
        icon
    )

    private val style = BlockStyle(context)

    override fun onStartTag(stream: Editable, attributes: TagAttributes) {
        super.onStartTag(stream, attributes)
        startContent(stream)
    }

    override fun onEndTag(stream: Editable) {
        val content = stopContent(stream)
        (currentVM as? BlockAttributesVM)?.setContent(content)
        super.onEndTag(stream)
    }

    override fun createAttributesVM(attributes: TagAttributes): BaseAttributesVM {
        val icon = this.icon?.let {
            IconicsDrawable(context, it).apply {
                color(style.iconColor)
                sizePx(style.iconSize)
            }
        }
        return BlockAttributesVM(attributes.getTag(), icon)
    }
}