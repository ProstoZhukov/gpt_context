package ru.tensor.sbis.edo_decl.passage.mass_passage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.passage.PassageComponent
import ru.tensor.sbis.edo_decl.passage.config.PassageConfig
import ru.tensor.sbis.edo_decl.passage.config.PassageDocumentIds
import ru.tensor.sbis.edo_decl.passage.mass_passage.creator.MassPassagesOperationCreator
import ru.tensor.sbis.mobile.docflow.generated.MassPassagesOperation

/**
 * Тип выполнения массовых переходов
 *
 * @author sa.nikitin
 */
sealed interface MassPassagesExecutionType : Parcelable {

    /**
     * Выполнение массовых переходов через [MassPassagesOperation]
     *
     * @property massPassagesOperationCreator   Фабрика [MassPassagesOperation]
     * @property docListFragmentFactory         Фабрика фрагмента списка обрабатываемых документов
     */
    @Parcelize
    class ByOperation(
        val massPassagesOperationCreator: MassPassagesOperationCreator,
        val docListFragmentFactory: MassPassagesDocListFragmentFactory
    ) : MassPassagesExecutionType

    /**
     * Выполнение массовых переходов последовательно через компонент одиночного перехода
     * См. [PassageComponent] и его конфиг [PassageConfig]
     *
     * @property docType                    [PassageDocumentIds.type]
     * @property ids                        Списки пар: [PassageDocumentIds.id] - [PassageDocumentIds.activeEventId]
     * @property docListFragmentFactory     Фабрика фрагмента списка обрабатываемых документов
     */
    @Parcelize
    class ByIds(
        val docType: String,
        val ids: List<Pair<DocumentId, ActiveEventId?>>,
        val docListFragmentFactory: MassPassagesDocListFragmentFactory?
    ) : MassPassagesExecutionType
}