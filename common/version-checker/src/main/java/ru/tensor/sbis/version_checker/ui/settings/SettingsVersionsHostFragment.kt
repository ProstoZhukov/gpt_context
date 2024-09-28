package ru.tensor.sbis.version_checker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.BindingAdapters.isVisibilityOrGone
import ru.tensor.sbis.common.util.addNavigationArg
import ru.tensor.sbis.common.util.doIfNavigationDisabled
import ru.tensor.sbis.common.util.isNavigationEnabled
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.insets.addTopPaddingByInsets
import ru.tensor.sbis.version_checker.R
import ru.tensor.sbis.version_checker_decl.VersionedComponent

/**
 * Хостовой фрагмент содержащий тулбар и экран настроек отладки обновлений [SettingsVersionUpdateDebugFragment]
 *
 * @author as.chadov
 */
internal class SettingsVersionsHostFragment :
    BaseFragment(),
    VersionedComponent {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(
            R.layout.versioning_fragment_host_settings,
            container,
            false
        ).apply {
            initToolbar(this)
            showSettingsFragment()
        }

    private fun showSettingsFragment() {
        var settingsFragment = childFragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG)
        settingsFragment != null && return
        settingsFragment = SettingsVersionUpdateDebugFragment()
        childFragmentManager
            .beginTransaction()
            .add(R.id.versioning_settings_fragment_container, settingsFragment, SETTINGS_FRAGMENT_TAG)
            .commit()
    }

    private fun initToolbar(rootView: View) {
        val toolbar = rootView.findViewById<SbisTopNavigationView>(R.id.versioning_sbis_toolbar)

        val needShowToolbar = arguments?.getBoolean(FRAGMENT_SHOW_TOOLBAR_BUNDLE) ?: true
        isVisibilityOrGone(toolbar, needShowToolbar)

        if (needShowToolbar) {
            toolbar.content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Value(
                    arguments?.getString(FRAGMENT_TITLE_BUNDLE) ?: getString(R.string.versioning_settings_label)
                )
            )
            toolbar.backBtn?.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            doIfNavigationDisabled(this) {
                toolbar.showBackButton = false
            }
        }
        addTopPaddingByInsets(if (needShowToolbar) toolbar else rootView)
    }

    override fun swipeBackEnabled(): Boolean =
        isNavigationEnabled(this)

    companion object {

        /** Ключ показа Toolbar-a Bundle. */
        const val FRAGMENT_SHOW_TOOLBAR_BUNDLE = "fragment_show_toolbar_bundle"

        /** Ключ заголовка Bundle. */
        const val FRAGMENT_TITLE_BUNDLE = "fragment_title_bundle"

        @Suppress("deprecated")
        /** Тэг фрагмента. */
        private val SETTINGS_FRAGMENT_TAG =
            SettingsVersionsHostFragment::class.java.simpleName + ".SETTINGS_FRAGMENT_TAG"

        /** @SelfDocumented */
        @Suppress("deprecated")
        fun newInstance(title: String?, showToolbar: Boolean, withNavigation: Boolean): Fragment {
            val args = Bundle().apply {
                putBoolean(FRAGMENT_SHOW_TOOLBAR_BUNDLE, showToolbar)

                title?.let { putString(FRAGMENT_TITLE_BUNDLE, it) }
                addNavigationArg(this, withNavigation)
            }
            return SettingsVersionsHostFragment().apply { arguments = args }
        }

        /** @SelfDocumented */
        fun newInstance(title: String?): Fragment =
            newInstance(title, showToolbar = true, withNavigation = true)
    }
}