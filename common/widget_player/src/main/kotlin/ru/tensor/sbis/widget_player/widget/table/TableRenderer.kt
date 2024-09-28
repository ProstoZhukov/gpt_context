package ru.tensor.sbis.widget_player.widget.table

import android.view.View
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.layout.widget.TreeWidgetRenderer
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.widget.table.model.TableCellElement

/**
 * @author am.boldinov
 */
internal class TableRenderer(
    context: WidgetContext,
    options: TableOptions
) : TreeWidgetRenderer<TableElement> {

    override val view = TableView(context, options).apply {
        setDefaultWidgetLayoutParams().apply {
            topMargin = options.marginTop.getValuePx(context)
            bottomMargin = options.marginBottom.getValuePx(context)
        }
    }

    override fun render(element: TableElement) {
        with(view) {
            setViewData(element.viewData)
        }
    }

    override fun addChild(parent: GroupWidgetElement, child: View) {
        if (parent is TableCellElement) {
            view.findCellContainer(parent.id)?.addView(child)
        }
    }
}