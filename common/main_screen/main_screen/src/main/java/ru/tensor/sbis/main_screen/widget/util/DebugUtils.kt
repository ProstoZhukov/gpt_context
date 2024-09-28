package ru.tensor.sbis.main_screen.widget.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.getPendingActions
import timber.log.Timber

private const val TAG_FM_DESTROYED = "FM_DESTROYED_ISSUE"

/**
 * Залоггировать вспомогательную информацию для анализа проблемы "FragmentManager has been destroyed".
 *
 * @author us.bessonov
 */
fun logFragmentManagerDestroyedIssueAnalytics(activityFragmentManager: FragmentManager) {
    Timber.tag(TAG_FM_DESTROYED).e(getAnalyticsForFragmentsAndTheirChildren(activityFragmentManager))
}

private fun getAnalyticsForFragmentsAndTheirChildren(activityFragmentManager: FragmentManager): String {
    val fragments = activityFragmentManager.fragments
        .flatMap { listOf(it).plus(it.childFragmentManager.fragments) }
    return fragments.joinToString { it.getAnalytics() }
}

private fun Fragment.getAnalytics(): String {
    val fm = childFragmentManager
    return "[$this | state: ${lifecycle.currentState}, FM state saved: ${fm.isStateSaved}, " +
        "FM is destroyed: ${fm.isDestroyed}, activity destroyed: ${fm.isDestroyed}, fragments: ${fm.fragments}, " +
        "pending actions: ${fm.getPendingActions()}]"
}