package ru.tensor.sbis.design.text_span.text.masked.formatter.simple

/**
 * Предназначен для получения новой строки с применением требуемого форматирования
 *
 * @author us.bessonov
 */
interface SimpleFormatter {

    /**
     * @return строка с примением форматирования к [s]
     */
    fun apply(s: CharSequence): CharSequence
}