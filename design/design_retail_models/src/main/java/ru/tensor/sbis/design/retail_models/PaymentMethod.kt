package ru.tensor.sbis.design.retail_models

import androidx.annotation.StringRes
import ru.tensor.sbis.design.R as RDesign

/**
 * Способ оплаты
 * @param iconId /**@SelfDocumented */
 */
enum class PaymentMethod constructor(@StringRes val iconId: Int) {
    CASH(R.string.retail_models_icon_cash_payment),
    CARD(R.string.retail_models_icon_card_payment),
    UNDER_SALARY(R.string.retail_models_icon_salary_payment),
    MIXED(RDesign.string.design_mobile_icon_mix_payment),
    QR_CODE(R.string.retail_models_mobile_icon_qr_code),
    INTERNET(R.string.retail_models_mobile_icon_internet),
    NON_FISCAL(R.string.retail_models_icon_non_fiscal_payment)
}