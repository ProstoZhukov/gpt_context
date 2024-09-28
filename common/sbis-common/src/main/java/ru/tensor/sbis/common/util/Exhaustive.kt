package ru.tensor.sbis.common.util

/**
 * Свойство-расширение на объект любого типа
 * Следует использовать на результате when для гарантии предупреждения «'when' expression must be exhaustive...»
 * Если результат when не используется, то нет проверки наличия всех значений из ограниченного набора,
 * например, значений enum или sealed class
 * Обращение к этому свойству на результате when исправит проблему
 *
 * @author sa.nikitin
 */
inline val <T> T.exhaustive: T get() = this