package ru.tensor.sbis.design.text_span.text.masked.formatter.phone

import ru.tensor.sbis.design.text_span.text.masked.formatter.simple.SimpleFormatter

/**
 * Реализация [SimpleFormatter] по умолчанию для российских номеров телефонов
 *
 * @author us.bessonov
 */
internal object DefaultRuPhoneFormatter : SimpleFormatter by createSimplePhoneFormatter()