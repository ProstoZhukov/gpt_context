package ru.tensor.sbis.edo_decl.doc_opener

import ru.tensor.sbis.edo_decl.doc_opener.card.factory.AppliedDocCardFactory
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Реестр opener-а документов
 * Используется для хранения фабрик прикладных карточек документов
 *
 * @author sa.nikitin
 */
interface DocOpenerRegistry : Feature {

    /**
     * Зарегистрировать фабрику прикладной карточки документа [factory]
     */
    fun registerAppliedDocCardFactory(factory: AppliedDocCardFactory)
}