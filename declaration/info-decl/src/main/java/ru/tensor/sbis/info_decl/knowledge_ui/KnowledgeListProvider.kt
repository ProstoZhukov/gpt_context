package ru.tensor.sbis.info_decl.knowledge_ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс-поставщик фрагмента реестра баз знаний
 *
 * @author sr.golovkin on 30.08.2020
 */
interface KnowledgeListProvider : Feature {

    /**
     * Предоставляет фрагмент реестра баз знаний
     */
    fun getKnowledgeBaseListFragment(
        configuration: KnowledgeBaseRegistryConfiguration = KnowledgeBaseRegistryConfiguration()
    ): Fragment

    /**
     * Предоставляет фрагмент реестра с избранными базами и статьями
     */
    fun getKnowledgeBaseListFavoriteFragment(): Fragment
}