package ru.tensor.sbis.widget_player.renderer

import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.widget_player.api.WidgetPlayerViewApi
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.internal.HostAccess
import ru.tensor.sbis.widget_player.renderer.WidgetViewBodyRenderer.Event
import ru.tensor.sbis.widget_player.renderer.WidgetViewBodyRenderer.Model

/**
 * @author am.boldinov
 */
internal interface WidgetViewBodyRenderer : MviView<Model, Event>, WidgetPlayerViewApi {

    sealed interface Event {

        class WidgetAccessTo(val access: HostAccess) : Event
    }

    class Model(
        val body: WidgetBody?,
        val changed: WidgetElement?
    )
}