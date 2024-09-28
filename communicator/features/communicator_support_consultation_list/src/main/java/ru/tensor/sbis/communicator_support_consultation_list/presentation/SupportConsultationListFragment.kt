package ru.tensor.sbis.communicator_support_consultation_list.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communicator.design.icon
import ru.tensor.sbis.communicator_support_consultation_list.R
import ru.tensor.sbis.communicator_support_consultation_list.data.ConsultationsCollectionCrud3ServiceWrapperImpl
import ru.tensor.sbis.communicator_support_consultation_list.databinding.CommunicatorSupportConsultationsFragmentBinding
import ru.tensor.sbis.communicator_support_consultation_list.di.SupportConsultationListPlugin
import ru.tensor.sbis.communicator_support_consultation_list.feature.SabyGetConfig
import ru.tensor.sbis.communicator_support_consultation_list.feature.SupportConsultationListFeatureContract
import ru.tensor.sbis.communicator_support_consultation_list.mapper.SupportConsultationMapperFactory
import ru.tensor.sbis.communicator_support_consultation_list.utills.LastItemBottomMarginDecoration
import ru.tensor.sbis.consultations.generated.ConsultationCollectionProvider
import ru.tensor.sbis.consultations.generated.SupportRegistryDataConsultations
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.extentions.setLeftPadding
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegate
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateImpl
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateParams
import ru.tensor.sbis.design.utils.insets.IndentType
import ru.tensor.sbis.design.utils.insets.Position
import ru.tensor.sbis.design.utils.insets.ViewToAddInset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.profile.R as RDesignProfile

/**
 * Фрагмент реестра обращений в техподдержку
 */
