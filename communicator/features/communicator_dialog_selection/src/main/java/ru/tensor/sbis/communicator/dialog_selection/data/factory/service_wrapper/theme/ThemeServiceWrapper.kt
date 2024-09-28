package ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.theme

import ru.tensor.sbis.communicator.dialog_selection.data.DialogsFilter

/**
 * Интерфейс обертки контроллера диалогов для экрана выбора диалога/участников
 *
 * @author vv.chekurda
 */
internal interface ThemeServiceWrapper {

    /**
     * Установка колбэка в контроллер диалогов
     * @param callback колбэк с обработкой результа
     */
    fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any?

    /** @SelfDocumented */
    fun list(filter: DialogsFilter): DialogsResult

    /** @SelfDocumented */
    fun refresh(filter: DialogsFilter): DialogsResult
}