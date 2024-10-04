package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.set_data

import android.content.res.ColorStateList
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.retail_models.BonusValues
import ru.tensor.sbis.design.retail_models.utils.isMoreZero
import ru.tensor.sbis.design.retail_views.bonus_button.BonusButtonViewLanguage
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_dangerous.DiscountViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_safety.DiscountViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.utils.intAmountFormat
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import java.math.BigDecimal

/** Реализация объекта для установки данных в элементы управления "блок скидок". */
internal class DiscountViewsSetDataHandler(
    private val safetyApi: DiscountViewsAccessSafetyApi,
    private val viewAccessApi: DiscountViewsAccessDangerousApi
) : DiscountViewsSetDataApi {

    override fun setTotalDiscount(totalDiscount: BigDecimal) {
        val discountText = intAmountFormat.format(totalDiscount)

        viewAccessApi.discountButton.apply {
            val iconColorState = ColorStateList.valueOf(StyleColor.BONUS.getIconColor(context))
            val textColorState = ColorStateList.valueOf(StyleColor.BONUS.getTextColor(context))

            model = SbisButtonModel(
                icon = SbisButtonTextIcon(
                    icon = SbisMobileIcon.Icon.smi_discount.character.toString(),
                    size = SbisButtonIconSize.XL,
                    style = SbisButtonIconStyle(iconColorState)
                ),
                title = if (!totalDiscount.isMoreZero()) null
                else {
                    SbisButtonTitle(
                        text = discountText,
                        style = SbisButtonTitleStyle.create(textColorState, textColorState, textColorState)
                    )
                }
            )
        }
    }

    override fun setBonuses(bonuses: BonusValues?) {
        (bonuses != null).let { availableBonuses ->
            safetyApi.setBonusButtonVisibility(availableBonuses)

            if (availableBonuses) {
                viewAccessApi.bonusButton.setBonuses(bonuses!!)
            }
        }
    }

    override fun setBonusButtonLanguage(bonusButtonLang: BonusButtonViewLanguage) {
        viewAccessApi.bonusButton.setLanguage(bonusButtonLang)
    }
}