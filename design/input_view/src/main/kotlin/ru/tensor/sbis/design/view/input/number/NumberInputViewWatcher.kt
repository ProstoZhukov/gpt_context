package ru.tensor.sbis.design.view.input.number

import android.text.Editable
import android.text.Selection
import android.text.Spanned
import android.text.TextWatcher
import ru.tensor.sbis.design.text_span.span.CharSequenceSpan
import ru.tensor.sbis.design.utils.extentions.clearSpans
import timber.log.Timber
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewGroupDecorationApi

/**
 * Реализация [TextWatcher] для числового поля ввода с поддержкой стандартного поведения
 * и ограничения вводимого значения.
 *
 * @author ps.smirnyh
 */
internal open class NumberInputViewWatcher : TextWatcher {

    private var newText = ""

    /**
     * Блокировка рекурсии при изменении [Editable] в [afterTextChanged].
     */
    private var isRecursiveAfterTextChangedCall = false

    private val groupingSeparator: CharSequence = " "

    /**
     * Является ли изменение текста удалением.
     */
    protected var isDeleting = false

    /**
     * Начальный индекс редактируемого текста.
     */
    protected var editStartIndex = 0

    /**
     * Позиция разделителя до начала редактирования.
     */
    protected var oldDotIndex = -1

    /**
     * Минимальное значение для ввода.
     */
    internal var min: Double = -Double.MAX_VALUE

    /**
     * Максимальное значение для ввода.
     */
    internal var max: Double = Double.MAX_VALUE

    /**
     * Свойство, отвечающее показывать ли нулевое значение при пустой строке в поле ввода.
     */
    internal open var isShownZeroValue: Boolean = false

    /**
     * Длина дробной части для числового поля.
     */
    internal var numberFraction: UByte = UByte.MAX_VALUE

    /** @see NumberInputViewGroupDecorationApi.usesGroupingSeparator */
    internal var usesGroupingSeparator: Boolean = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (s == null || isRecursiveAfterTextChangedCall) return
        isDeleting = count > after
        oldDotIndex = s.indexOf('.')
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isRecursiveAfterTextChangedCall) return
        newText = s?.substring(start, start + count) ?: ""
        editStartIndex = start
    }

    override fun afterTextChanged(s: Editable?) {
        s ?: return
        if (isRecursiveAfterTextChangedCall) return
        isRecursiveAfterTextChangedCall = true
        if (s.isEmpty() && isShownZeroValue) {
            s.insert(0, "0")
        }
        if (s.isEmpty()) {
            isRecursiveAfterTextChangedCall = false
            return
        }
        s.restoreDotIfNeed(oldDotIndex)
        s.insertZeroIfOnlyDot()
        s.deleteUnusedZero()
        // Если ввели разделитель, то после него ставим 0
        if (!isDeleting && s.endsWith(".")) {
            s.insert(s.length, "0")
            Selection.setSelection(s, s.length - 1)
        } else if (s.endsWith(".")) { // Если удалили дробную часть, то удаляем и точку
            s.delete(s.length - 1, s.length)
        }
        val indexIntegerPartEnd = s.findIntegerPartEnd()
        // Если вводим число после разделителя, то оно будет заменять число справа
        if (!isDeleting) {
            if (editStartIndex > indexIntegerPartEnd) {
                val indexEndEditing = Selection.getSelectionStart(s)
                s.delete(indexEndEditing, (indexEndEditing + newText.length).coerceAtMost(s.length))
            }
        }
        s.checkFractionPart(indexIntegerPartEnd)
        s.checkValueLimit(isFraction = indexIntegerPartEnd != s.length)
        s.clearSpans<CharSequenceSpan>()
        s.useGroupingSeparation(indexIntegerPartEnd)

        isRecursiveAfterTextChangedCall = false
    }

    /**
     * Свойство, показывающее можно ли вставить разделитель.
     * Вставлять можно, если [numberFraction] больше нуля и в данный момент разделителя нет.
     */
    protected val Editable.isCanInsertDot: Boolean
        get() = findIntegerPartEnd() == length && numberFraction > 0u

    /**
     * Найти индекс разделителя целой и дробной частей.
     * @return индекс разделителя целой и дробной частей или длину, если нет дробной части и разделителя.
     */
    protected fun Editable.findIntegerPartEnd(): Int {
        val dotIndex = indexOf('.')
        return if (dotIndex > -1) dotIndex else length
    }

    private fun Editable.useGroupingSeparation(indexIntegerPartEnd: Int) {
        if (!usesGroupingSeparator) {
            return
        }
        (indexIntegerPartEnd - 1 downTo 1).windowed(3, 3).forEach {
            setSpan(CharSequenceSpan(groupingSeparator), it.last(), it.last() + 1, Spanned.SPAN_POINT_MARK)
        }
    }

    /**
     * Восстановить разделитель, если он был удален, но должен всегда быть.
     */
    private fun Editable.restoreDotIfNeed(newPointIndex: Int) {
        if (!(oldDotIndex > -1 && isCanInsertDot && newPointIndex < length)) return
        insert(newPointIndex, ".")
        Selection.setSelection(this, newPointIndex)
        delete(newPointIndex - 1, newPointIndex)
    }

    /**
     * Вставить 0 первым символом, если первым символом является точка.
     */
    private fun Editable.insertZeroIfOnlyDot() {
        if (getOrNull(0) == '.') {
            insert(0, "0")
        }
    }

    /**
     * Удалить символы, если длина дробной части превышает [numberFraction].
     */
    private fun Editable.checkFractionPart(integerPartEnd: Int) {
        val currentFractionLength = length - integerPartEnd - 1
        val fraction = numberFraction.toInt()
        if (currentFractionLength > fraction) {
            delete(length - currentFractionLength + fraction, length)
        }
        if (fraction == 0 && endsWith('.')) {
            delete(length - 1, length)
        }
    }

    /**
     * Проверить, входит ли введенное значение в интервал [min]..[max].
     */
    private fun Editable.checkValueLimit(isFraction: Boolean) {
        val indexMinus = indexOf('-')
        if (min >= 0 && indexMinus > -1 && length == 1) replace(indexMinus, indexMinus + 1, "")
        val textValue = toString()
        try {
            if (newText.length > 1) {
                val replaceText = getValueInRange(textValue, isFraction)
                if (replaceText != textValue) {
                    replace(0, length, replaceText)
                }
            } else {
                if (textValue.toDouble() in min..max || editStartIndex == textValue.length) return
                delete(editStartIndex, editStartIndex + newText.length)
            }
        } catch (e: NumberFormatException) {
            Timber.i(e)
        }
    }

    private fun getValueInRange(originalText: String, isFraction: Boolean): String {
        return if (isFraction) {
            if (originalText.toDouble() in min..max) return originalText
            originalText.toDouble().coerceIn(min..max).toBigDecimal().toPlainString()
        } else {
            val longRange = min.toLong()..max.toLong()
            if (originalText.toLong() in longRange) return originalText
            originalText.toLong().coerceIn(longRange).toString()
        }
    }

    /**
     * Удалить не несущий смысла 0.
     */
    private fun Editable.deleteUnusedZero() {
        val integerPartEnd = findIntegerPartEnd()
        if (startsWith("0") && integerPartEnd > 1) {
            delete(0, 1)
        } else if (startsWith("-0") && integerPartEnd > 2) {
            delete(1, 2)
        }
    }
}