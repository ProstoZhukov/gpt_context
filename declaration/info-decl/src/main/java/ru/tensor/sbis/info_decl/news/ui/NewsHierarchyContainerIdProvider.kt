package ru.tensor.sbis.info_decl.news.ui

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет идентификатора контейнера для вставки иерархии экранов модуля новостей.
 *
 * @author am.boldinov
 */
interface NewsHierarchyContainerIdProvider : Feature {

    /**
     * Идентификатор контейнера, в который вставляется иерархия фрагментов модуля новостей.
     */
    val containerFrameId: Int
}