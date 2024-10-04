package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoRecipientServiceResult
import ru.tensor.sbis.list.base.data.ResultHelper

/**
 * @author us.bessonov
 */
class DemoRecipientResultHelper : ResultHelper<Int, DemoRecipientServiceResult> {

    override fun hasNext(result: DemoRecipientServiceResult) = result.hasMore

    override fun isEmpty(result: DemoRecipientServiceResult) = result.data.isEmpty()

    override fun getAnchorForNextPage(result: DemoRecipientServiceResult): Int? = null

    override fun getAnchorForPreviousPage(result: DemoRecipientServiceResult): Int? = null
}