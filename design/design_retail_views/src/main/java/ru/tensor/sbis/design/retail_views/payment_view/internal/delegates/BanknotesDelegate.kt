package ru.tensor.sbis.design.retail_views.payment_view.internal.delegates

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import androidx.core.view.isGone
import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_models.Amount
import ru.tensor.sbis.design.retail_models.PaymentMethod
import ru.tensor.sbis.design.retail_models.utils.PRICE_SCALE
import ru.tensor.sbis.design.retail_models.utils.ZERO_MONEY_VALUE
import ru.tensor.sbis.design.retail_models.utils.isMoreZero
import ru.tensor.sbis.design.retail_models.utils.roundHalfUp
import ru.tensor.sbis.design.retail_views.banknote.BanknoteView
import ru.tensor.sbis.design.retail_views.databinding.BanknotesCustomizedBinding
import ru.tensor.sbis.design.retail_views.numberic_keyboard.NumericKeyboard
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper.FieldType
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.PaymentUtils.convertUserInputToBigDecimalOrNull
import ru.tensor.sbis.design.retail_views.utils.amountFormat
import ru.tensor.sbis.design.retail_views.utils.isOutOfHierarchyBoundsHorizontally
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.utils.extentions.doOnNextGlobalLayout
import java.math.BigDecimal
import kotlin.properties.Delegates

