package ru.tensor.sbis.design.view.input.mask

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.widget.EditText
import kotlin.math.max
import kotlin.math.min

/**
 * Стандартная для полей ввода логика ввода символов по маске, где 0 - число, А - буква, * - любой символ.
 *
 * @param mask маска.
 * @param initialText начальный текст, который будет содержаться в поле ввода.
 *
 * @author ps.smirnyh
 */
internal class MaskInputViewTextWatcher(
    private val inputView: EditText,
    private val valueChangedWatcher: TextWatcher,
    mask: String,
    initialText: Editable? = null
) : BaseMaskInputViewTextWatcher(mask) {

    /**
     * Длина строки.
     */
    private val length: Int
        get() = chars.size

    /**
     * Символы в строке.
     */
    private var chars: CharArray = CharArray(mask.length, mask::get)

    /**
     * Типы символов в строке.
     */
    private var types: Array<MaskSymbol> = arrayOf()

    /**
     * Позиция первого изменяемого символа в строке.
     */
    private var firstDynamic: Int = 0

    init {
        initMask(mask)
        initialText?.let {
            newText = it
            afterTextChanged(it)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        s ?: return
        doIfNotApplyingFormat {
            isRecursiveAfterTextChangedCall = true
            // удаляем watcher, чтобы не публиковать промежуточные события изменения текста
            inputView.removeTextChangedListener(valueChangedWatcher)
            applyMask(s)
            // добавляем watcher, чтобы публиковать изменения текста
            inputView.addTextChangedListener(valueChangedWatcher)
            isRecursiveAfterTextChangedCall = false
        }
    }

    override fun changeMask(newMask: String, editable: Editable) {
        super.changeMask(newMask, editable)
        chars = CharArray(mask.length, mask::get)
        initMask(mask)
        afterTextChanged(editable)
    }

    private fun initMask(mask: String) {
        var fixedPrefix = true
        var firstDynamic = 0
        types = Array(
            mask.length,
            fun(position: Int): MaskSymbol {
                val char = mask[position]
                for (s in MaskSymbol.values()) {
                    if (s.symbol == char) {
                        // Запоминаем тип символа и заменяем его на соответствующий placeholder
                        chars[position] = s.placeholder
                        if (s != MaskSymbol.FIXED && fixedPrefix) {
                            // Запоминаем позицию первого изменяемого символа
                            fixedPrefix = false
                            firstDynamic = position
                        }
                        return s
                    }
                }
                return MaskSymbol.FIXED
            }
        )
        this.firstDynamic = firstDynamic
    }

    private fun applyMask(editable: Editable) {
        val selection = applyInputChanges()
        insertTo(editable, selection)
    }

    private fun applyInputChanges(): Int = when {
        // Удалили часть символов
        oldText.isNotEmpty() && newText.isEmpty() -> delete(replacementStart, oldText.length, true)
        // Вставили несколько символов
        newText.length > 1 -> clear().run { insert(replacementStart, newText) }
        // Вписали новый символ
        else -> insert(replacementStart, newText)
    }

    private fun insertTo(editable: Editable, selection: Int) {
        // Очистим поле ввода
        editable.clear()
        // Обновляем значение в editText полностью сформированное маской
        editable.append(String(chars))
        // Устанавливаем каретку
        Selection.setSelection(editable, selection)
    }

    private fun insert(start: Int, input: CharSequence): Int {
        var pos = start
        var i = 0
        val replacedSymbols = mutableListOf<Char>()
        while (pos < length && i < input.length) {
            val type = types[pos]
            val char = input[i]
            if (type == MaskSymbol.FIXED) {
                // Обрабатываем фиксированный символ
                if (chars[pos] == char) {
                    // Фиксированный символ совпал с символом из input
                    ++i // Переходим к следующему символу из input
                }
                ++pos
            } else {
                // Обрабатываем динамический символ
                if (type.matches(char) || char == type.placeholder) {
                    // Символ из input подошел к маске
                    if (chars[pos] != type.placeholder || replacedSymbols.isNotEmpty()) {
                        // сохраняем заменённый символ
                        replacedSymbols.add(chars[pos])
                    }
                    chars[pos] = char
                    ++pos // Переходим к заполнению следующего символа
                }
                ++i // Переходим к следующему символу из input
            }
        }
        return validatePosition(pos)
    }

    private fun delete(start: Int, count: Int, atLeastOne: Boolean): Int {
        var pos = start
        var endPosition = start
        var prefix = true
        while (pos < start + count) {
            val type = types[pos]
            if (type != MaskSymbol.FIXED) {
                prefix = false
                // Сбрасываем символ на позиции с нефиксированным символом
                chars[pos] = type.placeholder
            } else if (prefix) {
                // Если мы находимся в префиксе - смещаем конечную позицию на след. символ
                endPosition = pos + 1
            }
            ++pos
        }

        if (atLeastOne && prefix) {
            // Необходимо удалить хотя бы один символ,
            // но в диапазоне только статичные символы маски
            do {
                // Смещаемся в начало строки, ищем символ, который можно удалить
                --endPosition
            } while (endPosition > -1 && types[endPosition] == MaskSymbol.FIXED)

            if (endPosition > -1) {
                // Нашли символ перед диапазоном, который можно удалить
                chars[endPosition] = types[endPosition].placeholder
            } else {
                // От начала строки до start все символы фиксированные
                endPosition = start + count
            }
        }
        return validatePosition(endPosition)
    }

    private fun clear(): Int = delete(0, length, false)

    private fun validatePosition(position: Int): Int {
        val result = min(position, length)
        return max(result, firstDynamic)
    }
}