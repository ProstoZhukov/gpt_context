package ru.tensor.sbis.design.text_span.text.masked.formatter

import android.text.Spannable
import timber.log.Timber

/**
 * Условие для выбора форматтера. Для выбора будет передана строка пользовательского ввода без
 * символов форматирования
 *
 * @see [FormatterRule]
 */
internal typealias FormatterPredicate = (CharSequence) -> Boolean

/**
 * Правило выбора форматтера. Если выполняется [predicate], то использовать заданный [formatter]
 *
 * @author us.bessonov
 */
internal data class FormatterRule<FORMATTER>(val predicate: FormatterPredicate, val formatter: FORMATTER)

/**
 * Форматтер с условиями применения. Реализация [Formatter], который применяет тот или иной
 * форматтер из [rules] в зависимости от пользовательского ввода. Допустимо использование с одним
 * форматтером для ситуаций, когда его нужно применять только при определённых условиях
 *
 * @author ma.kolpakov
 * Создан 3/29/2019
 */
internal class ConditionalFormatter(
    private val rules: List<FormatterRule<Formatter>>
) : Formatter {

    init {
        if (rules.isEmpty()) {
            throw IllegalArgumentException("Empty rules list. It makes formatter useless and looks like mistake")
        }
    }

    override fun apply(s: Spannable) {
        rules.firstOrNull { (predicate, _) -> predicate(s) }
            ?.let { (_, formatter) -> formatter.apply(s) }
            ?: Timber.w(
                IllegalStateException(
                    "Unable to get formatter for input: '$s'. You should filter user input or expand rules list"
                )
            )
    }
}