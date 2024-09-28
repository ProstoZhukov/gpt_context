package ru.tensor.sbis.widget_player.widget.list.root

import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.element.findParentAs

/**
 * @author am.boldinov
 */
internal class ListViewElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    private val remoteConfig: ListViewConfig?,
    localDefaultConfig: ListViewConfig,
    val startIndex: Int
) : GroupWidgetElement(tag, attributes, resources) {

    var config: ListViewConfig = remoteConfig ?: localDefaultConfig
        private set
    var level: Int = 0
        private set

    override fun onAttachedToHierarchy(parent: WidgetElement) {
        super.onAttachedToHierarchy(parent)
        level = findParentAs<ListViewElement>()?.let {
            it.level + 1
        } ?: level
        if (remoteConfig == null) { // если конфиг не был установлен - пытаемся взять от родителя
            findParentAs<ListViewElement> {
                it.remoteConfig != null
            }?.let { root ->
                config = root.config
            }
        }
    }
}