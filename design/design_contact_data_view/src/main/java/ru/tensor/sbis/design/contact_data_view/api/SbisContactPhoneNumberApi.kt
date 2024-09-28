package ru.tensor.sbis.design.contact_data_view.api

import ru.tensor.sbis.design.contact_data_view.ClickElementListener
import ru.tensor.sbis.design.contact_data_view.model.SbisContactPhoneNumberModel

/** @SelfDocumented **/
interface SbisContactPhoneNumberApi {

    /**
     * Слушатель клика по компоненту
     */
    var clickListener: ClickElementListener?

    /** @SelfDocumented **/
    fun setData(data: SbisContactPhoneNumberModel)
}