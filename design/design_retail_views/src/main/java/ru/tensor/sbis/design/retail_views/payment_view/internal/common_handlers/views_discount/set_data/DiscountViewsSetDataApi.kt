package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.set_data

import ru.tensor.sbis.design.retail_models.BonusValues
import ru.tensor.sbis.design.retail_views.bonus_button.BonusButtonViewLanguage
import java.math.BigDecimal

/** Обобщение API для установки данных в "блок скидок". */
interface DiscountViewsSetDataApi {

    /** Установка значения скидки. */
    fun setTotalDiscount(totalDiscount: BigDecimal)

    /** Установка значения бонусов. */
    fun setBonuses(bonuses: BonusValues?)

    /** Установка локализации кнопки бонусов. */
    fun setBonusButtonLanguage(bonusButtonLang: BonusButtonViewLanguage)
}