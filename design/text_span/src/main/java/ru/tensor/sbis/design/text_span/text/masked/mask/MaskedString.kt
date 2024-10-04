package ru.tensor.sbis.design.text_span.text.masked.mask

/**
 * Модель маскирующей строки
 *
 * @author ma.kolpakov
 * Создан 2/20/2019
 */
interface MaskedString : CharSequence {
    /**
     * Восстановить символы по указанной строке с сохранением позиции и игнорированием, если символ
     * в позиции не прошёл проверку [MaskSymbol.matches]
     */
    fun restore(input: CharSequence)

    /**
     * Проверить корректность позиции и если нужно - исправить ее.
     */
    fun validatePosition(position: Int): Int

    /**
     * Вставить указанную строку начиная с указанной позиции.
     */
    fun insert(start: Int, input: CharSequence): Int

    /**
     * Удалить символы из диапазона, заданного позицией первого символа [start] и количеством символов [count].
     */
    fun delete(start: Int, count: Int, atLeastOne: Boolean = false): Int

    /**
     * Очистить строку.
     */
    fun clear(): Int
}