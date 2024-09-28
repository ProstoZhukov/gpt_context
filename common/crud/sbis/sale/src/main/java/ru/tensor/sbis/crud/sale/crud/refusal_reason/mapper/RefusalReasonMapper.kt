package ru.tensor.sbis.crud.sale.crud.refusal_reason.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.model.RefusalReason
import ru.tensor.sbis.crud.sale.model.map
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonModel as ControllerRefund

/**@SelfDocumented */
internal class RefusalReasonMapper(context: Context) :
        BaseModelMapper<ControllerRefund, RefusalReason>(context) {

    override fun apply(rawData: ControllerRefund): RefusalReason =
            rawData.map()
}
