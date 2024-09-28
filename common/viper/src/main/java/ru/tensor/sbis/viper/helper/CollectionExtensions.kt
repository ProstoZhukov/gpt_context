package ru.tensor.sbis.viper.helper

/**
 * Расширение для определения, одинаковое ли содержимое в двух списках.
 *
 * @param collection список, с которым надо сравнить текущий список.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
infix fun <T> Collection<T>.isSameContent(collection: Collection<T>) =
    collection.let { this.size == it.size && this.containsAll(it) }