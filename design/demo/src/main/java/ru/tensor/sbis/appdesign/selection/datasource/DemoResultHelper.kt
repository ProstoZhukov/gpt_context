package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoServiceResult
import ru.tensor.sbis.list.base.data.ResultHelper

/**
 * @author us.bessonov
 */
class DemoResultHelper : ResultHelper<Int, DemoServiceResult> {

    override fun hasNext(result: DemoServiceResult) = result.hasMore

    override fun isEmpty(result: DemoServiceResult) = result.data.isEmpty()

    override fun getAnchorForNextPage(result: DemoServiceResult): Int? = null

    override fun getAnchorForPreviousPage(result: DemoServiceResult): Int? = null
}