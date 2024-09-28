package ru.tensor.sbis.info_decl.notification

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для получения фрагмента настроек уведомлений
 */
interface NotificationSettingsFragmentProvider : Feature {

    /**
     * Возвращает [Fragment] настроек уведомлений
     */
    fun getNotificationSettingsFragment(withNavigation: Boolean = true): Fragment
}