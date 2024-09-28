package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.set_data

import ru.tensor.sbis.design.retail_models.TaxationSystemBlockViewModel

/** Обобщение API для установки данных в "блок СНО". */
interface TaxationInfoSetDataApi {
    /** Установка данных [taxationSystemInfo] в "блок СНО". */
    fun setTaxSystemInfo(taxationSystemInfo: TaxationSystemBlockViewModel?)
}