package ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper

import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionSearchFilter
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionServiceResult
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.recipients.RecipientsServiceWrapperImpl
import ru.tensor.sbis.communicator.dialog_selection.data.factory.service_wrapper.theme.ThemeServiceWrapperImpl
import ru.tensor.sbis.list.base.data.ServiceWrapper
import javax.inject.Inject

/**
 * Реализация обертки сервиса для экрана выбора диалога/участников
 * @property recipientsServiceWrapper обертка контроллера получателей
 * @property themeServiceWrapper      обертка контроллера диалогов
 *
 * @author vv.chekurda
 */
internal class DialogSelectionServiceWrapper @Inject constructor(
    private val recipientsServiceWrapper: RecipientsServiceWrapperImpl,
    private val themeServiceWrapper: ThemeServiceWrapperImpl
) : ServiceWrapper<DialogSelectionServiceResult, DialogSelectionSearchFilter> {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any =
        themeServiceWrapper.setCallbackAndReturnSubscription {
            callback(it)
        }

    override fun list(filter: DialogSelectionSearchFilter): DialogSelectionServiceResult =
        DialogSelectionServiceResult(
            recipientsServiceWrapper.list(filter.recipientsFilter),
            themeServiceWrapper.list(filter.dialogsFilter)
        )

    override fun refresh(
        filter: DialogSelectionSearchFilter,
        params: Map<String, String>
    ): DialogSelectionServiceResult =
        DialogSelectionServiceResult(
            recipientsServiceWrapper.refresh(filter.recipientsFilter),
            themeServiceWrapper.refresh(filter.dialogsFilter)
        )
}