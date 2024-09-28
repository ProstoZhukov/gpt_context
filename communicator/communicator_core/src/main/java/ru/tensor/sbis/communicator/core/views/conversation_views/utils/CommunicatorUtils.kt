package ru.tensor.sbis.communicator.core.views.conversation_views.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import ru.tensor.sbis.common_views.SearchSpan
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * Утилиты для построения ячеек списка реестра диалогов/каналов
 *
 * @author vv.chekurda
 */
object CommunicatorUtils {

    /**
     * Плотность пикселей для расчета значений dp
     */
    private var density = 1f

    /**
     * Масштабируемая плотность пикселей для расчета значений sp
     */
    private var scaledDensity = 1f

    /**
     * Установить плотности для расчетов значений dp и sp
     */
    fun setupDensities(context: Context) {
        density = context.resources.displayMetrics.density
        scaledDensity = context.resources.displayMetrics.scaledDensity
    }

    /**
     * Получить значение в пикселях по переданному значению в dp,
     * округленное до целого числа (по правилам округления).
     */
    fun dp(value: Float): Int =
        if (value == 0f) 0
        else (density * value).toDouble().roundToInt()

    /**
     * Получить значение в пикселях по переданному значению в dp,
     * округленное до целого числа (по правилам округления).
     */
    fun dp(value: Int): Int =
        dp(value.toFloat())

    /**
     * Получить значение в пикселях по переданному значению в sp,
     * округленное до целого числа (по правилам округления).
     */
    fun sp(value: Float): Int =
        (scaledDensity * value).toDouble().roundToInt()

    /**
     * Получить значение в пикселях по переданному значению в sp,
     * округленное до целого числа (по правилам округления).
     */
    fun sp(value: Int): Int =
        sp(value.toFloat())

    /**
     * Получить строку с выделенным текстом из переданного списка выделений в формате (начало выделения, конец выделения).
     * @see SearchSpan
     */
    fun CharSequence.highlightText(spanList: List<SearchSpan>?): Spannable =
        (this as? Spannable ?: SpannableString(this)).apply {
            if (length == 0 || spanList == null) return@apply
            try {
                // Проверка на наличие символа троеточия в качестве сокращения текста
                val symbolEllipsized = last().toString() == "\u8230"
                // Проверка на наличие трех точек в качестве сокращения текста
                val simpleEllipsized = length > 3 && lastIndexOf("...") == lastIndex - 2
                // Вычисление последней позиции текста до сокращения
                val lastTextPosition = when {
                    symbolEllipsized -> lastIndex - 1
                    simpleEllipsized -> lastIndex - 3
                    else -> lastIndex
                }
                spanList.forEach {
                    when {
                        it.start <= lastTextPosition && it.end <= lastTextPosition -> {
                            setHighlightSpan(this, it.start, it.end)
                        }
                        it.start <= lastTextPosition && it.end > lastTextPosition -> {
                            setHighlightSpan(this, it.start, length)
                            return@forEach
                        }
                        it.start > lastTextPosition && (symbolEllipsized || simpleEllipsized) -> {
                            setHighlightSpan(this, lastTextPosition + 1, length)
                            return@forEach
                        }
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
                Timber.e("Ошибочные позиций выделения текста при поиске: ${ex.message}")
            }
        }

    /**
     * Применить span выделения текста [spannable] от позиции [start] до позиции [end] включительно,
     * и получить результат.
     */
    private fun setHighlightSpan(spannable: Spannable, start: Int, end: Int): Spannable =
        spannable.apply {
            setSpan(
                BackgroundColorSpan(CommunicatorTheme.theme_highlightTextColor), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
}