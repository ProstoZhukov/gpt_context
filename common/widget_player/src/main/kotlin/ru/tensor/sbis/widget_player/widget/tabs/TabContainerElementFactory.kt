package ru.tensor.sbis.widget_player.widget.tabs

import org.json.JSONObject
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotEmpty
import timber.log.Timber
import java.util.LinkedList

/**
 * @author am.boldinov
 */
internal class TabContainerElementFactory : WidgetElementFactory<TabContainerElement> {

    override fun create(
        tag: String,
        attributes: WidgetAttributes,
        environment: WidgetEnvironment
    ): TabContainerElement {
        val tabs = LinkedList<SbisTabsViewItem>()
        attributes.parseTabs { tab ->
            tabs += SbisTabsViewItem(
                content = LinkedList<SbisTabViewItemContent>().apply {
                    add(
                        SbisTabViewItemContent.Text(
                            PlatformSbisString.Value(tab.optString("title"))
                        )
                    )
                }
            )
        }

        val data = TabContainerData(
            selectedIndex = 0,
            tabs = tabs
        )
        return TabContainerElement(tag, attributes, environment.resources, data)
    }

    private inline fun WidgetAttributes.parseTabs(callback: (tabItem: JSONObject) -> Unit) {
        getNotEmpty("variants")?.let { variants ->
            try {
                JSONObject(variants).optJSONArray("items")?.let { items ->
                    repeat(items.length()) { index ->
                        items.optJSONObject(index)?.let(callback)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}