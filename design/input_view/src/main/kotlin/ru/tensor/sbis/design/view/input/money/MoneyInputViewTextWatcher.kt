package ru.tensor.sbis.design.view.input.money

import android.text.Editable
import android.text.InputFilter
import android.text.Selection
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.decorators.FontColorStyle
import ru.tensor.sbis.design.decorators.MoneyDecorator
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontColorStyle
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontSize
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisDimen
import ru.tensor.sbis.design.utils.extentions.clearSpans
import ru.tensor.sbis.design.view.input.money.utils.style.MoneyStyleHolder.Companion.DEFAULT_IS_DECORATED
import ru.tensor.sbis.design.view.input.number.NumberInputViewKeyListener
import ru.tensor.sbis.design.view.input.number.NumberInputViewWatcher
import ru.tensor.sbis.design.view.input.utils.safeSetSelection

/**
 * Обновление декораций денежного поля, разделение на триады, конвертация значений.
 * @property moneyDecorator помощник для декорирования денег - цвета, размеры, см. [MoneyDecorator].
 *
 * @author ps.smirnyh
 */
internal class MoneyInputViewTextWatcher(
    private val moneyDecorator: MoneyDecorator,
    private val inputView: EditText,
    private val valueChangedWatcher: TextWatcher
) : NumberInputViewWatcher() {

    /**
     * Дополнительный конструктор.
     * @param integerPartColor цвет текста целой части.
     * @param integerPartSize размер текста целой части.
     * @param fractionPartColor цвет текста дробной части.
     * @param fractionPartSize размер текста дробной части.
     */
    @Suppress("UNUSED")
    constructor(
        @ColorInt integerPartColor: Int,
        @Px integerPartSize: Int,
        @ColorInt fractionPartColor: Int,
        @Px fractionPartSize: Int,
        inputView: EditText,
        valueChangedWatcher: TextWatcher
    ) : this(
        MoneyDecorator(inputView.context) {
            fontSize = NumberDecoratorFontSize.Custom(
                SbisDimen.Px(integerPartSize),
                SbisDimen.Px(fractionPartSize)
            )
            fontColorStyle = NumberDecoratorFontColorStyle(
                FontColorStyle.Custom(SbisColor.Int(integerPartColor)),
                FontColorStyle.Custom(SbisColor.Int(fractionPartColor))
            )
        },
        inputView,
        valueChangedWatcher
    )

    /**
     * Блокировка рекурсии при изменении [Editable] в [afterTextChanged].
     */
    private var isRecursiveAfterTextChangedCall = false

    /**
     * Позиция удаляемого пробела для удаления цифры после него.
     */
    private var deletedWhitespacePosition = 0
    private var originFilters: Array<InputFilter> = emptyArray()

    /**
     * Если true - поле декорируется, false - нет.
     */
    var isDecorated = DEFAULT_IS_DECORATED

    override var isShownZeroValue: Boolean = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (s == null || isRecursiveAfterTextChangedCall) return
        super.beforeTextChanged(s, start, count, after)
        if (count - after != 1) return
        deletedWhitespacePosition =
            if (s.toString()[start].isWhitespace()) start - s.countWhitespaces(0, start - 1) else 0
    }

    override fun afterTextChanged(s: Editable?) {
        s ?: return
        if (isRecursiveAfterTextChangedCall) return
        isRecursiveAfterTextChangedCall = true
        setUpBeforeEditing(s)

        val countWhitespaces = s.countWhitespaces(0, inputView.selectionStart)
        // Корректируем индекс с учетом удаления пробелов
        editStartIndex -= countWhitespaces

        val newText = SpannableStringBuilder(s)
        newText.removeAllSpaces()

        newText.safeSetSelection(inputView.selectionStart - countWhitespaces)

        // Корректируем индекс с учетом удаления пробелов
        oldDotIndex -= countWhitespaces

        // Форматирование по правилам числовых полей
        super.afterTextChanged(newText)

        newText.deleteBeforeWhitespace()
        newText.insertFractionIfNeed()

        newText.clearSpans<AbsoluteSizeSpan>()
        newText.clearSpans<ForegroundColorSpan>()
        s.replace(0, s.length, newText)
        inputView.text.safeSetSelection(Selection.getSelectionStart(newText))

        oldDotIndex = -1

        if (!isDecorated) {
            isRecursiveAfterTextChangedCall = false
            tearDownAfterEditing(s)
            return
        }

        // декорация если требуется
        inputView.text.decorate()

        tearDownAfterEditing(s)
        isRecursiveAfterTextChangedCall = false
    }

    private fun setUpBeforeEditing(editable: Editable) {
        inputView.removeTextChangedListener(valueChangedWatcher)
        originFilters = editable.filters
        editable.filters = editable.filters.filterNot { it is NumberInputViewKeyListener }.toTypedArray()
    }

    private fun tearDownAfterEditing(editable: Editable) {
        inputView.addTextChangedListener(valueChangedWatcher)
        editable.filters = originFilters
    }

    /**
     * Удалить символ после пробела, если нажали удалить перед пробелом.
     */
    private fun Editable.deleteBeforeWhitespace() {
        if (deletedWhitespacePosition > 0) {
            delete(deletedWhitespacePosition - 1, deletedWhitespacePosition)
            deletedWhitespacePosition = 0
        }
    }

    /**
     * Удалить все пробелы.
     */
    private fun Editable.removeAllSpaces() {
        var index = 0
        while (index < length) {
            if (get(index) == ' ') {
                delete(index, index + 1)
                index--
            }
            index++
        }
    }

    private fun Editable.insertFractionIfNeed() {
        val fractionLength = (lastIndex - findIntegerPartEnd()).coerceAtLeast(0)
        val oldSelection = Selection.getSelectionStart(this)
        moneyDecorator.configure {
            showEmptyDecimals = true
            precision = if (isNeedAddFractionPart) numberFraction else fractionLength.toUByte()
            useGrouping = true
        }
        moneyDecorator.changeValue(this.toString())
        val newSelection = oldSelection + moneyDecorator.formattedValue.countWhitespaces(0, oldSelection)
        replace(0, length, moneyDecorator.formattedValue)
        safeSetSelection(newSelection)
    }

    /**
     * Декорировать строку в соответствии со стандартом денежных полей ввода.
     */
    private fun Editable.decorate() {
        if (isNotEmpty()) {
            val fractionLength = (lastIndex - findIntegerPartEnd()).coerceAtLeast(0).toUByte()
            moneyDecorator.configure {
                showEmptyDecimals = true
                precision = if (isNeedAddFractionPart) numberFraction else fractionLength
                useGrouping = true
            }
            moneyDecorator.changeValue(this.toString().filter { !it.isWhitespace() })
            inputView.setTextKeepState(moneyDecorator.formattedValue)
        }
    }

    /**
     * Посчитать количество пробелов в отрезке с [startIndex] по [endIndex].
     */
    private fun CharSequence.countWhitespaces(startIndex: Int, endIndex: Int): Int {
        val allowedRange = 0..length
        if ((startIndex !in allowedRange) || (endIndex !in allowedRange)) {
            return 0
        }
        return subSequence(startIndex, endIndex).count { it.isWhitespace() }
    }

    /**
     * Нужно ли дополнять дробную часть до допустимой длины.
     */
    private val CharSequence.isNeedAddFractionPart: Boolean
        get() = (isShownZeroValue || !isDeleting) && isNotEmpty()
}