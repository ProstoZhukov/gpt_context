package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore

/**
 * Контракт view компонента MVI содержимого экрана добавления ссылки.
 *
 * @author dv.baranov
 */
internal interface LinkAdditionView : MviView<LinkAdditionView.Model, LinkAdditionView.Event> {

    /** @SelfDocumented */
    sealed interface Event {

        /** @SelfDocumented */
        fun toIntent(): LinkAdditionStore.Intent

        /** Событие изменения введенной ссылки в поле ввода. */
        data class InputValueChanged(val inputValue: String) : Event {
            override fun toIntent(): LinkAdditionStore.Intent = LinkAdditionStore.Intent.InputValueChanged(inputValue)
        }

        /** Событие сохранения введенной ссылки. */
        object SaveLink : Event {
            override fun toIntent(): LinkAdditionStore.Intent = LinkAdditionStore.Intent.SaveLink
        }
    }

    /** @SelfDocumented */
    data class Model(
        val inputValue: String = StringUtils.EMPTY
    )

    /** @SelfDocumented */
    fun interface Factory : (View) -> LinkAdditionView
}