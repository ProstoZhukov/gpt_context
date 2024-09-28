package ru.tensor.sbis.crud.sale.crud.refusal_reason.mapper

import android.content.Context
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.model.RefusalReason
import ru.tensor.sbis.crud.sale.model.map
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonListResult

/**@SelfDocumented */
internal class RefusalReasonListMapper(context: Context) :
        BaseModelMapper<RefusalReasonListResult, PagedListResult<RefusalReason>>(context) {

    override fun apply(rawList: RefusalReasonListResult): PagedListResult<RefusalReason> =
            PagedListResult(rawList.result.map { it.map() }, rawList.hasMore)
}