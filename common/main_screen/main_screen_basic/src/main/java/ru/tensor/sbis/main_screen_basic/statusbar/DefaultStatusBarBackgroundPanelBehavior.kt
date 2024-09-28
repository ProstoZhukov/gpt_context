package ru.tensor.sbis.main_screen_basic.statusbar

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.main_screen_decl.basic.data.OverlayStatusBarBackgroundPanelBehavior

internal class DefaultStatusBarBackgroundPanelBehavior : OverlayStatusBarBackgroundPanelBehavior {

    override fun onFragmentViewAdded(
        statusBarBackgroundPanel: View,
        fragment: Fragment,
        fragmentManager: FragmentManager,
        hasMainContent: Boolean
    ) {
        if (fragmentManager.hasFragmentsOnTop(hasMainContent)) {
            statusBarBackgroundPanel.isVisible = true
        }
    }

    override fun onFragmentViewRemoved(
        statusBarBackgroundPanel: View,
        fragment: Fragment,
        fragmentManager: FragmentManager,
        hasMainContent: Boolean
    ) {
        if (!fragmentManager.hasFragmentsOnTop(hasMainContent)) {
            statusBarBackgroundPanel.isVisible = false
        }
    }

    private fun FragmentManager.hasFragmentsOnTop(hasMainContent: Boolean): Boolean {
        val count = fragments.count { it.isVisible }.run {
            if (hasMainContent) this - 1 else this
        }
        return count > 0
    }

}