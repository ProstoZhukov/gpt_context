package ru.tensor.sbis.widget_player.renderer.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.tensor.sbis.widget_player.api.WidgetSource
import ru.tensor.sbis.widget_player.config.WidgetBodyDecoration
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
internal interface WidgetPlayerStore :
    Store<WidgetPlayerStore.Intent, WidgetPlayerStore.State, WidgetPlayerStore.Label> {

    sealed interface Intent {
        class Install(val source: WidgetSource) : Intent
        class Decorate(val decoration: WidgetBodyDecoration) : Intent
        object Play : Intent
        object Pause : Intent
        class BodyAccess(val action: (body: WidgetBody) -> Unit): Intent
    }

    sealed interface Label

    data class State(
        val body: WidgetBody? = null,
        val changed: WidgetElement? = null,
        val isLoadingProcess: Boolean = false,
        val source: WidgetSource? = null,
        val playingState: PlayingState = PlayingState.PLAY,
        val decoration: WidgetBodyDecoration? = null
    )

    enum class PlayingState {
        PAUSE,
        PAUSE_DEFERRED,
        PLAY
    }
}