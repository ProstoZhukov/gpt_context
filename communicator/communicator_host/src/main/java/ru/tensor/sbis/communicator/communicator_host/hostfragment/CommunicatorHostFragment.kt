package ru.tensor.sbis.communicator.communicator_host.hostfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.provider.BottomBarProvider
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.communicator.CommunicatorMasterDetailFragment
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.common.R
import ru.tensor.sbis.communicator.common.data.model.CommunicatorHostFragmentFactory
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorHostRegistrySaver
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorHostRouter
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorThemesRouter
import ru.tensor.sbis.communicator.common.themes_registry.CHAT_TYPE_ID
import ru.tensor.sbis.communicator.common.themes_registry.ConversationOpener
import ru.tensor.sbis.communicator.common.themes_registry.DIALOG_TYPE_ID
import ru.tensor.sbis.communicator.common.themes_registry.RegistryDeeplinkActionNode
import ru.tensor.sbis.communicator.common.themes_registry.ThemesRegistry
import ru.tensor.sbis.communicator.common.ui.hostfragment.contracts.FabKeeper
import ru.tensor.sbis.communicator.common.ui.hostfragment.foldable.FoldableConfigurationChangeDelegate
import ru.tensor.sbis.communicator.common.ui.hostfragment.foldable.FoldableHostConfig
import ru.tensor.sbis.communicator.common.ui.hostfragment.foldable.FoldableState
import ru.tensor.sbis.communicator.common.ui.hostfragment.foldable.FoldableState.FOLDED
import ru.tensor.sbis.communicator.common.ui.hostfragment.foldable.FoldableState.UNFOLDED
import ru.tensor.sbis.communicator.common.ui.hostfragment.foldable.FoldableStateChangeListener
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.requireCastTo
import ru.tensor.sbis.communicator.communicator_host.CommunicatorHostFacade.communicatorHostDependency
import ru.tensor.sbis.communicator.communicator_host.CommunicatorHostPlugin.customizationOptions
import ru.tensor.sbis.communicator.declaration.MasterFragment
import ru.tensor.sbis.communicator.declaration.crm.contract.CRMConversationContract
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.ParcelableDeeplinkAction
import ru.tensor.sbis.deeplink.SerializableDeeplinkAction
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.navigation.util.ActiveTabOnClickListener
import ru.tensor.sbis.design.navigation.util.NavTabSelectionListener
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.stubview.ResourceAttributeStubContent
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.toolbox_decl.navigation.NavigationItemHostScreen
import java.util.UUID
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.core.R as RCommunicatorCore
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.stubview.R as RStubView

/**
 * Фрагмент для хостинга всех реестров модуля коммуникатор,
 * включающий в себя один из трех реестров: диалогов/чатов, контактов или сотрудников,
 * а также содержит в планшетной верстке details контейнер и собственный навигационный Fab
 *
 * @author vv.chekurda
 */
