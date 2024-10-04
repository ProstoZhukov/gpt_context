package ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact

import ru.tensor.sbis.appdesign.combined_multiselection.data.contact.DemoContactServiceResult
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
class DemoContactServiceWrapper(private val controller: DemoContactController) :
    ServiceWrapper<DemoContactServiceResult, DemoRecipientFilter> {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any? = null

    override fun list(filter: DemoRecipientFilter) = controller.list(filter)

    override fun refresh(filter: DemoRecipientFilter, params: Map<String, String>): DemoContactServiceResult =
        controller.refresh(filter)
}
