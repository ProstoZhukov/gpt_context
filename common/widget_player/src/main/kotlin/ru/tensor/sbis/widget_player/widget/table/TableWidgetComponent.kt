package ru.tensor.sbis.widget_player.widget.table

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.attributes.store.getAsInt
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget
import ru.tensor.sbis.widget_player.widget.table.model.TableCellElement
import ru.tensor.sbis.widget_player.widget.table.model.TableRowElement

/**
 * @author am.boldinov
 */
internal class TableWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = WidgetElementFactory.tree(
            factory = TableElementFactory(tableOptions),
            children = {
                child("TableRow", "ModuleEditor/table:Row", factory = { tag, attributes, environment ->
                    TableRowElement(tag, attributes, environment.resources)
                }).child("TableCell", "ModuleEditor/table:Cell", factory = { tag, attributes, environment ->
                    TableCellElement(
                        tag,
                        attributes,
                        environment.resources,
                        colSpan = attributes.getAsInt("colspan"),
                        rowSpan = attributes.getAsInt("rowspan")
                    )
                })
            }),
        inflater = {
            GroupWidget(
                context = this,
                renderer = TableRenderer(this, tableOptions)
            )
        }
    )
}