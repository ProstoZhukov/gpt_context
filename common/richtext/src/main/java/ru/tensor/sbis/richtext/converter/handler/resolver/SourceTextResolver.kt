package ru.tensor.sbis.richtext.converter.handler.resolver

/**
 * Резолвер, выполняющийся перед потоковой обработкой исходного текста.
 *
 * @author am.boldinov
 */
internal interface SourceTextResolver {

    /**
     * Исправляет входное значение текста для корректного отображения.
     *
     * @param text исходный markup-текст
     */
    fun resolve(text: String): String
}