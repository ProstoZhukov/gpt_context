package ru.tensor.sbis.video_monitoring_decl.registry

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для получения экранов, используемых для наивгации в BasicMainScreenView.
 */
interface TopNavigationEntryPointProvider : Feature {

    val notificationPersistentUniqueIdentifier: String

    val profilePersistentUniqueIdentifier: String

    /**
     * Создать экран уведомлений.
     */
    fun createNotificationsScreen(): Fragment

    /**
     * Создать экран профиля (настроек).
     */
    fun createProfileScreen(): Fragment
}