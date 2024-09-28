package ru.tensor.sbis.version_checker_decl

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Открытие окна обновления для приложения Демо.
 */
interface VersioningDemoOpener {

    /**
     * Открыть шторку рекомендуемого обновления в [fragmentManager].
     */
    fun openRecommendedUpdateFragment(fragmentManager: FragmentManager)

    /**
     * Поставщик реализации [VersioningDemoOpener].
     */
    interface Provider : Feature {
        val versioningDemoOpener: VersioningDemoOpener
    }
}