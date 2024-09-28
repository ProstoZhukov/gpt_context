package ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers

import android.text.Editable
import android.widget.EditText
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.numberic_keyboard.NumericKeyboard
import ru.tensor.sbis.design.retail_views.numberic_keyboard.internal.LengthFilterWithPointAndDecimalPartExclusion
import ru.tensor.sbis.design.retail_views.utils.divider
import ru.tensor.sbis.design.retail_views.utils.format
import ru.tensor.sbis.design.retail_views.utils.formatAmount
import ru.tensor.sbis.design.retail_views.utils.intAmountFormat
import ru.tensor.sbis.design.retail_views.utils.kopeckCursorOffset
import ru.tensor.sbis.design.retail_views.utils.moneyZeroValue
import ru.tensor.sbis.design.retail_views.utils.numberDecimalCursorOffset
import ru.tensor.sbis.design.retail_views.utils.numberZeroValue
import ru.tensor.sbis.design.retail_views.utils.quantityDecimalCursorOffset
import ru.tensor.sbis.design.retail_views.utils.quantityFormat
import ru.tensor.sbis.design.retail_views.utils.quantityZeroValue
import ru.tensor.sbis.design.retail_views.utils.whitespace
import kotlin.math.max

/** Класс для обработки и отображения введенного с цифровой клавиатуры значения. */
class CustomNumericKeyboardHelper {

    private val maxValueLengthDefault = 13

    private var needToUseDefaultInputLimits = false

    /** Действие, которое будет вызвано при нажатии на кнопку "Reset". */
    var resetClickedAction: (() -> Unit)? = null

    /** Нужно-ли переопределять хинты значениями по-умолчанию. */
    var overrideHints = true

    private lateinit var keyboardActions: NumericKeyboard
    private var _inputField: EditText? = null
    private var inputField: EditText?
        get() {
            if (_inputField?.hasFocus() == false) _inputField?.requestFocus()
            return _inputField
        }
        set(value) {
            _inputField = value
        }

    fun setKeyboard(keyboard: NumericKeyboard) {
        this.keyboardActions = keyboard
        initKeyboard()
    }

    fun setInputField(
        inputField: EditText?,
        fieldType: FieldType = FieldType.MONEY,
        needToUseDefaultInputLimits: Boolean = true,
        canUseSoftKeyboard: Boolean = false
    ) {
        if (this.inputField !== inputField || this.inputField?.fieldType != fieldType) {
            this.inputField = inputField
            this.needToUseDefaultInputLimits = needToUseDefaultInputLimits
            val maxLength = if (needToUseDefaultInputLimits) maxValueLengthDefault else null

            initInputField(fieldType, maxLength, canUseSoftKeyboard)
        }
    }

    /** Обновить позицию курсора для указанного поля ввода. */
    fun updateCursorOffset(inputField: EditText, fieldType: FieldType) {
        inputField.cursorOffset = fieldType.defaultCursorOffset
    }

    /**
     * Отвязать клавиатуру от конкретного поля
     */
    fun detachInputFieldIfSame(inputFieldToDetach: EditText?) {
        if (this.inputField === inputFieldToDetach) {
            setInputField(inputField = null)
        }
    }

    private fun initInputField(
        fieldType: FieldType,
        maxLength: Int?,
        canUseSoftKeyboard: Boolean = false
    ) = inputField?.also { inputFieldLocal ->
        inputFieldLocal.fieldType = fieldType
        inputFieldLocal.showSoftInputOnFocus = canUseSoftKeyboard
        maxLength?.let { length ->
            inputFieldLocal.filters = arrayOf(
                LengthFilterWithPointAndDecimalPartExclusion(length, divider.toString(), inputFieldLocal)
            )
        }
        if (inputFieldLocal.text.isEmpty() && overrideHints) resetValue()
    }

    private fun initKeyboard() {
        keyboardActions.numericClickAction = { inputSymbol -> processNumericButtonClick(inputSymbol) }
        keyboardActions.bottomRightButtonClickAction = { moveCursorToKopeck() }
        keyboardActions.resetClickAction = {
            if (overrideHints) {
                resetValue()
            } else {
                clearValue()
            }
            resetClickedAction?.invoke()
        }
    }

    private fun processNumericButtonClick(inputSymbol: String) {
        inputField?.run {
            when {
                isAllTextSelected -> processEmptyValue(inputSymbol)
                fieldType == FieldType.EDIT -> text.append(inputSymbol)
                fieldType == FieldType.NUMBER -> processInteger(inputSymbol)
                fieldType == FieldType.NUMBER_GROUPING -> processInteger(inputSymbol)
                text.isEmpty() -> processEmptyValue(inputSymbol)
                cursorOffset > decimalCursorOffset -> processInteger(inputSymbol)
                else -> processKopeck(inputSymbol)
            }
        }
    }

    private fun processEmptyValue(inputSymbol: CharSequence) {
        // установка значения по умолчанию
        resetValue()
        // вставка символа в целую часть
        inputField?.run {
            val stIdx = (selectionEnd - 1).coerceAtLeast(0)
            text.replace(
                stIdx,
                selectionEnd,
                inputSymbol
            )
        }
    }

