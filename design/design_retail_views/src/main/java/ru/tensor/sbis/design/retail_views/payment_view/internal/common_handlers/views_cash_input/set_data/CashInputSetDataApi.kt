package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data

import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.AmountChangeListener
import java.math.BigDecimal

/** Обобщение API для установки данных в блок "Ввод денежных средств". */
interface CashInputSetDataApi {

    /** Получить слушатель изменения кол-ва внесенных средств. */
    val amountChangeListener: AmountChangeListener

    /**
     * Режимы блока "Сдача/Ещё".
     *
     * @param moneyValue кол-во внесенных денежных средств.
     */
    sealed class CashInputMode(val moneyValue: BigDecimal?) {

        /** 'Отключить блок 'Сдача/Ещё'. (Элементы интерфейса будут скрыты) */
        object DisableAll : CashInputMode(null)

        /** 'Сдача'. */
        class Change(moneyValue: BigDecimal?) : CashInputMode(moneyValue)

        /** 'Переплата'. */
        class OverPayment(moneyValue: BigDecimal?) : CashInputMode(moneyValue)

        /** 'Ещё'. */
        class More(moneyValue: BigDecimal?) : CashInputMode(moneyValue)

        /** 'Без сдачи'. */
        class NoChange(moneyValue: BigDecimal?) : CashInputMode(moneyValue)
    }

    /**
     * Сбросить введенное значение в поле "Наличные".
     *
     * Если был передан [disableInputField] == true,
     * то поле будет автоматически заблокировано для дальнейшего ввода.
     */
    fun dropCashInputValue(disableInputField: Boolean = false)

    /** Переключить режим блока "Сдача/Ещё". */
    fun changeCashInputModeTo(mode: CashInputMode)

    /** Установка суммы [totalCheckValue], которую нужно оплатить/внести/вернуть. */
    fun setTotalCheckValue(totalCheckValue: BigDecimal)
}