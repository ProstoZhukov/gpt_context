package ru.tensor.sbis.communicator.base.conversation.data.model.crud

/**
 * Модель для хранения флагов, необходимых для специфичных реакций
 * при получении результата запроса или при его выполнении.
 *
 * @author vv.chekurda
 */
data class ConversationFilterRules(
    /**
     * Флаг для того, чтобы правильно отреагировать на инициализирующую загрузку данных.
     */
    @Volatile
    var isInitialListLoading: Boolean = true,

    /**
     * Флаг для того, чтобы правильно заполнить фильтр при быстром проскроле вниз.
     */
    @Volatile
    var isFastScrollClicked: Boolean = false,

    /**
     * Флаг для того, чтобы правильно заполнить фильтр при клике на цитату.
     */
    @Volatile
    var isQuoteClicked: Boolean = false,

    /**
     * Флаг для того, чтобы правильно заполнить фильтр при удалении первого сообщения.
     */
    var isFirstMessageSelectedForDeletion: Boolean = false,

    /**
     * Флаг выставится в true, если нужно перерисовать весь список сообщений
     * нужен, чтобы после отрисовки нового списка, проскролиться вниз переписки.
     */
    var isNeedToScrollAfterReloadData: Boolean = false,

    /**
     * Флаг для подскрола к релевантному сообщению по окончанию загрузки списка.
     */
    var isNeedToScrollToRelevant: Boolean = false,

    /**
     * Необходимость проскролиться в конец переписки после обновления новой страницы.
     * Кейс стреляет в прочитанных каналах.
     */
    var isNeedToScrollToBottomAfterNewerUpdating: Boolean = false,

    /**
     * Запрос при появлении интернета.
     */
    var onNetworkConnection: Boolean = false
)