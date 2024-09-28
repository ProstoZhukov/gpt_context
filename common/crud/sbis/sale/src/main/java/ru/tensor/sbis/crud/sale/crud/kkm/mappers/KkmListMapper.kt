package ru.tensor.sbis.crud.sale.crud.kkm.mappers

import android.content.Context
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.model.CashRegister
import ru.tensor.sbis.crud.sale.model.toAndroidType
import ru.tensor.sbis.sale.mobile.generated.KkmListResult

/**@SelfDocumented */
internal class KkmListMapper(context: Context) :
        BaseModelMapper<KkmListResult, PagedListResult<CashRegister>>(context) {

    override fun apply(rawList: KkmListResult): PagedListResult<CashRegister> =
            PagedListResult(rawList.result.map { it.toAndroidType() }, rawList.hasMore)
}