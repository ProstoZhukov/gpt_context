package ru.tensor.sbis.edo_decl.passage.mass_passage

import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Поставщик фильтра для списка документов во время массовых переходов
 *
 * @author sa.nikitin
 */
interface MassPassagesDocListFilterProvider : Feature {

    /**
     * Получить фильтр для списка документов согласно идентификатору сессии массовых переходов
     */
    fun getDocListFilter(sessionKey: UUID): StateFlow<MassPassagesDocListFilter>
}