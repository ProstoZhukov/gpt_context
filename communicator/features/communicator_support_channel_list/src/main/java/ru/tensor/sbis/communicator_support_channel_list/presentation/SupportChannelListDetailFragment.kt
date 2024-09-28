package ru.tensor.sbis.communicator_support_channel_list.presentation

import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.provider.BottomBarProvider
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.exhaustive
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SabyGetConsultationListFragmentFactory
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SabyGetOpenChatsParams
import ru.tensor.sbis.communicator.declaration.crm.contract.CRMConversationContract
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCreationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.communicator_support_channel_list.databinding.CommunicatorSupportChannelListDetailFragmentBinding
import ru.tensor.sbis.communicator_support_channel_list.di.SupportChannelListPlugin
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import ru.tensor.sbis.communicator_support_channel_list.utills.BottomBarProviderAdapter
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.ConversationSourceType
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListHostViewModel
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListViewModelFactoryFactory
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.extentions.setBottomMargin
import java.util.UUID
import javax.inject.Inject


/**
 * Фрагмент - реестр консультаций
 * Отображает в себе реестр консультаций и переписку, сам определяет, что из этого отображать
 * Используется как независимый фрагмент при отображении в режиме SupportComponentConfig.SabySupport
 * @see config
 */
internal class SupportChannelListDetailFragment : Fragment(), KeyboardEventListenerChildPropagate,
    FragmentBackPress,
    BottomBarProvider by BottomBarProviderAdapter(), CRMConversationContract {
    @Inject
    internal lateinit var supportChannelRouterFactory: SupportChannelRouterFactory

    @Inject
    internal lateinit var scrollHelper: ScrollHelper

    @Inject
    internal lateinit var supportChatListViewModelFactoryFactory: SupportChannelListViewModelFactoryFactory

    /**
     * Роутер для этого фрагмента
     */
    private val router by lazy {
        supportChannelRouterFactory.create(
            childFragmentManager,
            requireActivity() as? OverlayFragmentHolder,
            R.id.communicator_support_channel_list_fragment_container,
            DeviceConfigurationUtils.isTablet(requireContext()),
            config
        ).apply {
            initSupportFragmentManager(requireActivity().supportFragmentManager)
        }
    }

    /**
     * Обработчик действия "назад"
     */
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            childFragmentManager.popBackStack()
        }
    }

    /**
     * config
     * @see SupportComponentConfig
     */
    private val config by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(
                ARG_CONFIG,
                SupportComponentConfig::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(ARG_CONFIG)
        } ?: SupportComponentConfig.SabySupport
    }

    /**
     * Источник консультаций
     * Может отстутствовать, в таком случае будет получен с помощью viewModel.loadSupportInfoDetail()
     */
    private val sourceId: UUID? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(
                SOURCE_ID,
                ParcelUuid::class.java
            )?.uuid
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable<ParcelUuid?>(SOURCE_ID)?.uuid
        }
    }


    /**
     * Вью-модель
     * Используется та же модель, что хост-фрагментов (по типу), но свой экземпяр
     * Это позволяет избежать дублирования кода
     */
    private val viewModel: SupportChannelListHostViewModel by viewModels {
        supportChatListViewModelFactoryFactory.create(
            config,
            DeviceConfigurationUtils.isTablet(requireContext()),
            sourceId,
            consultationId
        )
    }

    /**
     * Идентфикатор консультации
     * Передается, если выбран канал с одной консультацией, или по пушу
     */
    private val consultationId: UUID? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(
                CONSULTATION_ID,
                ParcelUuid::class.java
            )?.uuid
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable<ParcelUuid?>(CONSULTATION_ID)?.uuid
        }
    }


    private var _binding: CommunicatorSupportChannelListDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private val component = SupportChannelListPlugin.component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            CommunicatorSupportChannelListDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setBottomMarginForFabSbisPanel(viewModel.isOpenConsultationListOnStart())
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        childFragmentManager.addOnBackStackChangedListener {
            onBackPressedCallback.isEnabled = childFragmentManager.backStackEntryCount > 1
        }

        childFragmentManager.addOnBackStackChangedListener {

            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.isOpenConsultationListOnStart() && childFragmentManager.backStackEntryCount == 1) {
                    scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE)
                    setPaddingForBottomNavigation(false)
                }
            }

            /*
             * Отслеживаем стек фрагментов с целью определить, показывать или нет ActionButton
             * Список фрагментов, для которых показываем ActionButton - router.destinationsWithCreateButton
             */
            if (childFragmentManager.backStackEntryCount > 0) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val entry = childFragmentManager.getBackStackEntryAt(childFragmentManager.backStackEntryCount - 1)
                    val supportNeedShowCreateActionButton = entry.name in router.destinationsWithCreateButton
                    val needShowCreateActionButton = if (viewModel.config is SupportComponentConfig.SabyGet) {
                        supportNeedShowCreateActionButton && viewModel.needShowCreationButtonInSabyGet(sourceId)
                    } else {
                        supportNeedShowCreateActionButton
                    }

                    if (needShowCreateActionButton) {
                        showCreateActionButton()
                    } else {
                        hideCreateActionButton()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle (Lifecycle.State.STARTED) {
                viewModel.openConsultationsFromChannel.collect {
                    router.openConsultations { it.second.getFragment() }
                    setUpCreateActionButton(it.first)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createConsultation.collect {

                    val needBackBtn = !isTablet && !hasAccordion()

                    if (viewModel.isOpenConsultationListOnStart() && childFragmentManager.backStackEntryCount == 1) {
                        setPaddingForBottomNavigation(true)
                    }
                    val (sourceId, useOverlay) = it
                    router.createConsultation(
                        sourceId = sourceId,
                        needBackButton = needBackBtn,
                        isSabyget = isSabyget(),
                        isBrand = isBrand(),
                        hasAccordion = hasAccordion(),
                        useOverlay = useOverlay
                    )
                    KeyboardUtils.hideKeyboard(view)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openCompanyDetail.collect {
                    router.openCompanyDetails(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openConversationInDetail.collect {
                    val isConsultationSourceType = it.conversationSourceType == ConversationSourceType.Consultation
                    val needBackBtn = (isConsultationSourceType && isTablet)
                            || !isTablet && !it.needBackButton

                    router.openConversation(
                        id = it.id,
                        needBackButton = needBackBtn,
                        needOpenKeyboard = it.conversationSourceType == ConversationSourceType.Channel,
                        isSabyget = isSabyget(),
                        isBrand = isBrand(),
                        hasAccordion = hasAccordion() && !isConsultationSourceType,
                        useOverlay = isBrand() && needBackBtn,
                        isMultyChannel = true
                    )
                    scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openCompanyDetailsCard.collect {
                    router.openCompanyDetails(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectConsultationForDetail
                    .collect {
                        router.openConversation(
                            id = it,
                            needBackButton = false,
                            isMultyChannel = false
                        )
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showViewStub.collect { stubViewContent ->
                    binding.communicatorSupportChannelListStubViewContainer.isVisible =
                        stubViewContent != null

                    stubViewContent?.let {
                        binding.communicatorSupportChannelListStubView.setContentFactory {
                            stubViewContent
                        }
                        binding.communicatorSupportChannelListStubViewToolbar?.leftIcon?.setOnClickListener {
                            activity?.onBackPressedDispatcher?.onBackPressed()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldShowNavPadding.collect { shouldShowNavPadding ->
                    if (shouldShowNavPadding && childFragmentManager.backStackEntryCount == 1) {
                        setPaddingForBottomNavigation(true)
                        scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE)
                    } else {
                        setPaddingForBottomNavigation(false)
                        scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_DOWN_FAKE)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.initViewModel()
        }
    }

    override fun onBackPressed(): Boolean {
        if (childFragmentManager.backStackEntryCount > 1) {
            childFragmentManager.popBackStack()
            return true
        }
        return false
    }

    override fun showNewConversation(params: CRMConsultationParams) {
        viewModel.createConsultation((params as CRMConsultationCreationParams).crmConsultationCase.originUuid to isBrand())
    }

    override fun openSalePointDetailCard(companyId: UUID) {
        viewModel.openCompanyDetailCard(companyId)
    }

    /**
     * Показать ActionButtons
     * Если фрагмент находится в OverlayFragmentHolder, то покажем собственный OverlayFragmentHolder
     */
    private fun showCreateActionButton() {
        bottomBarProvider().showExtraFabButton()
    }

    private fun setUpCreateActionButton(sourceId: UUID) {
        bottomBarProvider().let {
            it.setExtraFabClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.createConsultation(sourceId to false)
                }
            }
        }
    }

    /**
     * Скрыть ActionButtons
     */
    private fun hideCreateActionButton() {
        bottomBarProvider().hideExtraFabButton()
    }

    /**
     * Получить BottomBarProvider
     * Если фрагмент НЕ находится в OverlayFragmentHolder, то будет возвращено null
     */
    private fun bottomBarProvider() = this

    /**
     * Скрыть / показать отступ для ННП
     */
    private fun setPaddingForBottomNavigation(show: Boolean) {
        binding.communicatorSupportChannelListFragmentContainer.setPadding(
            0,
            0,
            0,
            if (!isTablet && show) resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.bottom_navigation_height) else 0
        )
    }

    private fun isSabyget() =
        (config as? SupportComponentConfig.SabyGet)?.isBrand == false

    private fun isBrand() =
        (config as? SupportComponentConfig.SabyGet)?.isBrand == true

    private fun hasAccordion() =
        (config as? SupportComponentConfig.SabyGet)?.hasAccordion == true

    /**
     * Поднять / опустить FAB.
     */
    private fun setBottomMarginForFabSbisPanel(isNeed: Boolean) {
        binding.communicatorSupportChannelListFabPanel.setBottomMargin(
            if (isNeed) resources.getDimensionPixelSize(R.dimen.communicator_support_channel_list_bottom_fab_margin) else 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setBottomMarginForFabSbisPanel(false)
        scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE)
        _binding = null
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        setPaddingForBottomNavigation(false)
        return super.onKeyboardOpenMeasure(keyboardHeight)
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        val isNeedPaddingNnp = viewModel.shouldShowNavPadding.value && childFragmentManager.backStackEntryCount == 1
        setPaddingForBottomNavigation(isNeedPaddingNnp)
        return super.onKeyboardCloseMeasure(keyboardHeight)
    }

    override fun setExtraFabClickListener(extraFabClickListener: View.OnClickListener?) {
        binding.communicatorSupportChannelListFab.setOnClickListener(extraFabClickListener)
    }

    override fun showExtraFabButton() {
        binding.communicatorSupportChannelListFab.show(true)
    }

    override fun hideExtraFabButton() {
        binding.communicatorSupportChannelListFab.hide(true)
    }

    companion object : SabyGetConsultationListFragmentFactory {

        /**
         * Аргумент, конфиг компонента
         */
        private const val ARG_CONFIG = "ARG_CONFIG"

        /**
         * Идентификатор канала / источника консультаций
         */
        private const val SOURCE_ID = "SOURCE_ID"

        /**
         * идентификатор переписки.
         * передается, если при создании фрагмент требуется открыть переписку
         */
        private const val CONSULTATION_ID = "CONSULTATION_ID"

        /**
         * Получить экземляр фрагмента
         */
        fun newInstance(
            config: SupportComponentConfig,
            source: UUID?,
            consultationId: UUID?
        ) =
            SupportChannelListDetailFragment().withArgs {
                putParcelable(ARG_CONFIG, config)
                source?.let {
                    putParcelable(SOURCE_ID, ParcelUuid(it))
                }
                consultationId?.let {
                    putParcelable(CONSULTATION_ID, ParcelUuid(it))
                }
            }

        override fun createSabyGetChatsListHostFragment(
            sabyGetOpenChatsParams: SabyGetOpenChatsParams
        ): Fragment {
            return newInstance(
                SupportComponentConfig.SabyGet(
                    sabyGetOpenChatsParams.showLeftPanelOnToolbar,
                    sabyGetOpenChatsParams.isBrand,
                    sabyGetOpenChatsParams.salePoint,
                    sabyGetOpenChatsParams.hasAccordion
                ),
                sabyGetOpenChatsParams.salePoint,
                null
            )
        }
    }
}