package ru.tensor.sbis.widget_player.widget.list.root

import ru.tensor.sbis.widget_player.layout.widget.GroupWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.widget.list.item.ListItemElement

/**
 * @author am.boldinov
 */
internal class ListViewRenderer(
    context: WidgetContext
) : GroupWidgetRenderer<ListViewElement> {

    override val view = ListViewLayout(context).apply {
        setDefaultWidgetLayoutParams()
    }

    override fun render(element: ListViewElement) {
        var checkedCount = 0
        val checkedIndexes = Array(element.children.size) { index ->
            ((element.children[index] as? ListItemElement)?.checked ?: true).also {
                if (it) {
                    checkedCount++
                }
            }
        }
        view.configure(
            config = element.config,
            properties = ListIndexProperties(
                level = element.level,
                startIndex = element.startIndex,
                checkedIndexes = checkedIndexes,
                checkedCount = checkedCount
            )
        )
    }
}