package ru.tensor.sbis.person_decl.employee.my_profile.factory

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.ProfileConfiguration
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания [Intent] активности моего профиля
 *
 * @author ra.temnikov
 */
interface MyProfileIntentFactory : Feature {

    /**
     * Создать [Intent] активности моего профиля
     *
     * @param context контекст
     * @param configuration нештатная конфигурация карточки
     */
    fun createMyProfileIntent(context: Context, configuration: ProfileConfiguration? = null): Intent
}