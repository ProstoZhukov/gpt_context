package ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog

import ru.tensor.sbis.appdesign.combined_multiselection.data.dialog.DemoDialogServiceResult
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
class DemoDialogServiceWrapper(private val controller: DemoDialogController) :
    ServiceWrapper<DemoDialogServiceResult, DemoRecipientFilter> {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any? = null

    override fun list(filter: DemoRecipientFilter) = controller.list(filter)

    override fun refresh(filter: DemoRecipientFilter, params: Map<String, String>): DemoDialogServiceResult =
        controller.refresh(filter)
}
