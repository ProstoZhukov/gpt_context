package ru.tensor.sbis.person_decl.profile

import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.person_decl.profile.model.Person
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Предоставляет [Observable] для получения данных персоны
 *
 * @author ra.temnikov
 */
interface ProfileRepository : Feature {

    /**
     * Возвращает [Observable] с данными персоны с синхронизацией
     */
    fun getProfileInfo(profileUuid: UUID): Observable<Person>

    /**
     * Возвращает [Observable] с данными персоны из кэша
     */
    fun getProfileInfoFromCache(profileUuid: UUID): Single<Person>
}