package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.messages

import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage

/**
 * Вспомогательная реализация реестра сообщений по отображающимся сообщениям.
 *
 * @author vv.chekurda
 */
internal class VisibleMessagesHelper<MESSAGE : BaseConversationMessage>(
    private var dataList: List<MESSAGE> = emptyList()
) {
    /**
     * Индекс первого видимыого элемента (самый нижний).
     */
    var firstVisibleItem: Int = 0
        private set
    /**
     * Индекс последнего видимыого элемента (самый верний).
     */
    var lastVisibleItem: Int = 0
        private set

    /**
     * Признак видимости самого первого (нижнего элемента в списке).
     * Не учитывает пагинацию, т.е. фактически нижний элемент в текущем списке.
     */
    var isFirstItemShown: Boolean? = null
        private set
    /**
     * Признак отображения самого последнего (нового) сообщения на его нижней границе.
     */
    var atBottomOfList: Boolean = false
        private set

    /**
     * Признак видимости всех элементов списка на экране.
     */
    val isAllListVisible: Boolean
        get() = firstVisibleItem == 0 && lastVisibleItem < dataList.size

    /**
     * Список поменялся.
     */
    fun onDataListChanged(dataList: List<MESSAGE>) {
        this.dataList = dataList
    }

    /**
     * Проверить видим ли хотя бы один элемент из списка индексов [indexes].
     */
    fun isAnyVisible(indexes: List<Long>): Boolean {
        val visibleItems = firstVisibleItem..lastVisibleItem
        return indexes.any { it in visibleItems }
    }

    /**
     * Обработать изменение видимых элементов на экране.
     */
    fun onVisibleItemsChanged(first: Int, last: Int) {
        firstVisibleItem = first
        lastVisibleItem = last
    }

    /**
     * Обработать изменение видимости нижнего элемента на экране.
     */
    fun onFirstItemShownStateChanged(isFirstItemShown: Boolean, atBottomOfList: Boolean) {
        this.isFirstItemShown = isFirstItemShown
        this.atBottomOfList = atBottomOfList
    }

    /**
     * Сбросить состояние "нахождения в самом конце переписки".
     */
    fun dropAtBottomState() {
        isFirstItemShown = false
        atBottomOfList = false
    }
}