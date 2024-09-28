package ru.tensor.sbis.widget_player.renderer

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.states
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.mvi_extension.subscribe
import ru.tensor.sbis.widget_player.api.ScrollingMode
import ru.tensor.sbis.widget_player.api.WidgetPlayerApi
import ru.tensor.sbis.widget_player.api.WidgetSource
import ru.tensor.sbis.widget_player.config.WidgetBodyDecorationBuilder
import ru.tensor.sbis.widget_player.config.WidgetStore
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.WidgetStoreOwner
import ru.tensor.sbis.widget_player.layout.internal.HostAccess
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStore
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStoreFactory
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStore.State
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStore.Intent
import ru.tensor.sbis.widget_player.renderer.WidgetViewBodyRenderer.Model
import ru.tensor.sbis.widget_player.renderer.WidgetViewBodyRenderer.Event

/**
 * @author am.boldinov
 */
internal class WidgetPlayerApiMediator : WidgetPlayerApi, WidgetStoreOwner {

    private lateinit var renderer: WidgetViewBodyRenderer
    private lateinit var store: WidgetPlayerStore

    override var widgetStore: WidgetStore = WidgetStore.EMPTY

    override var scrollingMode: ScrollingMode
        get() = renderer.scrollingMode
        set(value) {
            renderer.scrollingMode = value
        }

    override var navigationPanelPadding: Boolean
        get() = renderer.navigationPanelPadding
        set(value) {
            renderer.navigationPanelPadding = value
        }

    override var floatingPanelPadding: Boolean
        get() = renderer.floatingPanelPadding
        set(value) {
            renderer.floatingPanelPadding = value
        }

    override val body: WidgetBody?
        get() = store.state.body

    fun attachTo(
        lifecycle: Lifecycle,
        renderer: WidgetViewBodyRenderer,
        storeFactory: WidgetPlayerStoreFactory
    ) {
        this.renderer = renderer
        store = storeFactory.create()
        val binder = bind(
            mainContext = Dispatchers.Main.immediate
        ) {
            renderer.events.map { it.toIntent() } bindTo store
            store.states.map { it.toModel() } bindTo renderer
        }
        lifecycle.subscribe(
            onStart = {
                binder.start()
                store.accept(Intent.Play)
            },
            onStop = {
                store.accept(Intent.Pause)
                binder.stop()
            },
            onDestroy = {
                store.dispose()
            }
        )
    }

    override fun setWidgetSource(source: WidgetSource) {
        store.accept(Intent.Install(source))
    }

    override fun getVisibleWidgetRect(id: WidgetID) = renderer.getVisibleWidgetRect(id)

    override fun getVisibleWidgetRect(widget: View, rect: Rect) = renderer.getVisibleWidgetRect(widget, rect)

    override fun findViewByWidgetId(id: WidgetID) = renderer.findViewByWidgetId(id)

    override fun decorate(decoration: WidgetBodyDecorationBuilder.() -> Unit) {
        val builder = WidgetBodyDecorationBuilder()
        decoration.invoke(builder)
        store.accept(Intent.Decorate(builder.build()))
    }

    private fun State.toModel() = Model(
        body = body,
        changed = changed
    )

    private fun Event.toIntent() = when (this) {
        is Event.WidgetAccessTo -> {
            when (access) {
                is HostAccess.Body -> Intent.BodyAccess(access.action)
            }
        }
    }
}