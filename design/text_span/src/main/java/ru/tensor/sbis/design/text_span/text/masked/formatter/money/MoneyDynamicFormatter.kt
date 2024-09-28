package ru.tensor.sbis.design.text_span.text.masked.formatter.money

import android.graphics.Color
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import ru.tensor.sbis.design.text_span.span.CharSequenceSpan
import ru.tensor.sbis.design.text_span.span.CharSequenceTailSpan
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.mask.MaskParser
import ru.tensor.sbis.design.text_span.text.masked.mask.MaskSymbol

/**
 * Реализация [Formatter] для денег, который применяет маску в зависимости от длинны. Красит сумму и увеличивает размер шрифта до точки.
 *
 * @author ar.leschev
 */
internal class MoneyDynamicFormatter(parser: MaskParser) : Formatter {

    /**
     * Предполагаемая длина ввода. Используется для определения места вставки "хвоста форматирования"
     */
    private val expectedLen = parser.types.size
    private val placeholders = parser.placeholders
    private val moneyColor: Int = Color.parseColor(MONEY_COLOR)

    /**
     * Для форматирвоания извлекаются только символы, которые не относятся к маске [MaskSymbol].
     * Какие именно символы в маске не имеет значения
     */
    constructor(mask: CharSequence) : this(MaskParser(mask))

    override fun apply(s: Spannable) {
        val dotPosition = s.indexOf('.')

        val hasDot = dotPosition > 0

        // установим форматирование
        for (index in 0..s.lastIndex) {
            // Размер суммы
            if (s.getSpans(index, index + 1, RelativeSizeSpan::class.java).isEmpty() &&
                (index < dotPosition || hasDot.not())
            ) s.setSpan(
                RelativeSizeSpan(MONEY_SIZE_FACTOR),
                index,
                index + 1,
                Spanned.SPAN_POINT_MARK
            )

            // Отступы из маски
            placeholders[index]?.apply {
                s.setSpan(CharSequenceSpan(this, moneyColor), index, index + 1, Spanned.SPAN_POINT_MARK)
            }
        }

        /*
        Добавим "хвост" форматирования в конец, причём при привышении длины форматера "хвост" будет
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

        // Установить цвет суммы до начала копеек
        s.setSpan(
            ForegroundColorSpan(moneyColor),
            0,
            dotPosition.takeIf { it > 0 } ?: s.length,
            Spanned.SPAN_POINT_MARK
        )
    }

    private companion object {
        /**
         * Коэффициент увеличения суммы относительно копеек.
         */
        private const val MONEY_SIZE_FACTOR = 1.5f

        /**
         * Цвет суммы до начала копеек.
         */
        private const val MONEY_COLOR = "#30415D"
    }
}