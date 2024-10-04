package ru.tensor.sbis.design.contact_data_view.api

import ru.tensor.sbis.design.contact_data_view.ClickElementListener
import ru.tensor.sbis.design.contact_data_view.model.SbisContactDataModel

/** @SelfDocumented **/
interface SbisContactDataApi {

    /**
     * Слушатель клика по компоненту
     */
    var onClickElementListener: ClickElementListener?

    /** @SelfDocumented **/
    fun setData(model: SbisContactDataModel)

    /**
     * Количество открываемых элементов при нажатии кнопки "еще"
     */
    var numberOpeningPerStep: Int
}