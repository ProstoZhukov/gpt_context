package ru.tensor.sbis.base_app_components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.base_app_components.databinding.FragmentSettingsMasterContainerBinding
import ru.tensor.sbis.base_app_components.settings.SettingItemsProvider
import ru.tensor.sbis.base_app_components.settings.SettingsViewModel
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationSyncState
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.checkSafe
import ru.tensor.sbis.master_detail.DetailFragmentManager
import ru.tensor.sbis.master_detail.SelectionHelper
import ru.tensor.sbis.settings_screen.contract.SettingsScreenFeature
import ru.tensor.sbis.settings_screen.contract.UpdatableContent
import ru.tensor.sbis.settings_screen.view.ProvideSettingsScreenDelegate
import ru.tensor.sbis.settings_screen_decl.SettingsScreenDelegate
import ru.tensor.sbis.auth_settings.R as RAuth
import ru.tensor.sbis.design.R as RDesign

/**
 * "Обертка" над фрагментом настроек для отображения тулбара. Осуществляет контроль навигации при показе дополнительных
 * активити и диалогов настроек.
 * Подписывается на данные [SettingsViewModel], для обновления отображаемого списка настроек.
 *
 * @author du.bykov
 */
abstract class BaseMasterContainerFragment : Fragment(),
    SettingsScreenDelegate,
    SelectionHelper {

    private val detailFragmentManager: DetailFragmentManager get() = parentFragment as DetailFragmentManager

    private var _viewHolder: FragmentSettingsMasterContainerBinding? = null
    private val viewHolder get() = _viewHolder!!

    /**
     * Нужно ли отображать [SbisTopNavigationView.dividerView]
     */
    protected open var isVisibleToolbarDivider: Boolean = false

    /**
     * Нужно ли отображать заголовок большим
     */
    protected open val isLargeTitle: Boolean = true

    /**
     * Нужно ли отображать заголовок заглушку большим
     */
    protected open val isLargeStubTitle: Boolean = true

    abstract fun getItemProvider(): SettingItemsProvider
    abstract fun getTViewModel(): SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _viewHolder = FragmentSettingsMasterContainerBinding.inflate(inflater)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            initToolbar()
        }
        return viewHolder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) showSettingsScreen()

        observeViewModel()
    }

    override fun startActivityWithIntent(getIntent: (Context) -> Intent) {
        super.startActivity(getIntent(requireContext()))
        requireActivity().overridePendingTransition(
            RDesign.anim.right_in,
            RDesign.anim.nothing
        )
    }

    override fun showFragment(getFragment: (Context) -> Fragment) {
        detailFragmentManager.showDetailFragment(getFragment(requireContext()))
    }

    override fun showFragment(
        getFragmentForFullScreen: (Context) -> Fragment,
        getFragmentForDetail: (Context) -> Fragment
    ) {
        detailFragmentManager.showDetailFragment(
            { getFragmentForFullScreen(requireContext()) },
            { getFragmentForDetail(requireContext()) })
    }

    override fun cleanSelection() {
        getFragmentAsMaster()?.cleanSelection()
    }

    override fun shouldHighlightSelectedItems() {
        getFragmentAsMaster()?.shouldHighlightSelectedItems()
    }

    private fun getFragmentAsMaster(): SelectionHelper? {
        val fragment = childFragmentManager.findFragmentById(R.id.settings_fragment_container)
        return if (fragment is SelectionHelper) fragment else null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            getTViewModel().title.collect {
                val title = if (it == null) {
                    PlatformSbisString.Res(RAuth.string.auth_settings_settings_title)
                } else {
                    PlatformSbisString.Value(it)
                }
                val navxId = if (it == null) NavxId.SETTINGS else null

                val isLarge = if (it == null) isLargeStubTitle else isLargeTitle

                viewHolder.settingsToolbar.content = if (isLarge) {
                    SbisTopNavigationContent.LargeTitle(title, navxId = navxId)
                } else {
                    SbisTopNavigationContent.SmallTitle(title, navxId = navxId)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            getTViewModel().content.collect {
                val fragment = childFragmentManager.findFragmentById(R.id.settings_fragment_container)

                checkSafe(fragment is UpdatableContent) { "Фрагмент настроек не реализует интерфейс UpdatableContent" }
                (if (fragment is UpdatableContent) fragment else null)?.updateContent(it)
            }
        }
    }

    private fun showSettingsScreen() {
        if (childFragmentManager.findFragmentById(R.id.settings_fragment_container) != null) return

        childFragmentManager
            .beginTransaction()
            .add(
                R.id.settings_fragment_container,
                createSettingsFragment()
            )
            .commit()
    }

    /** @SelfDocumented */
    fun setToolbarBackButtonVisibility(isVisible: Boolean) {
        viewHolder.settingsToolbar.showBackButton = isVisible
    }

    /** @SelfDocumented */
    fun setToolbarBackButtonCallback(onClick: (View) -> Unit) =
        viewHolder.settingsToolbar.backBtn?.setOnClickListener(onClick)

    private fun createSettingsFragment(): Fragment {
        return SettingsScreenFeature().getFragment(
            provideDelegate = ProvideSettingsScreenDelegate()
        )
    }

    private suspend fun initToolbar() {
        viewHolder.settingsToolbar.isDividerVisible = isVisibleToolbarDivider
        getTViewModel().progress.collect {
            viewHolder.settingsToolbar.syncState = if (it)
                SbisTopNavigationSyncState.Running
            else
                SbisTopNavigationSyncState.NotRunning

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewHolder = null
    }
}
