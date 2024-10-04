package ru.tensor.sbis.design.view.input.mask.phone.formatter

import android.text.Spannable
import android.text.Spanned
import ru.tensor.sbis.design.text_span.span.CharSequenceSpan
import ru.tensor.sbis.design.text_span.span.CharSequenceTailSpan
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.view.input.mask.MaskSymbol

/**
 * Реализация [Formatter], которая отображает символы форматирования отображаются по мере ввода текста.
 *
 * @author ps.smirnyh
 */
internal class DynamicFormatter(parser: MaskParser) : Formatter {

    /**
     * Предполагаемая длина ввода. Используется для определения места вставки "хвоста форматирования".
     */
    private val expectedLen = parser.types.size
    private val placeholders = parser.placeholders

    /**
     * Для форматирвоания извлекаются только символы, которые не относятся к маске [MaskSymbol].
     * Какие именно символы в маске не имеет значения.
     */
    constructor(mask: CharSequence) : this(MaskParser(mask))

    override fun apply(s: Spannable) {
        // установим форматирование
        for (index in 0..s.lastIndex) {
            placeholders[index]?.apply {
                s.setSpan(CharSequenceSpan(this), index, index + 1, Spanned.SPAN_POINT_MARK)
            }
        }
        /*
        Добавим "хвост" форматирования в конец, причём при превышении длины форматера "хвост" будет
        добавлен процедурой выше
         */
        if (s.length == expectedLen) {
            placeholders[expectedLen]?.apply {
                s.setSpan(
                    CharSequenceTailSpan(this),
                    expectedLen - 1,
                    expectedLen,
                    Spanned.SPAN_POINT_MARK
                )
            }
        }
    }
}