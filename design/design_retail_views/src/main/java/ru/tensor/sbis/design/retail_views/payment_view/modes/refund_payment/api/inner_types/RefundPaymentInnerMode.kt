package ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.inner_types

import ru.tensor.sbis.design.retail_views.R

/**
 * Режимы внутри делегата "возврат".
 * --------------------------------
 * Информация для потомков, будьте внимательны:
 *  - Частичные возвраты: Аванс, Предоплата, В кредит - всегда заблокированный режим кнопки.
 *  - Возврат обычной продажи (обязательно смешанной) - кнопка вообще должна отсутствовать.
 *  - Возврат без чека - доступно переключение "Расчет", "Нефискальный".
 */
enum class RefundPaymentInnerMode(val stringResId: Int = R.string.retail_views_payment_type_refund_text) {

    /** Возврат (режим "Аванс"). */
    REFUND_ADVANCE,

    /** Возврат (режим "В кредит"). */
    REFUND_CREDIT,

    /** Возврат (режим "Предоплата"). */
    REFUND_PRE_PAYMENT,

    /** Возврат (режим "Предоплата") для полной предоплаты. */
    REFUND_FULL_PRE_PAYMENT,

    /** Возврат (режим "Дефолтный"). */
    REFUND(R.string.retail_views_payment_type_payment_text),

    /** Возврат (режим "Нефискальный"). */
    REFUND_NONFISCAL(R.string.retail_views_payment_type_non_fiscal),
}