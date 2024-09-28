package ru.tensor.sbis.design.text_span.text.masked.formatter.simple

import ru.tensor.sbis.design.text_span.text.masked.formatter.FormatterRule
import timber.log.Timber

/**
 * Реализация [SimpleFormatter], который применяет первый подходящий форматтер из [rules], в зависимости от
 * пользовательского ввода
 *
 * @author us.bessonov
 */
internal class ConditionalSimpleFormatter(private val rules: List<FormatterRule<SimpleFormatter>>) : SimpleFormatter {

    override fun apply(s: CharSequence): CharSequence {
        return rules.firstOrNull { it.predicate(s) }
            ?.formatter
            ?.apply(s)
            ?: s.also {
                Timber.w(IllegalStateException("Cannot format text: there is no rule that matches '$s'"))
            }
    }
}