    private fun processInteger(inputSymbol: CharSequence) {
        inputField?.let {
            val cursorPosition = it.selectionEnd
            val text = Editable.Factory.getInstance().newEditable(it.text)

            // проверка граничных случаев
            if (cursorPosition == 0 && inputSymbol == "0") return

            // если перед курсором стоит 0, заменяем его на число,
            // иначе просто вставляем число и двигаем курсор
            if (text.substring(0, cursorPosition) == "0") {
                text.replace(0, 1, inputSymbol)
                updateText(text)
                it.setSelection(cursorPosition)
                return
            }

            // обновление текста и позиции курсора
            val textBeforeInsert = text.toString()
            if (!needToUseDefaultInputLimits || textBeforeInsert.length < maxValueLengthDefault) {
                var newCursorPosition = cursorPosition
                text.insert(cursorPosition, inputSymbol)
                updateText(text)
                val textAfterInsert = it.text?.toString() ?: return
                newCursorPosition += (textAfterInsert.length - textBeforeInsert.length)
                it.setSelection(newCursorPosition)
            }
        }
    }

    private fun processKopeck(inputSymbol: CharSequence) {
        inputField?.let {
            val cursorPosition = it.selectionEnd
            val cursorOffset = it.cursorOffset

            // вставляем копейки и обновляем поле ввода
            if (cursorOffset > 0) {
                val text = Editable.Factory.getInstance().newEditable(it.text)
                text.replace(cursorPosition, cursorPosition + 1, inputSymbol)
                updateText(text)
                it.cursorOffset = cursorOffset - 1
            }
        }
    }

    fun processBackspaceButtonClick() {
        inputField?.let {
            val text = Editable.Factory.getInstance().newEditable(it.text)
            var cursorPosition = it.selectionEnd
            val symbol: CharSequence

            // проверка граничных случаев
            if (text.isBlank() ||
                cursorPosition == 0 ||
                cursorPosition == 1 && text[cursorPosition - 1] == divider
            ) {
                return
            }

            if (it.cursorOffset > it.decimalCursorOffset) { // целая часть
                // удаление символа
                cursorPosition -= if (text[cursorPosition - 1] == whitespace) 2 else 1
                symbol = ""
            } else { // дробная часть
                if (text[cursorPosition - 1] == divider) {
                    // если дошли до разделителя, перескакиваем в целую часть и удаляем символ оттуда
                    cursorPosition -= 2
                    symbol = ""
                } else {
                    // иначе заменяем символ на 0
                    cursorPosition -= 1
                    symbol = "0"
                }
            }

            // обновление текста
            text.replace(cursorPosition, cursorPosition + 1, symbol)
            var newCursorPosition = cursorPosition
            val textBeforeInsert = it.text.toString()
            updateText(text)
            val textAfterInsert = it.text.toString()
            newCursorPosition += (textAfterInsert.length - textBeforeInsert.length)
            // обновление позиции курсора
            it.setSelection(max(newCursorPosition, 0))
        }
    }

    private fun moveCursorToKopeck() {
        inputField?.run {
            if (text.isEmpty()) {
                return
            } else {
                cursorOffset = decimalCursorOffset
            }
        }
    }

    private fun resetValue() {
        inputField?.run {
            setText(defaultValue)
            cursorOffset = defaultCursorOffset
        }
    }

    fun clearValue() {
        inputField?.run {
            setText("")
        }
    }

    private fun updateText(newText: String) {
        inputField?.run {
            val formattedText = fieldType!!.formatAction.invoke(newText)
            if (text.toString() != formattedText) {
                setText(formattedText)
            }
        }
    }

    private fun updateText(newText: Editable) = updateText(newText.toString())

    private var EditText.cursorOffset: Int
        get() {
            return text.length - selectionEnd
        }
        set(value) {
            setSelection(text.length - value)
        }

    private var EditText.fieldType
        get() = getTag(R.id.retail_views_numeric_keyboard_input_field_type_id) as? FieldType
        set(value) = setTag(
            R.id.retail_views_numeric_keyboard_input_field_type_id,
            value
        )

    private val EditText.defaultCursorOffset
        get() = fieldType!!.defaultCursorOffset

    private val EditText.decimalCursorOffset
        get() = fieldType!!.decimalCursorOffset

    private val EditText.defaultValue
        get() = fieldType!!.defaultValue

    private val EditText.isAllTextSelected
        get() = selectionEnd - selectionStart == text.length

    enum class FieldType(
        val defaultValue: String,
        val defaultCursorOffset: Int,
        val decimalCursorOffset: Int,
        val formatAction: (String) -> String
    ) {
        EDIT(
            "",
            0,
            0,
            { it }
        ),
        MONEY(
            moneyZeroValue,
            3,
            kopeckCursorOffset,
            { formatAmount(it) }
        ),
        QUANTITY(
            quantityZeroValue,
            4,
            quantityDecimalCursorOffset,
            { format(it, quantityFormat) }
        ),
        NUMBER(
            numberZeroValue,
            numberDecimalCursorOffset,
            numberDecimalCursorOffset,
            { it }
        ),

        /** Тип поля ввода - целое число с группировкой по разрядам. Например: 1 234 567 */
        NUMBER_GROUPING(
            numberZeroValue,
            numberDecimalCursorOffset,
            numberDecimalCursorOffset,
            { format(it, intAmountFormat) }
        )
    }
}