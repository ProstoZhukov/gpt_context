package ru.tensor.sbis.widget_player.widget.codeblock

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.richtext.util.RichTextAndroidUtil
import ru.tensor.sbis.widget_player.layout.TextHolderView
import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class CodeBlockRenderer(
    private val context: WidgetContext,
    private val options: CodeBlockOptions
) : GroupWidgetRenderer<CodeBlockElement> {

    override val view = CodeBlockView(context, options).apply {
        val verticalMargin = Offset.M.getDimenPx(context)
        setDefaultWidgetLayoutParams().apply {
            topMargin = verticalMargin
            bottomMargin = verticalMargin
        }
    }

    override fun addChild(child: View) {
        if (view.childCount == 0) {
            super.addChild(child)
            child.setCopyToClipboardListener { text ->
                RichTextAndroidUtil.copyToClipboard(context, text)
                true
            }
            (child.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                topMargin = options.padding.getValuePx(context)
                bottomMargin = topMargin
            }
        }
    }

    override fun removeChild(child: View) {
        if (view.childCount > 0) {
            child.setCopyToClipboardListener(null)
            super.removeChild(child)
        }
    }

    override fun removeChildAt(index: Int, child: View) {
        if (view.childCount > 0) {
            child.setCopyToClipboardListener(null)
            super.removeChildAt(index, child)
        }
    }

    override fun render(element: CodeBlockElement) {
        // ignore
    }

    private fun View.setCopyToClipboardListener(listener: ((text: String) -> Boolean)?) {
        (this as? TextHolderView)?.let { textHolder ->
            if (listener == null) {
                setOnLongClickListener(null)
            } else {
                setOnLongClickListener {
                    listener.invoke(textHolder.text.toString())
                }
            }
        }
    }
}