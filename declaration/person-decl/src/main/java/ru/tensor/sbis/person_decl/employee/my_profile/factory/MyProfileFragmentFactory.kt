package ru.tensor.sbis.person_decl.employee.my_profile.factory

import androidx.fragment.app.Fragment
import ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.ProfileConfiguration
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента моего профиля
 *
 * @author ra.temnikov
 */
interface MyProfileFragmentFactory : Feature {

    /**
     * Создать экземпляр фрагмента моего профиля
     *
     * @param withNavigation необходимо ли наличие навигационных кнопок на фрагменте
     * @param configuration нештатная конфигурация карточки
     * @return новый экземпляр фрагмента
     */
    fun createMyProfileFragment(withNavigation: Boolean = true, configuration: ProfileConfiguration? = null): Fragment
}