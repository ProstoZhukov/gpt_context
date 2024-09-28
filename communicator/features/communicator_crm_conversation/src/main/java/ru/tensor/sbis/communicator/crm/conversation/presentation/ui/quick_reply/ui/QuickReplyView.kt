package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui

import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.view.MviView
import org.apache.commons.lang3.StringUtils
import java.util.UUID

/**
 * Контракт view компонента MVI списка быстрых ответов.
 *
 * @author dv.baranov
 */
internal interface QuickReplyView : MviView<QuickReplyView.Model, QuickReplyView.Event> {

    /** @SelfDocumented */
    sealed interface Event {

        /** Пользователь ввел запрос в строку поиска. */
        data class EnterSearchQuery(val query: String) : Event

        /** Пользователь сменил папку. */
        data class FolderChanged(
            val folderTitle: String,
            val uuid: UUID?,
        ) : Event

        /** Пользователь нажал на кнопку быстрого ответа. */
        data class OnQuickReplyItemClicked(
            val uuid: UUID,
            val replyText: String,
            val isPinned: Boolean,
        ) : Event

        /** Пользователь нажал на кнопку открепить/закрепить быстрый ответ */
        data class OnPinClick(val uuid: UUID, val isPinned: Boolean) : Event

        /** Количество элементов списка поменялось. */
        data class OnListElementsCountChanged(val count: Int) : Event
    }

    /** @SelfDocumented */
    data class Model(
        val searchText: String = StringUtils.EMPTY,
        val folderTitle: String = StringUtils.EMPTY,
    )

    /**
     * Сделать поисковый запрос по быстрым ответам.
     */
    fun setSearchQuery(query: String)

    /**
     * Обработать изменение высоты панели, содержащей фрагмент быстрых ответов.
     */
    fun handleHeightChanges(heightEqualZero: Boolean)

    /**
     * Установить обработчик скролла списка быстрых ответов.
     */
    fun setScrollListener(listener: RecyclerView.OnScrollListener?)
}
