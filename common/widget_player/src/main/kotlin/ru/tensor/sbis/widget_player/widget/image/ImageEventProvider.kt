package ru.tensor.sbis.widget_player.widget.image

import com.arkivanov.mvikotlin.core.view.ViewEvents

/**
 * @author am.boldinov
 */
internal interface ImageEventProvider : ViewEvents<ImageEventProvider.Event> {

    sealed interface Event {

        object ImageClicked : Event
    }
}

