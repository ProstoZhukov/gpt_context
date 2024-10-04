package ru.tensor.sbis.design.text_span.text.masked.formatter

import android.text.Editable
import android.text.Selection
import ru.tensor.sbis.design.text_span.text.masked.mask.MaskedString
import ru.tensor.sbis.design.text_span.text.masked.mask.StaticMaskedString

/**
 * Реализация [StaticFormatter], которая отображает все символы форматирования сразу (даже пустая строка будет заполнена
 * символами форматирования)
 *
 * @param initialText изначальный текст, к которому следует примененить маску
 *
 * @author us.bessonov
 */
@Suppress("MemberVisibilityCanBePrivate")
open class StaticFormatterImpl(
    protected val maskedString: MaskedString,
    initialText: Editable? = null
) : StaticFormatter {

    constructor(mask: CharSequence, initialText: Editable? = null) : this(StaticMaskedString(mask), initialText)

    private var oldText: CharSequence = ""
    private var newText: CharSequence = ""
    protected var replacementStart = 0

    init {
        initialText?.let {
            newText = it
            applyMask(it)
        }
    }

    override fun onTextBlockChanged(start: Int, old: CharSequence, new: CharSequence) {
        replacementStart = start
        oldText = old
        newText = new
    }

    override fun apply(s: Editable) {
        applyMask(s)
    }

    private fun applyMask(s: Editable) {
        val selection = maskedString.applyInputChanges()
        maskedString.insertTo(s, selection)
    }

    private fun MaskedString.applyInputChanges(): Int = when {
        // Удалили часть символов
        oldText.isNotEmpty() && newText.isEmpty() -> delete(replacementStart, oldText.length, true)
        // Вставили несколько символов
        newText.length > 1 -> clear().also { insert(replacementStart, newText) }
        // Вписали новый символ
        else -> insert(replacementStart, newText)
    }

    private fun MaskedString.insertTo(editable: Editable, selection: Int) {
        // Очистим поле ввода
        editable.clear()
        // Обновляем значение в editText полностью сформированное маской
        editable.append(toString())
        // Устанавливаем каретку
        Selection.setSelection(editable, selection)
    }
}