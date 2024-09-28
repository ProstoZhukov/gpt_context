package ru.tensor.sbis.edo_decl.passage.mass_passage

import ru.tensor.sbis.edo_decl.passage.config.PassageDocumentIds
import java.util.UUID

typealias DocumentId = UUID
typealias ActiveEventId = UUID
typealias RegulationId = UUID
typealias PhaseId = UUID
typealias ErrorMessage = String

/**
 * Фильтр для списка документов во время массовых переходов
 *
 * @author sa.nikitin
 */
sealed interface MassPassagesDocListFilter {

    /**
     * Пустой фильтр
     * Используется, когда не нужно отображать список документов
     *
     * @author sa.nikitin
     */
    object Empty : MassPassagesDocListFilter

    /**
     * Фильтр по парам: [PassageDocumentIds] - Сообщение об ошибке перехода
     *
     * Сообщение об ошибке будет только при отображении списка документов, переход по которым не удалось выполнить
     *
     * @author sa.nikitin
     */
    data class ByIds(val ids: List<Pair<PassageDocumentIds, ErrorMessage?>>) : MassPassagesDocListFilter

    /**
     * Фильтр по группам документов, определяемых парами идентификаторов регламента и этапа
     *
     * @author sa.nikitin
     */
    data class ByGroupIds(val ids: List<Pair<RegulationId, PhaseId>>) : MassPassagesDocListFilter
}