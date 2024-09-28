package ru.tensor.sbis.version_checker_decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс активатора отладки версионирования
 *
 * @author as.chadov
 */
interface VersioningDebugActivator : Feature {

    /**
     * Создать фрагмент отладки версионирования
     */
    fun createVersioningDebugFragment(
        withNavigation: Boolean,
        title: String? = null,
        showToolbar: Boolean = true
    ): Fragment

    /**
     * Поставщик реализации [VersioningDebugActivator]
     */
    interface Provider : Feature {
        val versioningDebugActivator: VersioningDebugActivator
    }
}