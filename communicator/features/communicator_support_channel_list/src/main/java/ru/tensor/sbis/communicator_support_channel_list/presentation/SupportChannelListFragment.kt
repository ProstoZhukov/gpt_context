package ru.tensor.sbis.communicator_support_channel_list.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.runOnUiThread
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.communicator_support_channel_list.data.ChannelCollectionCrud3ServiceWrapper
import ru.tensor.sbis.communicator_support_channel_list.data.ChannelCollectionCrud3ServiceWrapperImplFactory
import ru.tensor.sbis.communicator_support_channel_list.databinding.CommunicatorSupportChannelListFragmentBinding
import ru.tensor.sbis.communicator_support_channel_list.di.SupportChannelListPlugin
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import ru.tensor.sbis.communicator_support_channel_list.feature.controllerSupportChatsType
import ru.tensor.sbis.communicator_support_channel_list.mapper.ChannelClickCommandInMaster
import ru.tensor.sbis.communicator_support_channel_list.mapper.SupportChannelsMapperFactory
import ru.tensor.sbis.communicator_support_channel_list.utills.LastItemBottomMarginDecoration
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListHostViewModel
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListViewModelFactoryFactory
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegate
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateImpl
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateParams
import ru.tensor.sbis.design.utils.insets.IndentType
import ru.tensor.sbis.design.utils.insets.Position
import ru.tensor.sbis.design.utils.insets.ViewToAddInset
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.master_detail.SelectionHelper
import ru.tensor.sbis.service.generated.StubType
import javax.inject.Inject
import javax.inject.Provider
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RDesign

/**
 * Фрагмент - реестр каналов
 */
internal class SupportChannelListFragment : Fragment(),
    SelectionHelper,
    DefaultViewInsetDelegate by DefaultViewInsetDelegateImpl() {

    /**
     * @see ChannelCollectionCrud3ServiceWrapper
     */
    @Inject
    internal lateinit var supportChannelsCollectionProvider: ChannelCollectionCrud3ServiceWrapperImplFactory

    /**
     * @see ru.tensor.sbis.crud3.ListComponentView.inject
     */
    @Inject
    internal lateinit var supportChatsMapperFactory: Provider<SupportChannelsMapperFactory>

    @Inject
    internal lateinit var supportChatListViewModelFactoryFactory: SupportChannelListViewModelFactoryFactory

    private var _binding: CommunicatorSupportChannelListFragmentBinding? = null
    private val binding get() = _binding!!

    /**
     * Вью-модель
     */
    private val hostViewModel: SupportChannelListHostViewModel by viewModels(ownerProducer = { getHostFragment() })

    private val component = SupportChannelListPlugin.component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CommunicatorSupportChannelListFragmentBinding.inflate(inflater, container, false)
        if (hostViewModel.config is SupportComponentConfig.SabyGet) {
            initInsetListener(
                DefaultViewInsetDelegateParams(
                    listOf(
                        ViewToAddInset(
                            binding.root.findViewById<SbisTopNavigationView>(R.id.communicator_support_channel_list_top_navigation_view),
                            listOf(IndentType.PADDING to Position.TOP)
                        )
                    )
                )
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapper =
            supportChatsMapperFactory.get().create(
                ListDateFormatter.MixedListDateFormatter(requireContext()),
                ChannelClickCommandInMaster(hostViewModel)
            )

        binding.communicatorSupportChannelListTopNavigationView.apply {
            val (title, navxId) =  if (hostViewModel.config is SupportComponentConfig.SabyGet) {
                R.string.communicator_support_channel_sabyget_title to null
            } else {
                R.string.communicator_support_channel_list_title to NavxId.SUPPORT
            }
            content = SbisTopNavigationContent.LargeTitle(PlatformSbisString.Res(title), navxId = NavxId.SUPPORT)
            if (!(DeviceConfigurationUtils.isTablet(requireContext()) || hostViewModel.config.showLeftPanelOnToolbar)) {
                showBackButton = true
                backBtn?.setOnClickListener {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
            }
        }

        binding.communicatorSupportChannelList.inject(
            this,
            lazy {
                supportChannelsCollectionProvider.create(
                    hostViewModel.config.controllerSupportChatsType()
                ){
                    onStubShowing(it)
                }
            },
            lazy { mapper },
            lazy {
                ChannelsStubFactory(hostViewModel.config is SupportComponentConfig.SabyGet)
            }
        )

        // Если открыть список каналов и проскролить в низ, затем вернуться обратно то ННП перекроет последний элемент.
        // Решили сделать как везде, а именно увеличить отступ для последнего элемента
        val lastItemBottomMargin =
            resources.getDimensionPixelOffset(RDesign.dimen.communicator_support_last_item_bottom_margin)

        binding.communicatorSupportChannelList.list.addItemDecoration(
            LastItemBottomMarginDecoration(
                lastItemBottomMargin
            )
        )
    }

    private fun updateTopNavigationContent(title: String? = null) {
        val titleValue = title ?: resources.getString(
            if (hostViewModel.config is SupportComponentConfig.SabyGet) {
                R.string.communicator_support_channel_sabyget_title
            } else {
                R.string.communicator_support_channel_list_title
            }
        )
        binding.communicatorSupportChannelListTopNavigationView.content =
            SbisTopNavigationContent.LargeTitle(PlatformSbisString.Value(titleValue))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun cleanSelection() {
        binding.communicatorSupportChannelList.list.cleanSelection()
    }

    override fun shouldHighlightSelectedItems() {
        binding.communicatorSupportChannelList.list.highlightSelection()
    }

    private fun onStubShowing(type: StubType?) {
        when (type) {
            StubType.NO_NETWORK_STUB -> {
                showSbisPopupNotification(
                    resources.getString(
                        RDesign.string.communicator_sync_error_message
                    )
                )
            }

            StubType.SERVER_TROUBLE -> {
                showSbisPopupNotification(
                    resources.getString(
                        RCommon.string.common_service_error
                    )
                )
            }

            StubType.BAD_FILTER_STUB -> {
                if (hostViewModel.config is SupportComponentConfig.SabyGet) {
                    runOnUiThread {
                        updateTopNavigationContent(StringUtils.EMPTY)
                    }
                }
            }

            StubType.ENTRY_NOT_FOUND -> {
                if (hostViewModel.config is SupportComponentConfig.SabyGet) {
                    runOnUiThread {
                        updateTopNavigationContent(StringUtils.EMPTY)
                    }
                }
            }

            null -> {
                if (hostViewModel.config is SupportComponentConfig.SabyGet) {
                    runOnUiThread {
                        updateTopNavigationContent()
                    }
                }
            }

            else -> Unit
        }
    }

    private fun showSbisPopupNotification(message: String) {
        val icon = SbisMobileIcon.Icon.smi_WiFiNone.character.toString()
        SbisPopupNotification.push(SbisPopupNotificationStyle.ERROR, message, icon)
    }
}