package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoFilter
import ru.tensor.sbis.appdesign.selection.data.DemoServiceResult
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
class DemoServiceWrapper(
    private val controller: DemoRegionController
) : ServiceWrapper<DemoServiceResult, DemoFilter> {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any? = null

    override fun list(filter: DemoFilter): DemoServiceResult = controller.list(filter)

    override fun refresh(filter: DemoFilter, params: Map<String, String>): DemoServiceResult =
        controller.refresh(filter)
}