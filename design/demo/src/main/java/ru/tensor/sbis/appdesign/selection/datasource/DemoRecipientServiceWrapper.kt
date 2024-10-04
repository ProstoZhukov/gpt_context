package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientServiceResult
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
class DemoRecipientServiceWrapper(
    private val controller: DemoRecipientController
) : ServiceWrapper<DemoRecipientServiceResult, DemoRecipientFilter> {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any? = null

    override fun list(filter: DemoRecipientFilter): DemoRecipientServiceResult = controller.list(filter)

    override fun refresh(filter: DemoRecipientFilter, params: Map<String, String>): DemoRecipientServiceResult =
        controller.refresh(filter)
}