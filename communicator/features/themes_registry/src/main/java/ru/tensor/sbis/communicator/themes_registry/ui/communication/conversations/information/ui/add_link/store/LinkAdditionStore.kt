package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore.Label
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore.State

/**
 * Описывает действия ([Intent]), состояния ([State]) и сайд-эффекты ([Label]) для экрана добавления ссылки.
 *
 * @author dv.baranov
 */
internal interface LinkAdditionStore :
    Store<Intent, State, Label> {

    /** @SelfDocumented */
    sealed interface Intent {

        /** Изменение значения в поле ввода. */
        data class InputValueChanged(val inputValue: String) : Intent

        /** Сохранение введенной ссылки. */
        object SaveLink : Intent
    }

    /** @SelfDocumented */
    sealed interface Label {

        /** Закрытие экрана добавления ссылки. */
        object Close : Label
    }

    /** @SelfDocumented */
    @Parcelize
    data class State(
        val inputValue: String = StringUtils.EMPTY
    ) : Parcelable
}