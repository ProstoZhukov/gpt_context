package ru.tensor.sbis.communicator_support_channel_list.presentation

import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator_support_channel_list.feature.SupportComponentConfig
import ru.tensor.sbis.communicator_support_channel_list.viewmodel.SupportChannelListHostViewModel
import ru.tensor.sbis.master_detail.MasterDetailFragment
import java.util.UUID

/**
 * Реализация MasterDetailFragment.
 * Мастер-фрагмент - это реестр каналов, дочерний - реестр консультаций (из него можно провалиться в переписку).
 */
internal class SupportMasterDetailFragment : MasterDetailFragment(), KeyboardEventListenerChildPropagate {
    override fun createMasterFragment(): Fragment = SupportChannelListFragment()

    /**
     * Вью-модель
     */
    private val hostViewModel: SupportChannelListHostViewModel by viewModels(ownerProducer = { getHostFragment() })

    /**
     * id переписки, если требуется сразу отобразить её
     */
    private val conversationId: UUID? by lazy {
        getConversationIdFromArguments()
    }

    private val isSwipeBackEnabled: Boolean by lazy {
        requireArguments().getBoolean(IS_SWIPE_BACK_ENABLED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                hostViewModel.onChannelSelectedInMaster.collect {
                    showDetailFragment(
                        SupportChannelListDetailFragment.newInstance(hostViewModel.config, it.id, null),
                        isSwipeBackEnabled
                    )
                }
            }
        }

        /**
         * Если открываем по пушу, то сразу показываем переписку
         */
        lifecycleScope.launchWhenResumed {
            conversationId?.let {
                showDetailFragment(
                    SupportChannelListDetailFragment.newInstance(hostViewModel.config, null, it),
                    isSwipeBackEnabled
                )
            }
        }
    }

    /**
     * Получить id переписки из параметров
     */
    @SuppressWarnings("deprecation")
    private fun getConversationIdFromArguments() : UUID? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(CONVERSATION_ID, ParcelUuid::class.java)?.uuid
        } else {
            requireArguments().getParcelable<ParcelUuid?>(CONVERSATION_ID)?.uuid
        }
    }

    companion object {

        private const val CONVERSATION_ID = "CONVERSATION_ID"
        private const val IS_SWIPE_BACK_ENABLED = "IS_SWIPE_BACK_ENABLED"

        /**
         * Получить экземляр фрагмента
         */
        fun newInstance(conversationId: UUID? = null, isSwipeBackEnabled: Boolean = true) =
            SupportMasterDetailFragment().withArgs {
                conversationId?.let {
                    putParcelable(CONVERSATION_ID, ParcelUuid(it))
                    putBoolean(IS_SWIPE_BACK_ENABLED, isSwipeBackEnabled)
                }
            }
    }
}