/** Внутренний делегат инкапсулирующий логику работы с блоком банкнот. */
@SuppressLint("UnsafeOptInUsageWarning")
internal class BanknotesDelegate private constructor(
    private val context: Context,
    private val targetAncestor: View,
    private val keyboardView: NumericKeyboard,
    private val banknotesViewBinding: BanknotesCustomizedBinding,
    private val fieldsForBanknotesInput: List<InputViewWrapper>
) : BanknotesDelegateApi {

    /**
     * Класс-обертка над полем ввода [inputField], с указанием типа пополняемых средств
     * [paymentMethod] ('null', если поле ввода не должно изменять кол-во внесенных средств)
     * и типа ввода для самого поля [fieldInputType].
     *
     * На практике, [fieldInputType] необходимо изменить только для кнопки с бонусами.
     * Для нее используйте: [FieldType.NUMBER_GROUPING].
     *
     * Выбранный режим отвечает за визуальную работу BanknoteView + за корректность
     * заполнения модели [Amount].
     *
     * Дополнительно [#1], для поля можно настроить динамическую размерность передав [autoSizeConfig].
     * Дополнительно [#2], для поля можно настроить автоматическое переопределение Hint'a [needOverrideInputFieldHint],
     * т.е. вместо вашего Hint'a, будет сразу подставлено "0.00". Это дефолтное поведение, которое можно отключить.
     */
    data class InputViewWrapper(
        val inputField: EditText,
        val paymentMethod: PaymentMethod? = null,
        val fieldInputType: FieldType = FieldType.MONEY,
        val autoSizeConfig: AutoSizeConfig? = null,
        val needOverrideInputFieldHint: Boolean = true
    ) {

        /**
         * Класс-конфигурация, для настройки динамической размерности текста в поле ввода.
         *
         * @param autoSizeDefaultTextSize дефолтный размер текста в поле ввода.
         * @param autoSizeMinTextSize минимальный размер текста в поле ввода.
         */
        data class AutoSizeConfig(
            private val autoSizeDefaultTextSize: FontSize = FontSize.X3L,
            private val autoSizeMinTextSize: FontSize = FontSize.X3S
        ) {
            /** Доступный диапазон изменения размера шрифта в поле ввода. */
            val autoTextSizeRangeIterable: List<FontSize> by lazy {
                val autoTextSizeRange = autoSizeMinTextSize..autoSizeDefaultTextSize

                /* ВАЖНО: от большего к меньшему! */
                FontSize.values()
                    .filter { fontSize -> autoTextSizeRange.contains(fontSize) }
                    .reversed()
            }
        }
    }

    companion object {
        /* Используется для случаев, когда [Amount] изменяется путем нажатия на банкноты. */
        private const val AMOUNT_CHANGED_BY_BANKNOTES_TAG = "AMOUNT_CHANGED_BY_BANKNOTES_TAG"

        /* Используется для случаев, когда [Amount] изменяется расчетов 'totalCheckAmount' - 'allAmount.total'. */
        private const val AMOUNT_CHANGED_BY_DIFF_TAG = "AMOUNT_CHANGED_BY_DIFF_TAG"

        /**
         * Метод для получения объекта [BanknotesDelegateApi].
         *
         * @param context [Context].
         * @param targetAncestor view-контейнер, относительно которого будет выполняться проверка
         * "поместился блок банкнот или нет", если нет, блок банкнот автоматически сожмется и проверка
         * будет выполнена повторно. В случае, если сжиматься уже некуда, блок банкнот будет скрыт.
         * @param keyboardView view отвечающая за работу клавиатуры [NumericKeyboard].
         * @param banknotesViewBinding объект для доступа к разметке блока банкнот [BanknotesCustomizedBinding].
         * @param fieldsForBanknotesInput поля поддерживающие ввод при помощи банкнот.
         */
        fun defaultCreator(
            context: Context,
            targetAncestor: View,
            keyboardView: NumericKeyboard,
            banknotesViewBinding: BanknotesCustomizedBinding,
            fieldsForBanknotesInput: List<InputViewWrapper>
        ): BanknotesDelegateApi = BanknotesDelegate(
            context = context,
            targetAncestor = targetAncestor,
            keyboardView = keyboardView,
            banknotesViewBinding = banknotesViewBinding,
            fieldsForBanknotesInput = fieldsForBanknotesInput
        )
    }

    override val banknotesApi: BanknotesDelegateApi.Handler by lazy {
        object : BanknotesDelegateApi.Handler {
            private val amountSubscribers by lazy {
                mutableListOf<AmountChangeListener>()
            }

            override val allAmount: Amount = Amount()

            /* 'var' - специализированное решение, для возможности подмены 'Итоговой суммы'. */
            override var totalCheckAmount: BigDecimal by Delegates.notNull()

            override var inputViewForAutoFocus: EditText? = null

            override fun initialize(keyboardHelper: CustomNumericKeyboardHelper) {
                /* Настройка ширины и наличия кнопок банкнот. */
                resizeKeyboardAndBanknotesIfNeeded()

                /* Устанавливаем слушатели на кнопки банкнот. */
                initBanknotesListeners()

                /* Вешаем слушатели на указанные поля ввода. */
                initManualInputListeners(keyboardHelper)

                /* Устанавливаем слушатели на кнопки клавиатуры ручного ввода. */
                initKeyboardView(keyboardHelper)

                /* Настраиваем работу нашей клавиатуры для переданных извне полей ввода. */
                keyboardHelper.setupForViews(*fieldsForBanknotesInput.toTypedArray())
            }

            @SuppressLint("SetTextI18n")
            override fun autoSetupRemainingAmountForInputField(inputField: EditText) {
                /* Если поле "Наличные" не заполнено, то работа автозаполнения - не требуется. */
                if (allAmount.cash.isMoreZero()) {
                    val amountDiff = totalCheckAmount - allAmount.total
                    if (amountDiff.isMoreZero()) {
                        /*
                         * Устанавливаем текст.
                         *
                         * Если на поле ввода был установлен [AmountInputViewTextWatcher],
                         * то значение отформатируется автоматически, иначе - просто установится
                         * неформатированный текст.
                         */
                        inputField.setInputFieldTextProgrammatically(
                            inputTag = AMOUNT_CHANGED_BY_DIFF_TAG,
                            inputText = amountDiff.toString()
                        )
                    }
                }
            }

            override fun subscribeToAmountChangedAction(subscriber: AmountChangeListener) {
                amountSubscribers.add(subscriber)
            }

            override fun notifyAllSubscribersAmountChangeTo(newAmountValue: Amount) {
                amountSubscribers.forEach { subscriber -> subscriber.invoke(newAmountValue) }
            }
        }
    }

    private val inputAmounts = mutableMapOf<InputViewWrapper, BigDecimal>()

    /* Флаг показывает, был ли крайним ввод при помощи банкнот. */
    private var lastInputWasBanknotes = false

    /* Контейнер, для замеров ширины текста. */
    private val inputTextSizeBox = Rect()

    /* Получение текущего видимого поля в фокусе. */
    private val currentInputViewWrapperInFocus: InputViewWrapper?
        get() = fieldsForBanknotesInput.firstOrNull {
            it.inputField.isFocused && it.inputField.isVisible && it.inputField.isEnabled
        }

    private val banknotes: List<BanknoteView> by lazy {
        with(banknotesViewBinding) {
            listOf(
                /* [1 сболбец / 1 строка]. */
                retailViewsBanknote11,
                /* [1 сболбец / 2 строка]. */
                retailViewsBanknote12,
                /* [1 сболбец / 3 строка]. */
                retailViewsBanknote13,
                /* [2 сболбец / 1 строка]. */
                retailViewsBanknote21,
                /* [2 сболбец / 2 строка]. */
                retailViewsBanknote22,
                /* [2 сболбец / 3 строка]. */
                retailViewsBanknote23
            )
        }
    }

    private fun initBanknotesListeners() {
        banknotes.forEach { banknote ->
            banknote.setOnBanknoteClickListener { banknoteView, banknoteValue ->
                onBanknoteInputHandler(banknoteView, banknoteValue, isIncreaseCounter = true)
            }

            banknote.setOnCounterClickListener { banknoteView, banknoteValue ->
                onBanknoteInputHandler(banknoteView, banknoteValue, isIncreaseCounter = false)
            }
        }
    }

    private fun initManualInputListeners(keyboardHelper: CustomNumericKeyboardHelper) {
        fieldsForBanknotesInput.forEach { fieldWrapper ->
            fieldWrapper.inputField.addTextChangedListener(AmountInputViewTextWatcher(fieldWrapper, keyboardHelper))
        }
    }

    private fun initKeyboardView(keyboardHelper: CustomNumericKeyboardHelper) {
        keyboardHelper.setKeyboard(keyboardView)
        keyboardHelper.resetClickedAction = { resetBanknotes() }
    }

    private fun resetBanknotes() {
        banknotes.forEach { banknote -> banknote.resetCounter() }
    }

    private fun onBanknoteInputHandler(banknoteView: BanknoteView, banknoteValue: Int, isIncreaseCounter: Boolean) {
        currentInputViewWrapperInFocus?.let { (inputView, paymentMethod) ->
            /* Добавляем поддержку способов оплаты по мере необходимости. */
            when (paymentMethod) {
                /*
                    Поле ввода принимает расчет "Наличными"/"По карте" +
                    поддерживает ввод для полей, которые не отвечают за
                    пополнение денежных средств.
                 */
                PaymentMethod.CASH, PaymentMethod.CARD, null -> {
                    BigDecimal(banknoteValue).roundHalfUp(PRICE_SCALE).let { banknoteInputValue ->
                        inputView.updateMoneyOnManual(
                            paymentMethod = paymentMethod,
                            isBanknotesInput = true,
                            inputValue = if (isIncreaseCounter) banknoteInputValue else -banknoteInputValue
                        )
                    }

                    /* Если работаем с полем ввода "Налички". */
                    if (paymentMethod == PaymentMethod.CASH) {
                        if (isIncreaseCounter) {
                            /* Увеличиваем счетчик купюр, если работаем с наличкой. */
                            banknoteView.increaseCounter()
                        } else {
                            /* Уменьшаем счетчик купюр, если работаем с наличкой. */
                            banknoteView.decreaseCounter()
                        }
                    }
                }

                /* Данный тип не поддерживается, кидаем ошибку. */
                else -> throw getUnsupportedMethodException(paymentMethod)
            }
        } ?: kotlin.run {
            banknotesApi.inputViewForAutoFocus?.let { inputView ->
                /* Если элемент выключен, то запрещаем установку фокуса. */
                if (inputView.isEnabled) {
                    /*
                        Если нет никакого поля в фокусе, то по-умолчанию устанавливаем
                        фокус во View, которая необходима делегату извне.
                    */
                    if (!inputView.isFocused) inputView.requestFocus()

                    onBanknoteInputHandler(banknoteView, banknoteValue, isIncreaseCounter)
                }
            }
        }
    }

    /**
     * При необходимости спрятать банкноты.
     *
     * Ожидается, что этот метод будет вызываться только в портретной ориентации,
     * т.к он обращается к View которые присутствуют только в портрете.
     */
    private fun resizeKeyboardAndBanknotesIfNeeded() {
        banknotesViewBinding.root.doOnNextGlobalLayout(
            skipWhile = { banknotesViewBinding.root.width == 0 },
            action = {
                /*
                    Подгоняем ширину [BanknoteView] под самую широкую в столбце.
                    Актуально для региональных валют.
                 */
                with(banknotesViewBinding) {
                    /* 1 столбец. */
                    setupMaxWidthForBanknoteViewsIfNeeded(
                        listOf(
                            retailViewsBanknote11,
                            retailViewsBanknote12,
                            retailViewsBanknote13
                        )
                    )

                    /* 2 столбец. */
                    setupMaxWidthForBanknoteViewsIfNeeded(
                        listOf(
                            retailViewsBanknote21,
                            retailViewsBanknote22,
                            retailViewsBanknote23
                        )
                    )
                }

                /* Для портретной ориентации доступен режим скрытия части блока с банкнотами. */
                if (isPortraitOrientation(context)) {
                    with(banknotesViewBinding) {
                        val isFirstBanknotesColumnVisible =
                            !retailViewsBanknote11.isOutOfHierarchyBoundsHorizontally(targetAncestor)
                        val isSecondBanknotesColumnVisible =
                            !retailViewsBanknote21.isOutOfHierarchyBoundsHorizontally(targetAncestor)
                        if (!isFirstBanknotesColumnVisible && !isSecondBanknotesColumnVisible) {
                            /* Скрываем все банкноты. */
                            root.isGone = true
                        } else if (!isFirstBanknotesColumnVisible) {
                            /* Скрываем только первый столбец банкнот, второй не трогаем. */
                            retailViewsBanknote11.isGone = true
                            retailViewsBanknote12.isGone = true
                            retailViewsBanknote13.isGone = true
                        }
                    }
                }
            }
        )
    }

    private fun setupMaxWidthForBanknoteViewsIfNeeded(banknotesInColumn: List<BanknoteView>) {
        if (banknotesInColumn.size > 1) {
            var maxBanknoteWidth = banknotesInColumn.first().width
            banknotesInColumn.drop(1).forEach { banknoteView ->
                if (banknoteView.width > maxBanknoteWidth) {
                    maxBanknoteWidth = banknoteView.width
                }
            }

            /* Требуется обновить ширину всех View в столбце на ширину самого большого элемента. */
            banknotesInColumn
                .filter { maxBanknoteWidth > it.width }
                .forEach { banknoteView -> banknoteView.updateBanknoteWidth(maxBanknoteWidth) }
        }
    }

    private fun isPortraitOrientation(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    private fun CustomNumericKeyboardHelper.setupForViews(vararg wrappers: InputViewWrapper) {
        wrappers.forEach { fieldWrapper ->
            fieldWrapper.inputField.onFocusChangeListener =
                View.OnFocusChangeListener { viewLocal, hasFocus ->
                    if (viewLocal is EditText && hasFocus) {
                        viewLocal.setupNumericKeyboard(this, fieldWrapper)
                    } else viewLocal.clearFocus()
                }
        }
    }

    private fun EditText.setupNumericKeyboard(
        keyboardHelper: CustomNumericKeyboardHelper,
        fieldWrapper: InputViewWrapper
    ) {
        /* Предварительно перенастраиваем поведение [CustomNumericKeyboardHelper] связанное с Hint'ом.  */
        keyboardHelper.overrideHints = fieldWrapper.needOverrideInputFieldHint

        /* "Подключаем" нашу клавиатуру к выбранному полю ввода. */
        keyboardHelper.setInputField(
            inputField = this,
            fieldType = fieldWrapper.fieldInputType,
            needToUseDefaultInputLimits = fieldWrapper.fieldInputType == FieldType.MONEY
        )
    }

    /**
     * Единая точка входа для обновления модели [Amount].
     *
     * @param paymentMethod тип поля, в которое вводится значение.
     * @param isBanknotesInput тип пользовательского ввода:
     * 'true' - при помощи блока с банкнотами,
     * 'false' - ручной, при помощи клавиатуры.
     * @param inputValue новое значение [Amount].
     */
    private fun EditText.updateMoneyOnManual(
        paymentMethod: PaymentMethod?,
        isBanknotesInput: Boolean,
        inputValue: BigDecimal
    ) {
        when (paymentMethod) {
            /* Поле ввода принимает расчет "Наличными". */
            PaymentMethod.CASH -> {
                if (isBanknotesInput) {
                    /* Всегда '+', т.к. поддерживаем отрицательные значения. */
                    banknotesApi.allAmount.cash += inputValue

                    updateMoneyOnBanknote(this, banknotesApi.allAmount.cash)
                } else {
                    /* Значение всегда равняется текущему пользовательскому вводу. */
                    banknotesApi.allAmount.cash = inputValue
                }
            }

            /* Поле ввода принимает расчет "По карте". */
            PaymentMethod.CARD -> {
                if (isBanknotesInput) {
                    /* Всегда '+', т.к. поддерживаем отрицательные значения. */
                    banknotesApi.allAmount.card += inputValue

                    updateMoneyOnBanknote(this, banknotesApi.allAmount.card)
                } else {
                    /* Значение всегда равняется текущему пользовательскому вводу. */
                    banknotesApi.allAmount.card = inputValue
                }
            }

            /* Поле ввода не отвечает за пополнение денежных средств. */
            null -> {
                if (isBanknotesInput) {
                    /* Всегда '+', т.к. поддерживаем отрицательные значения. */
                    convertUserInputToBigDecimalOrNull(text.toString())?.let { decimalValue ->
                        updateMoneyOnBanknote(this, amount = decimalValue + inputValue)
                    }
                }
            }

            /* Данный тип не поддерживается, кидаем ошибку. */
            else -> throw getUnsupportedMethodException(paymentMethod)
        }

        /* Уведомляем внешних слушателей, что мы обновили кол-во внесенных средств. */
        banknotesApi.notifyAllSubscribersAmountChangeTo(banknotesApi.allAmount)
    }

    private fun updateMoneyOnBanknote(inputField: EditText, amount: BigDecimal) {
        inputField.setInputFieldTextProgrammatically(
            inputTag = AMOUNT_CHANGED_BY_BANKNOTES_TAG,
            inputText = amountFormat.format(amount)
        )
    }

    private inner class AmountInputViewTextWatcher(
        private val inputViewWrapper: InputViewWrapper,
        private val keyboardHelper: CustomNumericKeyboardHelper
    ) : TextWatcher {

        private val editField = inputViewWrapper.inputField

        override fun afterTextChanged(inputText: Editable?) {
            /* HandleAutoSizeAction. */
            inputViewWrapper.handleAutoSizeActionIfNeeded(inputText.toString(), editField)

            /* Осуществлен ввод банкнотами. */
            val isBanknotesInput = editField.tag == AMOUNT_CHANGED_BY_BANKNOTES_TAG

            /*
             * Логика из 'MoneyInputView': сбрасываем все примененные банкноты,
             * если пользователь начал вводить сумму руками, при помощи клавиатуры.
             *
             * Важно: логика должна работать только для полей с типом "Наличные".
             */
            if (inputViewWrapper.paymentMethod == PaymentMethod.CASH) {
                if (lastInputWasBanknotes && isBanknotesInput.not()) {
                    resetBanknotes()
                    banknotesApi.allAmount.cash = ZERO_MONEY_VALUE
                }

                /* Изменяем флаг использования ввода банкнотами. */
                lastInputWasBanknotes = isBanknotesInput
            }

            /* Защита от бесконечного цикла, при вводе с банкнотами. */
            if (isBanknotesInput.not()) {
                convertUserInputToBigDecimalOrNull(inputText.toString())?.let { decimalValue ->
                    /*
                        Защита от повторного вызова колбэков изменения значений Amount.
                        https://online.sbis.ru/opendoc.html?guid=80a3353f-5d34-4716-8a14-e1b5e06e9bca&client=3
                    */
                    if (inputAmounts[inputViewWrapper]?.compareTo(decimalValue) != 0) {
                        inputAmounts[inputViewWrapper] = decimalValue
                        editField.updateMoneyOnManual(
                            paymentMethod = inputViewWrapper.paymentMethod,
                            isBanknotesInput = isBanknotesInput,
                            inputValue = decimalValue
                        )
                    }
                }
            }

            /* Осуществлен ввод путем пересчета недостающего значения. */
            val isProgrammaticallyDiffInput = editField.tag == AMOUNT_CHANGED_BY_DIFF_TAG
            if (isProgrammaticallyDiffInput) {
                /* Нужно выровнять каретку ввода. */
                keyboardHelper.updateCursorOffset(editField, inputViewWrapper.fieldInputType)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    private fun InputViewWrapper.handleAutoSizeActionIfNeeded(inputText: String, inputField: EditText) {
        if (autoSizeConfig != null && inputText.isNotEmpty()) {
            val autoSizeAction = Runnable {
                /* Применяем [InputViewAutoSizeConfig], если он был задан для указанного поля ввода. */
                autoSizeConfig.applyAutoSizeForView(inputField, inputText)
            }

            if (inputField.width != 0) autoSizeAction.run()
            else inputField.post(autoSizeAction)
        }
    }

    /* Можно оптимизировать, если передавать признак: "увеличилось кол-во символов или уменьшилось." */
    private fun InputViewWrapper.AutoSizeConfig.applyAutoSizeForView(view: EditText, inputText: String) {
        /* Пустые строки и 'нулевую' View - игнорируем. */
        if (inputText.isEmpty() || view.width == 0) return

        /* 'доступная ширина для текста' = 'ширина поля ввода (View)' - 'боковые отступы'. */
        val inputFieldAvailableWidth = view.width - (view.paddingStart + view.paddingEnd)

        /* Ширина текста больше, чем ширина View - места больше нет. */
        autoTextSizeRangeIterable.forEach { fontSize ->
            /*
                Ширина View больше, чем ширина текста - место еще есть.

                ВАЖНО: читаем через 'getScaleOffDimenPx(...) : Int', чтобы корректно
                округлять применяемый размер шрифта (работа по аналогии с [TextView]).
            */
            val configInputViewTextSize = fontSize.getScaleOffDimenPx(view.context).toFloat()

            if (view.textSize != configInputViewTextSize) {
                /* Устанавливаем дефолтный размер текста. */
                view.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    configInputViewTextSize
                )
            }

            /* Выполняем замеры ширины текста. */
            view.paint.getTextBounds(inputText, 0, inputText.length, inputTextSizeBox)
            val inputTextWidth = inputTextSizeBox.width()

            /* Измеряем - поместился текст или нет, иначе "второй круг". */
            if (inputTextWidth == 0 || inputFieldAvailableWidth >= inputTextWidth) {
                /* Все поместилось, более проходы не требуются. */
                return
            }
        }
    }

    /**
     * Специализированный метод для установки значения в поля ввода через код.
     * Влияет на логику работы [AmountInputViewTextWatcher].
     *
     * @param inputTag индивидуальный тег, для корректной работы [AmountInputViewTextWatcher].
     * @param inputText вводимый текст.
     */
    private fun EditText.setInputFieldTextProgrammatically(inputTag: String, inputText: String) {
        tag = inputTag
        setText(inputText)
        tag = null
    }

    private fun getUnsupportedMethodException(paymentMethod: PaymentMethod?) =
        Exception("Указанный способ оплаты $paymentMethod, не поддержан в делегате ${this::class.java.simpleName}!")
}