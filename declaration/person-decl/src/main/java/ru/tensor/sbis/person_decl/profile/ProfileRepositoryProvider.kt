package ru.tensor.sbis.person_decl.profile

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик [ProfileRepository]
 *
 * @author us.bessonov
 */
interface ProfileRepositoryProvider : Feature {

    /** @SelfDocumented */
    val profileRepository: ProfileRepository
}