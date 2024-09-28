package ru.tensor.sbis.verification_decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

interface LoginSettingsInterface : Feature {

    /**
     * Создание фрагмента смены пароля для настроек
     * @return фрагмент смены пароля
     */
    fun getChangePasswordFragment(userId: Int): Fragment

    /**
     * Создание фрагмента, используемого для изменения настроек входа
     * @return фрагмент с настройками входа
     */
    fun getLoginSettingsFragment(
        userId: Int,
        withNavigation: Boolean = true,
        withChangePassword: Boolean = true
    ): Fragment

    interface Provider {
        fun getLoginSettingsInterface(): LoginSettingsInterface
    }
}