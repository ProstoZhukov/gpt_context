package ru.tensor.sbis.design.retail_views.payment_view.internal.delegates

import android.widget.EditText
import ru.tensor.sbis.design.retail_models.Amount
import ru.tensor.sbis.design.retail_models.PaymentMethod
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import java.math.BigDecimal

internal typealias AmountChangeListener = (currentAmount: Amount) -> Unit

/**
 * Описание объекта, отвечающего за предоставление доступа к банкнотам.
 *
 * Внимание: [Internal Api], можно использовать только внутри делегатов оплаты.
 */
internal interface BanknotesDelegateApi {

    /** Объект предоставляющий доступ к API [BanknotesDelegateApi.Handler]. */
    val banknotesApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [BanknotesDelegateApi]. */
    interface Handler {

        /** Все внесенные пользователем денежные средства [Amount], разбитые по типам. */
        val allAmount: Amount

        /** Получить текущее значение 'Итоговой суммы' к оплате/возврату/авансу. */
        var totalCheckAmount: BigDecimal

        /**
         * Поле ввода в которое автоматически установится фокус, если пользователь
         * взаимодействует с блоком банкнот, а поля в фокусе отсутствуют.
         */
        var inputViewForAutoFocus: EditText?

        /** Метод для инициализации [BanknotesDelegate]. */
        fun initialize(keyboardHelper: CustomNumericKeyboardHelper)

        /**
         * Автоматически рассчитать и заполнить недостающее значение в указанное поле ввода [inputField].
         * Также, если поле имеет привязку к какому-нибудь типу из [Amount], например [PaymentMethod.CARD],
         * то это значение также будет заполнено.
         *
         * -----------------------
         * Задача: https://online.sbis.ru/opendoc.html?guid=509c3d19-1280-4c17-acc1-6ac589e09c86&client=3
         * Пример:
         *  * [totalCheckAmount] = 100.
         *  * [Amount.cash] = 30.
         *  - пользователь включает режим "смешанной оплаты".
         *  * Вызываем [autoSetupRemainingAmountForInputField] для поля с типом [PaymentMethod.CARD].
         *
         *  Итог: в указанное поле [inputField], а также в [Amount.card] будет записано
         *  значение [totalCheckAmount] - [Amount.total].
         * -----------------------
         *
         *  p.s. Если [Amount.total] > [totalCheckAmount], то заполнение указанного
         *  поля [inputField] и модифицирование [Amount] будет проигнорировано.
         */
        fun autoSetupRemainingAmountForInputField(inputField: EditText)

        /** Подписка [subscriber] на событие изменения кол-ва внесенных средств [Amount]. */
        fun subscribeToAmountChangedAction(subscriber: AmountChangeListener)

        /** Оповещение всех [AmountChangeListener] об изменении [Amount] кол-ва внесенных средств. */
        fun notifyAllSubscribersAmountChangeTo(newAmountValue: Amount)
    }
}