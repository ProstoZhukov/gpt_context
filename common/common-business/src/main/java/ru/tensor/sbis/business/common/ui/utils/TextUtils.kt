package ru.tensor.sbis.business.common.ui.utils

/**
 * Проверяет, является ли цифрой символ строки после последней точки
 *
 * @param string проверяемая строка
 * @param pointPosition позиция последнего вхождения символа '.'
 * @return является ли цифрой символ строки после последней точки (если она имеется)
 */
fun isCharacterAfterPointDigit(string: String, pointPosition: Int = string.lastIndexOf('.')): Boolean {
    return pointPosition > -1 && pointPosition + 1 < string.length && Character.isDigit(string[pointPosition + 1])
}