package ru.tensor.sbis.person_decl.motivation.cadres

import io.reactivex.Observable
import ru.tensor.sbis.person_decl.motivation.cadres.ui.CadresFragmentsProvider
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Контракт модуля кадров.
 *
 * @author ra.temnikov
 */
interface CadresFeature : CadresFragmentsProvider, Feature {

    /**
     * Проверка наличия доступа для просмотра трудовой книжки сотрудника.
     * @param uuid - UUID профиля
     * @return true - доступ разрешен
     */
    fun hasAccessToEmploymentRecordbook(uuid: UUID): Observable<Boolean>

}