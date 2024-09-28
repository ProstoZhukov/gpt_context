package ru.tensor.sbis.person_decl.motivation.cadres.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Провайдинг экранов модуля "Кадры".
 *
 * @author ra.temnikov
 */
interface CadresFragmentsProvider : Feature {

    /**
     * Создание фрагмента трудовой книжки модуля "Кадры"
     * @param [personUUID] - UUID сотрудника для которого открывается трудовая книга
     * @param [userName] - ФИО сотрудника
     * @param [userImageURL] - url фотографии сотрудника
     * @param [needShowInternalToolbar] - нужно ли показывать тулбар фрагмента
     * @param [needAddDefaultTopPadding] - нужно ли добавлять паддинг под статус бар
     * @return [Fragment] который можно положить в свой контейнер
     */
    fun createEmploymentRecordbookFragment(
        personUUID: UUID,
        userName: String? = null,
        userImageURL: String? = null,
        needShowInternalToolbar: Boolean,
        needAddDefaultTopPadding: Boolean = false
    ): Fragment
}