internal class SupportConsultationListFragment : SwipeBackFragment(),
    SupportConsultationListFeatureContract,
    DefaultViewInsetDelegate by DefaultViewInsetDelegateImpl() {

    private val onSelectConsultation = MutableSharedFlow<UUID>()
    private val onSabyGetToolbarClick = MutableSharedFlow<UUID>()

    private var _binding: CommunicatorSupportConsultationsFragmentBinding? = null
    private val binding get() = _binding!!

    private val channelId
       get() = arguments?.getSerializable(CHANNEL_ID) as? UUID

    /**
     * @see ru.tensor.sbis.crud3.ListComponentView.inject
     */
    @Inject
    internal lateinit var supportConsultationMapperFactory: Provider<SupportConsultationMapperFactory>

    companion object {

        private const val CHANNEL_ID = "CHANNEL_ID"
        private const val CHANNEL_TITLE = "CHANNEL_TITLE"
        private const val CHANNEL_ICON = "CHANNEL_ICON"
        private const val NEED_BACK_BTN_ARG = "NEED_BACK_BTN_ARG"
        private const val IS_SINGLE_CHANNEL = "IS_SINGLE_CHANNEL"
        private const val CHANNEL_URL = "CHANNEL_URL"
        private const val CHANNEL_SUBTITLE = "CHANNEL_SUBTITLE"
        private const val SABY_GET_CONFIG = "SABY_GET_CONFIG"


        /**
         * @see ru.tensor.sbis.crud3.ListComponentView.inject
         */
        private const val PAGE_SIZE = 40

        /**
         * @see ru.tensor.sbis.crud3.ListComponentView.inject
         */
        private const val VIEW_POST_SIZE = 10

        fun newInstance(
            supportRegistryDataConsultations: SupportRegistryDataConsultations,
            needBackBtn: Boolean = true,
            sabyGetConfig: SabyGetConfig? = null,
            isSingleChannel:Boolean = false
        ): SupportConsultationListFragment {
            return SupportConsultationListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CHANNEL_ID, supportRegistryDataConsultations.sourceId)
                    putString(CHANNEL_TITLE, supportRegistryDataConsultations.title)
                    putString(CHANNEL_ICON, supportRegistryDataConsultations.icon.iconName)
                    putString(CHANNEL_URL, supportRegistryDataConsultations.icon.url)
                    putString(CHANNEL_SUBTITLE, supportRegistryDataConsultations.subTitle)
                    putBoolean(NEED_BACK_BTN_ARG, needBackBtn)
                    putBoolean(IS_SINGLE_CHANNEL, isSingleChannel)
                    sabyGetConfig?.let { putParcelable(SABY_GET_CONFIG, it) }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupportConsultationListPlugin.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CommunicatorSupportConsultationsFragmentBinding.inflate(inflater, container, false)
        val isSingleChannel = arguments?.getBoolean(IS_SINGLE_CHANNEL, false)
        if (arguments?.containsKey(SABY_GET_CONFIG) == true || isSingleChannel == true) {
            initInsetListener(
                DefaultViewInsetDelegateParams(
                    listOf(
                        ViewToAddInset(
                            binding.root.findViewById<SbisTopNavigationView>(R.id.communicator_support_consultation_list_top_navigation_view),
                            listOf(IndentType.PADDING to Position.TOP)
                        )
                    )
                )
            )
            return binding.root
        }
        return addToSwipeBackLayout(binding.root)
    }

    override fun swipeBackEnabled(): Boolean = !DeviceConfigurationUtils.isTablet(requireContext())

    override fun nestedSwipeBackSupported(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapper = supportConsultationMapperFactory.get()
            .create(
                requireContext(),
                ListDateFormatter.MixedListDateFormatter(requireContext())) {
                lifecycleScope.launch {
                    onSelectConsultation.emit(it.id)
                }
            }

        val channelTitle = arguments?.getString(CHANNEL_TITLE)
        val channelSubTitle = arguments?.getString(CHANNEL_SUBTITLE)
        val channelIcon = arguments?.getString(CHANNEL_ICON)
        val needBackBtn = arguments?.getBoolean(NEED_BACK_BTN_ARG)
        val channelUrl = arguments?.getString(CHANNEL_URL)
        val sabyGetConfig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                SABY_GET_CONFIG,
                SabyGetConfig::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(SABY_GET_CONFIG)
        }

        binding.communicatorSupportConsultationList.inject(
            this,
            lazy {
                ConsultationsCollectionCrud3ServiceWrapperImpl(
                    channelId,
                    sabyGetConfig != null,
                    ConsultationCollectionProvider.instance()
                )
            },
            lazy { mapper },
            lazy {
                StubFactory()
            },
            pageSize = PAGE_SIZE,
            viewPostSize = VIEW_POST_SIZE
        )

        val lastItemBottomMargin =
            resources.getDimensionPixelOffset(RCommunicatorDesign.dimen.communicator_support_last_item_bottom_margin)

        binding.communicatorSupportConsultationList.list.addItemDecoration(
            LastItemBottomMarginDecoration(
                lastItemBottomMargin
            )
        )

        createToolbar(
            toolbarIcon = channelIcon,
            toolbarPhotoUrl = channelUrl,
            toolbarTitle = channelTitle,
            toolbarSubTitle = channelSubTitle,
            needBackBtn = needBackBtn == true,
            sabyGetConfig
        )
    }

    override fun getFragment(): Fragment = this

    override fun selectedConsultation(): Flow<UUID> = onSelectConsultation

    override fun onSabyGetTitleClick(): Flow<UUID> = onSabyGetToolbarClick

    private fun createToolbar(
        toolbarIcon: String?,
        toolbarPhotoUrl: String?,
        toolbarTitle: String?,
        toolbarSubTitle: String?,
        needBackBtn: Boolean,
        sabyGetConfig: SabyGetConfig?
    ) {
        val isSabyGet = sabyGetConfig?.let { !it.isBrand } ?: false
        val isBrand = sabyGetConfig?.isBrand ?: false
        val hasAccordion = isBrand && (sabyGetConfig?.hasAccordion ?: false)

        val topNavigation = binding.communicatorSupportConsultationListTopNavigationView

        when {
            isBrand -> {
                prepareBrandToolbar(topNavigation, hasAccordion)
            }
            isSabyGet -> {
                prepareSabyGetToolbar(
                    topNavigation,
                    toolbarTitle,
                    toolbarSubTitle,
                    toolbarIcon,
                    needBackBtn,
                    toolbarPhotoUrl
                )
            }
            else -> {
                prepareDefToolbar(
                    topNavigation,
                    toolbarTitle,
                    toolbarIcon,
                    needBackBtn
                )
            }
        }
    }

    private fun prepareDefToolbar(
        topNavigationView: SbisTopNavigationView,
        toolbarTitle: String?,
        toolbarIcon: String?,
        needBackBtn: Boolean
    ) {
        topNavigationView.apply {
            baseTopNavigationPreparing(topNavigationView, toolbarTitle, toolbarIcon, needBackBtn, true)
            smallTitleMaxLines = 2
        }
    }

    private fun prepareSabyGetToolbar(
        topNavigationView: SbisTopNavigationView,
        toolbarTitle: String?,
        toolbarSubTitle: String?,
        toolbarIcon: String?,
        needBackBtn: Boolean,
        toolbarPhotoUrl: String?
    ) {
        val needShowIcon = toolbarPhotoUrl.isNullOrEmpty() || toolbarPhotoUrl == "null"
        val companyData = if (!needShowIcon) CompanyData(null, toolbarPhotoUrl) else null

        topNavigationView.apply {
            baseTopNavigationPreparing(topNavigationView, toolbarTitle, toolbarIcon, needBackBtn, needShowIcon)
            if (!needShowIcon) {
                companyData?.let { personView?.setDataList(listOf(it)) }
            }
            toolbarSubTitle?.let {
                subtitleView?.text = it
                subtitleView?.isVisible = true
            }
            smallTitleMaxLines = 1
            setOnClickListener {
                lifecycleScope.launch {
                    channelId?.let { onSabyGetToolbarClick.emit(it) }
                }
            }
        }
    }

    private fun prepareBrandToolbar(topNavigationView: SbisTopNavigationView, hasAccordion: Boolean) {
        topNavigationView.apply {
            content = SbisTopNavigationContent.LargeTitle(PlatformSbisString.Res(R.string.communicator_support_consultation_saby_toolbar_title))
            titlePosition = HorizontalAlignment.LEFT
            showBackButton = false
            if (hasAccordion) setLeftPadding(dp(45))
        }
    }

    private fun baseTopNavigationPreparing(
        topNavigationView: SbisTopNavigationView,
        toolbarTitle: String?,
        toolbarIcon: String?,
        needBackBtn: Boolean,
        needShowIcon: Boolean
    ) {
        topNavigationView.apply {
            content = SbisTopNavigationContent.SmallTitle(
                PlatformSbisString.Value(StringUtils.EMPTY)
            )
            titlePosition = HorizontalAlignment.LEFT
            this.showBackButton = needBackBtn
            toolbarTitle?.let { titleView?.value = it }
            backBtn?.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            if (needShowIcon) {
                leftIconView?.apply {
                    isVisible = true
                    textSize = resources.getDimension(RDesignProfile.dimen.design_profile_sbis_title_view_collage_size)
                    setTextColor(topNavigationView.titleView!!.getColorFromAttr(RDesign.attr.iconColor))
                    text = toolbarIcon?.icon?.character.toString()
                }
                personView?.isVisible = false
            } else {
                leftIconView?.isVisible = false
                personView?.isVisible = true
            }
        }
    }
}