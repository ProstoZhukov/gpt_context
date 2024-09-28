package ru.tensor.sbis.widget_player.renderer

import android.graphics.Rect
import android.os.Trace
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.widget_player.WidgetPlayer
import ru.tensor.sbis.widget_player.api.ScrollingMode
import ru.tensor.sbis.widget_player.api.WidgetPlayerOffsetApi
import ru.tensor.sbis.widget_player.config.WidgetStore
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget
import ru.tensor.sbis.widget_player.layout.widget.Widget
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.renderer.WidgetViewBodyRenderer.Event
import ru.tensor.sbis.widget_player.renderer.WidgetViewBodyRenderer.Model
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.R
import ru.tensor.sbis.widget_player.layout.internal.HostAccess
import ru.tensor.sbis.widget_player.layout.internal.WidgetHostAccessor
import ru.tensor.sbis.widget_player.layout.internal.WidgetStateStore

/**
 * @author am.boldinov
 */
internal class WidgetPlayerBodyRenderer(
    private val player: WidgetPlayer,
    private val stateStore: Lazy<WidgetStateStore>
) : BaseMviView<Model, Event>(), WidgetViewBodyRenderer,
    WidgetPlayerOffsetApi by WidgetPlayerOffsetDecorator(player) {

    private val originContext = player.context
    private val widgetContext get() = WidgetContext(originContext)

    private val rootChild get() = player.getChildAt(0)

    private val widgetCache = mutableMapOf<WidgetID, Widget<WidgetElement>>()

    private val hostAccessor = object : WidgetHostAccessor {
        override fun accessTo(access: HostAccess) {
            dispatch(Event.WidgetAccessTo(access))
        }
    }

    override var scrollingMode: ScrollingMode = ScrollingMode.NONE
        set(value) {
            if (field != value) {
                field = value
                when (value) {
                    ScrollingMode.NONE -> {
                        (rootChild as? NestedScrollView)?.let {
                            player.removeView(it)
                            it.getChildAt(0)?.let { root ->
                                it.removeView(root)
                                player.addView(root)
                            }
                        }
                    }

                    ScrollingMode.VERTICAL -> {
                        rootChild.takeIf { it !is NestedScrollView }.let { root ->
                            if (root != null) {
                                player.removeView(root)
                            }
                            player.addView(NestedScrollView(originContext).apply {
                                setDefaultWidgetLayoutParams()
                                if (root != null) {
                                    addView(root)
                                }
                            })
                        }
                    }
                }

            }
        }

    override val renderer: ViewRenderer<Model> =
        diff {
            diff(get = Model::body, set = {
                val host = if (scrollingMode == ScrollingMode.VERTICAL) {
                    rootChild as? ViewGroup ?: player
                } else player

                val attachedWidgets = player.getAttachedWidgets()
                attachedWidgets.clear()
                val currentRootWidget = player.getRootWidget()
                if (currentRootWidget != null) {
                    removeAllWidgetsRecursive(widgetCache, currentRootWidget)
                    host.removeView(currentRootWidget.view) // TODO dispatch detached/attached events
                    player.setRootWidget(null)
                }

                Trace.beginSection("WidgetPlayer#fillWidgets")
                it?.let { body ->
                    val rootElement = body.elements.root
                    fillWidgets(
                        cache = widgetCache,
                        store = body.store,
                        parentElement = null,
                        element = rootElement,
                        host = null,
                        attachedWidgets = attachedWidgets
                    )
                    attachedWidgets[rootElement.id]?.let { rootWidget ->
                        host.addView(rootWidget.view)
                        player.setRootWidget(rootWidget as GroupWidget<*>)
                    }
                }
                widgetCache.values.forEach { widget ->
                    widget.dispatchDestroy()
                }
                widgetCache.clear()
                Trace.endSection()
            })
            diff(get = Model::changed, set = {
                it?.let { element ->
                    player.getAttachedWidgets()[element.id]?.bind(element) // TODO подумать если изменится таблица
                }
            })
        }

    override fun getVisibleWidgetRect(id: WidgetID): Rect? {
        return findViewByWidgetId(id)?.let {
            val rect = Rect()
            getVisibleWidgetRect(it, rect)
            rect
        }
    }

    override fun getVisibleWidgetRect(widget: View, rect: Rect) {
        widget.getDrawingRect(rect)
        player.offsetDescendantRectToMyCoords(widget, rect)
    }

    override fun findViewByWidgetId(id: WidgetID) = player.getAttachedWidgets()[id]?.view

    private fun removeAllWidgetsRecursive(
        cache: MutableMap<WidgetID, Widget<WidgetElement>>,
        root: GroupWidget<*>
    ) {
        Trace.beginSection("WidgetPlayer#removeAllWidgetsRecursive")
        for (i in root.children.size - 1 downTo 0) { // удаление элементов с конца без сдвига индексов
            val widget = root.children[i]
            if (widget is GroupWidget<*>) {
                removeAllWidgetsRecursive(cache, widget)
            }
            val removed = root.removeChildAt(i)
            val id = removed.id
            if (id != null) {
                cache[id] = removed
            }
        }
        Trace.endSection()
    }

    private fun fillWidgets(
        cache: MutableMap<WidgetID, Widget<WidgetElement>>,
        store: WidgetStore,
        parentElement: GroupWidgetElement?,
        element: WidgetElement,
        host: GroupWidget<*>?,
        attachedWidgets: MutableMap<WidgetID, Widget<WidgetElement>>
    ) {
        val filled = store.get(element.tag)?.inflater?.let { inflater ->
            Trace.beginSection("WidgetPlayer#inflateWidget_${element.tag}")
            val widget = cache.remove(element.id) ?: inflater.run {
                widgetContext.inflate().also {
                    it.dispatchInitialize(player.lifecycle, stateStore, hostAccessor)
                }
            }
            Trace.endSection()
            host?.addChild(widget, parentElement)
            attachedWidgets[element.id] = widget
            Trace.beginSection("WidgetPlayer#bindWidget_${element.tag}")
            widget.bind(element)
            Trace.endSection()
            widget
        } ?: run {
            // если для элемента отсутствует виджет - родитель отрисовывает самостоятельно, продолжаем рендеринг вглубь
            host
        }
        if (filled is GroupWidget<*> && element is GroupWidgetElement) {
            element.children.forEach {
                fillWidgets(
                    cache = cache,
                    store = store,
                    parentElement = element,
                    element = it,
                    host = filled,
                    attachedWidgets = attachedWidgets
                )
            }
        }
    }

    private fun WidgetPlayer.getAttachedWidgets(): MutableMap<WidgetID, Widget<WidgetElement>> {
        @Suppress("UNCHECKED_CAST")
        return getTag(R.id.widget_player_attached_widgets) as? MutableMap<WidgetID, Widget<WidgetElement>> ?: run {
            mutableMapOf<WidgetID, Widget<WidgetElement>>().also {
                setTag(R.id.widget_player_attached_widgets, it)
            }
        }
    }

    private fun WidgetPlayer.setRootWidget(widget: GroupWidget<*>?) {
        setTag(R.id.widget_player_root_widget, widget)
    }

    private fun WidgetPlayer.getRootWidget(): GroupWidget<*>? {
        return getTag(R.id.widget_player_root_widget) as? GroupWidget<*>
    }
}