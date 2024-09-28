package ru.tensor.sbis.design.text_span.text.masked.watcher

import android.graphics.Rect
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.method.TransformationMethod
import android.view.View
import ru.tensor.sbis.design.text_span.span.CharSequenceSpan
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter

/**
 * Реализация [TextWatcher] и [TransformationMethod], которая применяет форматирование [formatter]
 * во время пользовательского ввода.
 * Не изменяет оригинальную строку. Только визуальное форматирование.
 *
 * Может использоваться как [TransformationMethod]
 * ```
 *    editText.transformationMethod = MaskFormatter(...)
 * ```
 *
 * Или как [TextWatcher]
 * ```
 *    editText.addTextChangedListener(MaskFormatter(...))
 * ```
 *
 * Использовать одновременно в качестве [TransformationMethod] и [TextWatcher] не нужно
 *
 * @param formatter отвечает за форматирование пользовательского ввода
 *
 * @throws IllegalStateException если форматирование применяется не к [Spannable] объектам
 *
 * @author ma.kolpakov
 * Создан 3/28/2019
 */
internal class FormatterHolder(
    private val formatter: Formatter
) : TransformationMethod, TextWatcher {

    override fun onFocusChanged(
        view: View?,
        sourceText: CharSequence?,
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) = Unit

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence? = source.apply(::applyMask)

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = applyMask(s)

    override fun afterTextChanged(s: Editable?) = Unit

    private fun applyMask(s: CharSequence?) {
        s ?: return
        with(
            checkNotNull(s as? Spannable) {
                "Unexpected CharSequence implementation. Only Spannable supported"
            }
        ) {
            // очистим прошлое форматирование
            getSpans(0, length, CharSequenceSpan::class.java).forEach(::removeSpan)
            // применим новое
            formatter.apply(this)
        }
    }
}