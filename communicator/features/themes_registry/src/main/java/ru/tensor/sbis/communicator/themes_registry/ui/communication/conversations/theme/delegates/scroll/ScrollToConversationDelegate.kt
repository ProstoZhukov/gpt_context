package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.scroll

import io.reactivex.disposables.Disposable
import ru.tensor.sbis.persons.ConversationRegistryItem

/**
 * Делегат для скролирования на планшете к выделенному элементу списка при отправке сообщения текущим пользователем
 * https://online.sbis.ru/opendoc.html?guid=9667b200-6c0c-4507-9603-dd41f79de61d
 *
 * @author vv.chekurda
 */
internal interface ScrollToConversationDelegate {

    /**
     * Подписаться на подскроллы к переписке
     *
     * @param actions набор управляющих методов для работы делегата
     * @return [Disposable] для отписки от слежения за отправляемыми сообщениями
     */
    fun subscribeOnScrollToConversation(actions: ScrollToConversationActions): Disposable

    /**
     * Уведомление о событии обновления списка.
     * Метод необходимо вызывать при изменении текущего списка диалгов/чатов.
     * Добавление новых страниц можно не учитывать.
     */
    fun onDataListUpdated()
}

/**
 * Набор управляющих методов для делегата скролла к переписке [ScrollToConversationDelegate]
 *
 * @property getDataList      текущий список реестра
 * @property getListOffset    текущий offset пагинации
 * @property getSearchQuery   текущий поисковый запрос
 * @property scrollToTop      скролл к началу переписки со сбросом пагинации
 * @property scrollToPosition подскроллиться к позиции
 */
internal interface ScrollToConversationActions {
    val getDataList: () -> List<ConversationRegistryItem>
    val getListOffset: () -> Int
    val getSearchQuery: () -> String
    val scrollToTop: () -> Unit
    val scrollToPosition: (Int) -> Unit
}