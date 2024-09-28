package ru.tensor.sbis.link_opener.domain.builder

/**
 * Билдер роутинга компонента открытия ссылок.
 *
 * @author as.chadov
 */
@LinkOpenerDslBuilderMarker
internal interface BaseLinkOpenDslBuilder<T> {
    /** @SelfDocumented */
    fun build(): T
}

/**
 * Аннотация для отметки о принадлежности к DSL построения события открытия ссылки.
 *
 * @author as.chadov
 */
@DslMarker
internal annotation class LinkOpenerDslBuilderMarker