internal class CommunicatorHostFragment : Fragment(),
    CommunicatorHostRouter by communicatorHostDependency.getCommunicatorHostRouter(),
    FoldableConfigurationChangeDelegate by FoldableConfigurationChangeDelegate.NEW_INSTANCE,
    ConversationOpener,
    FragmentBackPress,
    FabKeeper,
    KeyboardEventListener,
    CommunicatorMasterDetailFragment,
    CommunicatorHostRegistrySaver,
    ActiveTabOnClickListener,
    NavTabSelectionListener,
    NavigationItemHostScreen,
    CRMConversationContract {

    override val containerId: Int = View.NO_ID

    override var navxId: String = StringUtils.EMPTY

    /**
     * Т.к. на планшете используется своя Fab кнопка, то требуется знать откуда мы получили кнопку.
     * Если кнопка получена из activity, то требуется вызывать методы [BottomBarProvider],
     * чтобы не затереть дефолтный обработчик клика по кнопке в activity.
     */
    private var isActivityFab = false
    private var bottomBarProvider: BottomBarProvider? = null
    private var fab: SbisRoundButton? = null
    override val fabId get() = fab!!.id

    private var currentRegistry: Fragment? = null
        get() = if (field?.mustBeCurrentRegistry() == true) {
            field
        } else {
            val mustBeCurrentRegistry = childFragmentManager.fragments.find { it.mustBeCurrentRegistry() }
            field = mustBeCurrentRegistry
            mustBeCurrentRegistry
        }

    // Для переопределения удобно использовать метод CommunicatorRouterImpl.changeRegistry(registryType)
    override var detailsStubViewText: String? = null
        set(value) {
            if (value == field) return
            field = value
            if (value != null) setStubViewText(value)
        }

    private val lastOpenedFragment: Fragment?
        get() = overlayDetailFragment
            ?: childFragmentManager.fragments.findLast { !it.isHidden }

    private val overlayDetailFragment: Fragment?
        get() = overlayDetailContainer?.let {
            activity?.supportFragmentManager?.findFragmentById(it.id)
        }

    private val requiredRegistryType by lazy {
        requireArguments().getSerializable(REGISTRY_ARG) as CommunicatorRegistryType
    }

    private var currentRegistryType: CommunicatorRegistryType? = null
    private var overlayDetailContainer: ViewGroup? = null
    private var isCustomOverlayAdded = false

    private var isTablet = false
    private var fabClickListener: (() -> Unit)? = null
    private var isFabForcedHidden: Boolean = false

    private fun Fragment.mustBeCurrentRegistry(): Boolean = !isHidden && this is MasterFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCommunicatorRouter(this)
        placeRequiredRegistry(savedInstanceState)
    }

    private fun placeRequiredRegistry(savedInstanceState: Bundle?) {
        currentRegistryType = savedInstanceState?.getSerializable(REGISTRY_ARG) as? CommunicatorRegistryType
        val registryType = currentRegistryType ?: requiredRegistryType
        if (savedInstanceState == null || customizationOptions.navigationWithCachedFragment) changeRegistry(registryType)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomBarProvider) bottomBarProvider = context
        isTablet = DeviceConfigurationUtils.isTablet(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(RCommunicatorCore.layout.communicator_fragment_host, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onCreateFoldableHostFragment(
            FoldableHostConfig(
                fragmentManager = childFragmentManager,
                context = requireContext(),
                savedInstanceState = savedInstanceState,
                masterContainerId = RCommunicatorCore.id.communicator_master_fragment_container,
                detailContainerId = RCommunicatorCore.id.communicator_details_fragment_container,
                listener = object : FoldableStateChangeListener {
                    override fun onFoldableStateChanged(newState: FoldableState, withFragmentTransfers: Boolean) {
                        when {
                            !withFragmentTransfers -> return
                            newState == FOLDED -> {
                                forceHideFab()
                                changeNavigationBottomMenuVisibility(isVisible = false)
                            }
                            newState == UNFOLDED -> {
                                restoreFabVisibility()
                                changeNavigationBottomMenuVisibility(isVisible = true)
                            }
                            else -> Unit
                        }
                    }
                }
            )
        )
        addOverlayDetailContainer()
        initFab(view)
        setStubViewText(
            detailsStubViewText
                ?: requireContext().getString(RStubView.string.design_stub_view_split_view_container_details))
    }

    override fun onHiddenChanged(hidden: Boolean) {
        lastOpenedFragment?.onHiddenChanged(hidden)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveFoldableHostFragmentState(outState)
        outState.putSerializable(REGISTRY_ARG, currentRegistryType)
    }

    /**
     * Принудительное скрытие FAB с сохранением слушателя для foldable сценариев
     */
    private fun forceHideFab() {
        isFabForcedHidden = true
        if (isActivityFab) {
            bottomBarProvider?.setNavigationFabClickListener(null)
        } else {
            fab?.setOnClickListener(null)
            fab?.hide()
            fab = null
        }
    }

    /**
     * Восстановление состояния FAB после принудительного скрытия [forceHideFab]
     */
    private fun restoreFabVisibility() {
        isFabForcedHidden = false
        setFabClickListener(fabClickListener)
    }

    override fun showNewConversation(params: CRMConsultationParams) {
        if (currentRegistry != null && currentRegistry is ThemesRegistry) {
            (currentRegistry as CommunicatorThemesRouter).showConsultationDetailsScreen(params, null)
        }
    }

    override fun openSalePointDetailCard(companyId: UUID) = Unit

    /**
     * Управление видимостью ННП для foldable сценариев
     *
     * @param isVisible true, если сделать ННП видимой
     */
    private fun changeNavigationBottomMenuVisibility(isVisible: Boolean) {
        CommunicatorCommonComponent.getInstance(requireContext())
            .scrollHelper
            .sendFakeScrollEvent(
                if (isVisible) ScrollEvent.SCROLL_UP_FAKE
                else ScrollEvent.SCROLL_DOWN_FAKE
            )
    }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        val registry = currentRegistry
        if (registry is RegistryDeeplinkActionNode && registry.isRegistryDeeplinkAction(args)) {
            registry.onNewDeeplinkAction(args)
        } else {
            handleDeeplinkAction(args)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeOverlayDetailContainer()
        detachFab()
    }

    override fun onDestroy() {
        super.onDestroy()
        detachCommunicatorRouter()
        onDestroyFoldableHostFragment()
    }

    override fun onAttachFragment(attachedFragment: Fragment) {
        if (!customizationOptions.navigationWithCachedFragment) DeeplinkActionNode.performNewDeeplinkAction(this)
    }

    override fun resetStateForNewData(
        selectedConversationUuid: UUID?,
        selectedMessageUuid: UUID?,
        resetTypeIfUnanswered: Boolean
    ) {
        currentRegistry?.castTo<ConversationOpener>()?.resetStateForNewData(selectedConversationUuid, selectedMessageUuid)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int) =
        reportKeyboardEventToOpenedFragments(keyboardHeight, isOpen = true)

    override fun onKeyboardCloseMeasure(keyboardHeight: Int) =
        reportKeyboardEventToOpenedFragments(keyboardHeight, isOpen = false)

    private fun reportKeyboardEventToOpenedFragments(keyboardHeight: Int, isOpen: Boolean): Boolean {
        currentRegistry.reportKeyboardEvent(keyboardHeight, isOpen)
        lastOpenedFragment.reportKeyboardEvent(keyboardHeight, isOpen)
        return true
    }

    override fun saveRegistry(registryType: CommunicatorRegistryType) {
        currentRegistryType = registryType
    }

    private fun Fragment?.reportKeyboardEvent(keyboardHeight: Int, isOpen: Boolean) {
        this?.castTo<KeyboardEventListener>()?.let {
            if (isOpen) {
                it.onKeyboardOpenMeasure(keyboardHeight)
            } else {
                it.onKeyboardCloseMeasure(keyboardHeight)
            }
        }
    }

    override fun onBackPressed(): Boolean =
        if (delegateBackActionToContentFragment()) true
        else popBackStack()

    private fun delegateBackActionToContentFragment(): Boolean =
        (lastOpenedFragment?.castTo<FragmentBackPress>()?.onBackPressed() == true).let {
            if (it.not() && childFragmentManager.backStackEntryCount == 0 && overlayDetailFragment == null) {
                currentRegistry?.castTo<FragmentBackPress>()?.onBackPressed() == true
            } else it
        }

    private fun initFab(rootView: View) {
        fab = rootView.findViewById(RCommunicatorCore.id.communicator_navigation_fab)
        if (fab == null) {
            fab = requireActivity().findViewById(RCommunicatorDesign.id.fab)
            isActivityFab = true
        }
    }

    private fun setStubViewText(text: String) {
        if (!isTablet) return
        view?.findViewById<StubView>(RCommunicatorCore.id.communicator_details_fragment_stub)?.apply {
            setContent(ResourceAttributeStubContent(ResourcesCompat.ID_NULL, null, text, emptyMap()))
        }
    }

    private fun detachFab() {
        fab = null
        fabClickListener = null
    }

    override fun setFabClickListener(navigationFabClickListener: (() -> Unit)?) {
        if (isHidden) return
        fabClickListener = navigationFabClickListener
        if (navigationFabClickListener != null && !isFabForcedHidden) {
            if (isActivityFab) {
                bottomBarProvider?.setNavigationFabClickListener { navigationFabClickListener() }
            } else {
                fab?.setOnClickListener { navigationFabClickListener() }
                fab?.show()
            }
        } else {
            if (isActivityFab) {
                bottomBarProvider?.setNavigationFabClickListener(null)
            } else {
                fab?.setOnClickListener(null)
                fab?.hide()
            }
        }
    }

    override fun showDetailFragment(fragment: Fragment) {
        if (isTablet) setSubContent(fragment)
    }

    override fun removeDetailFragment() {
        if (isTablet) removeSubContent()
    }

    private fun addOverlayDetailContainer() {
        val registryType = currentRegistryType ?: requiredRegistryType
        if (!registryType.isSupportsOverlayDetailContainer(isTablet)) return
        val activity = requireActivity()
        val containerId = R.id.communicator_overlay_detail_container_id
        overlayDetailContainer = activity.findViewById(containerId)
            ?: FrameLayout(requireContext()).also {
                it.id = containerId
                it.fitsSystemWindows = true
                if (!isTablet && !resources.getBoolean(RCommon.bool.is_landscape)) {
                    it.updatePadding(top = StatusBarHelper.getStatusBarHeight(context))
                }
                it.measureAllChildren = true
                activity.addContentView(it, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
                isCustomOverlayAdded = true
            }
    }

    private fun removeOverlayDetailContainer() {
        if (isCustomOverlayAdded) {
            overlayDetailContainer?.let {
                it.parent.requireCastTo<ViewGroup>().removeView(it)
                overlayDetailContainer = null
            }
        }
    }

    companion object : CommunicatorHostFragmentFactory {

        @JvmStatic
        override fun createCommunicatorHostFragment(
            registry: CommunicatorRegistryType,
            action: DeeplinkAction?
        ): Fragment =
            CommunicatorHostFragment().withArgs {
                putSerializable(REGISTRY_ARG, registry)
                when (registry) {
                    is CommunicatorRegistryType.DialogsRegistry -> putSerializable(DIALOG_TYPE_ID, registry.dialogType)
                    is CommunicatorRegistryType.ChatsRegistry   -> putSerializable(CHAT_TYPE_ID, registry.chatType)
                    else                                        -> Unit
                }
                when(action) {
                    is ParcelableDeeplinkAction -> {
                        DeeplinkActionNode.putNewDeeplinkActionToArgsIfNotNull(this, action)
                    }
                    is SerializableDeeplinkAction -> {
                        DeeplinkActionNode.putNewDeeplinkActionToArgsIfNotNull(this, action)
                    }
                    null -> {
                        // nop
                    }
                    else -> {
                        throw IllegalArgumentException("$action не может быть сохранен в bundle.")
                    }
                }
            }
    }

    override fun onActiveTabClicked(item: NavigationItem) {
        currentRegistry?.castTo<ActiveTabOnClickListener>()?.onActiveTabClicked(item)
    }

    override fun changeSelection(isSelected: Boolean) {
        currentRegistry?.castTo<NavTabSelectionListener>()?.changeSelection(isSelected)
    }
}

private const val REGISTRY_ARG = "COMMUNICATOR_REGISTRY_TYPE"
