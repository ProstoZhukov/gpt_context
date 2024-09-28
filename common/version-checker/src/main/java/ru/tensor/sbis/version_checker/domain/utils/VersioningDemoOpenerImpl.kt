package ru.tensor.sbis.version_checker.domain.utils

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.version_checker.ui.recommended.RecommendedUpdateFragment
import ru.tensor.sbis.version_checker_decl.VersioningDemoOpener

/**
 * Реализация открытия окна обновления для приложения Демо.
 */
internal class VersioningDemoOpenerImpl : VersioningDemoOpener {
    override fun openRecommendedUpdateFragment(fragmentManager: FragmentManager) {
        RecommendedUpdateFragment.newInstance().show(fragmentManager, RecommendedUpdateFragment.screenTag)
    }
}