package ru.tensor.sbis.appdesign.combined_multiselection.datasource

import ru.tensor.sbis.appdesign.combined_multiselection.data.DemoCombinedRecipientServiceResult
import ru.tensor.sbis.list.base.data.ResultHelper

/**
 * @author ma.kolpakov
 */
class DemoCombinedRecipientHelper : ResultHelper<Int, DemoCombinedRecipientServiceResult> {

    override fun hasNext(result: DemoCombinedRecipientServiceResult) = result.hasMore

    override fun isEmpty(result: DemoCombinedRecipientServiceResult) = result.isEmpty

    override fun getAnchorForNextPage(result: DemoCombinedRecipientServiceResult) = 0

    override fun getAnchorForPreviousPage(result: DemoCombinedRecipientServiceResult) = 0
}
