package ru.tensor.sbis.communicator_support_channel_list.presentation

import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SabyGetChannelListFragmentFactory
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.communicator_support_channel_list.databinding.CommunicatorSupportChannelListRootBinding
import ru.tensor.sbis.communicator_support_channel_list.di.SupportChannelListPlugin
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListHostViewModel
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListViewModelFactoryFactory
import java.util.UUID
import javax.inject.Inject

/**
 * "Рутовый" фрагмент
 * Содержит или SupportMasterDetailFragment, или сразу список обращений, если канал только один
 * @see SupportChannelListHostViewModel
 */
internal class SupportChannelListHostFragment : Fragment(), KeyboardEventListenerChildPropagate {

    @Inject
    internal lateinit var supportChatListViewModelFactoryFactory: SupportChannelListViewModelFactoryFactory

    @Inject
    internal lateinit var supportChannelRouterFactory: SupportChannelRouterFactory

    @Inject
    internal lateinit var scrollHelper: ScrollHelper


    private val config by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(ARG_CONFIG, SupportComponentConfig::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(ARG_CONFIG)
        } ?: SupportComponentConfig.SabySupport
    }

    private val conversationId: UUID? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(CONVERSATION_ID, ParcelUuid::class.java)?.uuid
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable<ParcelUuid?>(CONVERSATION_ID)?.uuid
        }
    }

    /**
     * Фабрика вью-модели.
     */
    private val viewModelFactory = viewModels<SupportChannelListHostViewModel> {
        supportChatListViewModelFactoryFactory.create(
            config,
            DeviceConfigurationUtils.isTablet(requireContext()),
            null,
            conversationId
        )
    }

    /**
     * Вью-модель.
     */
    private lateinit var viewModel: SupportChannelListHostViewModel

    private var _binding: CommunicatorSupportChannelListRootBinding? = null
    private val binding get() = _binding!!

    private val component = SupportChannelListPlugin.component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        viewModel = viewModelFactory.value
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CommunicatorSupportChannelListRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showChannelListOnStart
                    .filterNotNull()
                    .collect {
                        childFragmentManager.beginTransaction()
                            .replace(
                                R.id.communicator_support_channel_list_chat_fragment,
                                SupportMasterDetailFragment.newInstance(
                                    isSwipeBackEnabled = config !is SupportComponentConfig.SabyGet
                                )
                            ).commit()
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showConsultationsListOnStart
                    .filterNotNull()
                    .collect {
                        childFragmentManager.beginTransaction()
                            .replace(
                                R.id.communicator_support_channel_list_chat_fragment,
                                SupportChannelListDetailFragment.newInstance(
                                    config,
                                    it,
                                    null
                                )
                            )
                            .commit()
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openConversationOnStart
                    .filterNotNull()
                    .collect {
                        childFragmentManager.beginTransaction()
                            .replace(
                                R.id.communicator_support_channel_list_chat_fragment,
                                SupportChannelListDetailFragment.newInstance(config, null, it)
                            )
                            .commit()
                    }
            }
        }

        /*
            По пуше обрабатываем событие отдельно
            Если onlyMessages = true, значит канал всего один, и стека навигации нет. В этом
            случае, открываем SupportChannelListDetailFragment (внутри которого будет отображена переписка)
            Если нет, то откроем SupportMasterDetailFragment, в котором будет отображен список каналов
            и сразу будет показана переписка
         */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openConversationOnPush.filterNotNull().collect {
                    val (id, onlyMessages) = it
                    childFragmentManager.beginTransaction().replace(
                        R.id.communicator_support_channel_list_chat_fragment,
                        if (onlyMessages) {
                            SupportChannelListDetailFragment.newInstance(
                                config,
                                null,
                                id
                            )
                        } else {
                            SupportMasterDetailFragment.newInstance(
                                id,
                                config !is SupportComponentConfig.SabyGet
                            )
                        }
                    ).commit()
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showViewStub.collect { stubViewContent ->
                    stubViewContent?.let {
                        binding.communicatorSupportChannelListStubView.setContentFactory {
                            stubViewContent
                        }
                    }
                    binding.communicatorSupportChannelListStubView.isVisible =
                        stubViewContent != null
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            if (savedInstanceState == null)
                viewModel.initViewModel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE)
        _binding = null
    }

    companion object: SabyGetChannelListFragmentFactory {

        /**
         * Аргумент, конфиг компонента
         */
        private const val ARG_CONFIG = "ARG_CONFIG"

        /**
         * идентификатор переписки.
         * передается, если при создании фрагмент требуется открыть переписку
         */
        private const val CONVERSATION_ID = "CONVERSATION_ID"

        /**
         * Получить экземляр фрагмента
         */
        fun newInstance(config: SupportComponentConfig, conversationId: UUID? = null) =
            SupportChannelListHostFragment().withArgs {
                putParcelable(ARG_CONFIG, config)
                conversationId?.let {
                    putParcelable(CONVERSATION_ID, ParcelUuid(it))
                }
            }

        override fun createSabyGetChannelListHostFragment(
            showLeftPanelOnToolbar: Boolean,
            isBrand: Boolean,
            salePoin: UUID?
        ): Fragment {
            return newInstance(SupportComponentConfig.SabyGet(showLeftPanelOnToolbar, isBrand, salePoin))
        }
    }
}

internal fun Fragment.getHostFragment(): Fragment {
    return if (this is SupportChannelListHostFragment) this
    else requireParentFragment().getHostFragment()
}