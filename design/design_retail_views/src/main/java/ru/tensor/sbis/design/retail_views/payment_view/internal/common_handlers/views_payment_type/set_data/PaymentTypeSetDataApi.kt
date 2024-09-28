package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.retail_views.popup_menu.PopupMenuConfiguration

/** Обобщение API для установки данных в блок "тип оплаты". */
interface PaymentTypeSetDataApi {

    /** API для установки индивидуальных типов оплаты. */
    interface TypeChanger<PAYMENT_INNER_MODE> {
        /** Установка типа оплаты [PAYMENT_INNER_MODE]. */
        fun setPaymentType(paymentInnerMode: PAYMENT_INNER_MODE)
    }

    /** Конфигурирование выпадающего списка меню с типами оплаты. */
    fun configurePaymentTypeMenu(
        fragmentManager: FragmentManager,
        configuration: PopupMenuConfiguration
    )
}