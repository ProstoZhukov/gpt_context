package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import ru.tensor.sbis.design.retail_models.utils.isLessZero
import ru.tensor.sbis.design.retail_models.utils.isMoreZero
import ru.tensor.sbis.design.retail_models.utils.isZero
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.PaymentUtils.calculateChangeValue
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi.CashInputMode
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi.CashInputMode.Change
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi.CashInputMode.DisableAll
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi.CashInputMode.More
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi.CashInputMode.NoChange
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi.CashInputMode.OverPayment
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.AmountChangeListener
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import java.math.BigDecimal
import ru.tensor.sbis.design.R as RDesign

/** Реализация объекта для установки данных в элементы управления "Ввод денежных средств". */
internal class CashInputSetDataHandler(
    private val banknotesApi: BanknotesDelegateApi.Handler,
    private val safetyApi: CashInputAccessSafetyApi,
    private val viewAccessApi: CashInputAccessDangerousApi
) : CashInputSetDataApi {

    private val context: Context
        get() = viewAccessApi.paymentChangeLabel.context

    override val amountChangeListener: AmountChangeListener = { amount ->
        if (banknotesApi.totalCheckAmount.isZero()) {
            /* Если к оплате "0.00", то UI 'Сдача' - должен быть скрыт. */
            changeCashInputModeTo(DisableAll)
        } else {
            amount.calculateChangeValue(banknotesApi.totalCheckAmount).let { changeValue ->
                val moneyValue = changeValue.abs()

                // Сдача может быть выдана только с нала, но если сумма по безналичным типам была больше суммы оплаты,
                // то сдачу мы выдать не можем и пишем "Переплата".
                val isOverPaid = amount.card >= banknotesApi.totalCheckAmount

                changeCashInputModeTo(
                    mode = when {
                        amount.isZero -> DisableAll
                        changeValue.isMoreZero() && isOverPaid -> OverPayment(moneyValue)
                        changeValue.isMoreZero() -> Change(moneyValue)
                        changeValue.isLessZero() -> More(moneyValue)
                        else -> NoChange(moneyValue)
                    }
                )
            }
        }
    }

    override fun dropCashInputValue(disableInputField: Boolean) {
        viewAccessApi.moneyInputField.setText("")

        /* Отключаем поле "Наличные", при необходимости. */
        if (disableInputField) {
            safetyApi.setMoneyInputEnabled(isEnabled = false)
        }
    }

    override fun changeCashInputModeTo(mode: CashInputMode) {
        /* Настраиваем визуальную часть View элементов, перед отображением. */
        handleChangeCashInputModeTo(mode)

        /* Устанавливаем значение в поле "Сдача/Ещё". */
        viewAccessApi.paymentChangeValue.showMoney(mode.moneyValue)
    }

    override fun setTotalCheckValue(totalCheckValue: BigDecimal) {
        /* Запоминаем новое значение для 'Суммы к оплате'. */
        banknotesApi.totalCheckAmount = totalCheckValue

        /* Уведомляем подписчика об изменении 'Итоговой суммы' к оплате/возврату/авансу. */
        amountChangeListener.invoke(banknotesApi.allAmount)
    }

    private fun handleChangeCashInputModeTo(mode: CashInputMode) {
        when (mode) {
            /* 'Блок 'Сдача/Ещё' выключен. */
            DisableAll -> {
                updateInfoVisibilityTo(
                    isLabelVisible = false,
                    isValueVisible = false
                )
            }

            /* 'Ещё'. */
            is More -> {
                updateChangeValue(numberColorRes = RDesign.attr.dangerTextColor)
                updateChangeLabel(
                    titleRes = R.string.retail_views_payment_shortfall_text,
                    titleColor = StyleColor.DANGER.getTextColor(context)
                )

                updateInfoVisibilityTo(
                    isLabelVisible = true,
                    isValueVisible = true
                )
            }

            /* 'Сдача'. */
            is Change -> {
                updateChangeValue(numberColorRes = RDesign.attr.successTextColor)
                updateChangeLabel(
                    titleRes = R.string.retail_views_payment_change_text,
                    titleColor = StyleColor.SUCCESS.getTextColor(context)
                )

                updateInfoVisibilityTo(
                    isLabelVisible = true,
                    isValueVisible = true
                )
            }

            /* Переплата */
            is OverPayment -> {
                updateChangeValue(numberColorRes = RDesign.attr.successTextColor)
                updateChangeLabel(
                    titleRes = R.string.retail_views_payment_overpayment_text,
                    titleColor = StyleColor.SUCCESS.getTextColor(context)
                )

                updateInfoVisibilityTo(
                    isLabelVisible = true,
                    isValueVisible = true
                )
            }

            /* 'Без сдачи'. */
            is NoChange -> {
                updateChangeLabel(
                    titleRes = R.string.retail_views_payment_no_change_text,
                    titleColor = TextColor.LABEL.getValue(context)
                )

                updateInfoVisibilityTo(
                    isLabelVisible = true,
                    isValueVisible = false
                )
            }
        }
    }

    private fun updateChangeValue(@AttrRes numberColorRes: Int) {
        viewAccessApi.paymentChangeValue.setColorNumberAttr(numberColorRes)
    }

    private fun updateChangeLabel(@StringRes titleRes: Int, @ColorInt titleColor: Int) {
        viewAccessApi.paymentChangeLabel.setText(titleRes)
        viewAccessApi.paymentChangeLabel.setTextColor(titleColor)
    }

    private fun updateInfoVisibilityTo(isLabelVisible: Boolean, isValueVisible: Boolean) {
        safetyApi.setChangeLabelVisibility(isLabelVisible)
        safetyApi.setChangeValueVisibility(isValueVisible)
    }
}