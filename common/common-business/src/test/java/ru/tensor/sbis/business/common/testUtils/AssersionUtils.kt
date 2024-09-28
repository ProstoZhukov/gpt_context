package ru.tensor.sbis.business.common.testUtils

/**
 * Проверяет, что тип объекта равен типу дженерика(этой функции).
 * Если не равен, выводит наглядное сообщение об ошибке
 */
@Throws(AssertionError::class)
inline fun <reified T> assertType(value: Any?) {
    if (value == null) {
        throw AssertionError("Expected type '${T::class.java.name}' but actual type is 'null'")
    }
    if (value !is T) {
        throw AssertionError("Expected type '${T::class.java.name}' but actual type is '${value::class.java.name}'")
    }
}