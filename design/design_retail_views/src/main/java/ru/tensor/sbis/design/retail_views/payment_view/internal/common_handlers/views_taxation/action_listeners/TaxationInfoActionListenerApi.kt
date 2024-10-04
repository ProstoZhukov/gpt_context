package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.action_listeners

import ru.tensor.sbis.design.retail_models.UiTaxSystemCode

/** Обобщение Api для обработки действий пользователя с View элементами - "блок СНО". */
interface TaxationInfoActionListenerApi {

    /** Установка/получение слушателя общего действия при нажатии на кнопки СНО. (Поддерживается для "ООО, ИП"). */
    var onTaxationSystemClickAction: ((taxSystemCode: UiTaxSystemCode) -> Unit)?

    /** Установить действие [action] по нажатию на кнопку "название ООО". */
    fun setPrimaryTaxationSystemClickListener(action: () -> Unit)

    /** Установить действие [action] по нажатию на кнопку "название ИП". */
    fun setPatentTaxationSystemClickListener(action: () -> Unit)
}