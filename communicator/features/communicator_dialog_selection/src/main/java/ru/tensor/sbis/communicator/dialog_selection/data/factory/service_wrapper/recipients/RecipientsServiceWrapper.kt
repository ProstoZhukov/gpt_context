package ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.recipients

import ru.tensor.sbis.communicator.dialog_selection.data.RecipientsFilter

/**
 * Интерфейс обертки контроллера получателей для экрана выбора диалога/участников
 *
 * @author vv.chekurda
 */
internal interface RecipientsServiceWrapper {

    /**
     * Получение списка получателей для нового диалога из кэша с запросом в облако
     * @return источник данных, результат контроллера получателей
     */
    fun list(filter: RecipientsFilter): RecipientsServiceResult

    /**
     * Получение списка получателей для нового диалога из кэша
     * @return источник данных, результат контроллера получателей
     */
    fun refresh(filter: RecipientsFilter): RecipientsServiceResult
}