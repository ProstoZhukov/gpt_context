package ru.tensor.sbis.crud.sale.crud.kkm.mappers

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.model.CashRegister
import ru.tensor.sbis.crud.sale.model.toAndroidType
import ru.tensor.sbis.sale.mobile.generated.KkmModel

/**@SelfDocumented */
internal class KkmMapper(context: Context) : BaseModelMapper<KkmModel, CashRegister>(context) {

    override fun apply(rawData: KkmModel): CashRegister =
            rawData.toAndroidType()
}