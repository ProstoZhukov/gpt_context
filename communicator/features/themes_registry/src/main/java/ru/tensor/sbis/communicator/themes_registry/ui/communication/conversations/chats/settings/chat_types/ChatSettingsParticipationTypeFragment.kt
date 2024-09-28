package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import ru.tensor.sbis.base_components.fragment.selection.SelectionWindowContent
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorChatSettingsParticipantTypeFragmentBinding
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorChatSettingsTypeFragmentHeaderBinding
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreator
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.anyContainerAs
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.didAction

/**
 * Фрагмент изменения типа участия в канале.
 *
 * @author dv.baranov
 */
internal class ChatSettingsParticipationTypeFragment :
    SelectionWindowContent(),
    ChatSettingsTypeChangingListener {

    /**
     * Создатель экземпляра контента.
     * Используется контейнером для создания экземпляра фрагмента, содержащего отображаемый контент.
     */
    internal class Creator(private val currentType: ChatSettingsParticipationTypeOptions) : ContentCreator {

        override fun createFragment(): androidx.fragment.app.Fragment =
            ChatSettingsParticipationTypeFragment().withArgs {
                putSerializable(CURRENT_CHAT_PARTICIPATION_TYPE_ARG, currentType)
            }
    }

    private var binding: CommunicatorChatSettingsParticipantTypeFragmentBinding? = null

    override fun hasHeaderDivider(): Boolean = false

    override fun inflateHeaderView(inflater: LayoutInflater, container: ViewGroup) {
        CommunicatorChatSettingsTypeFragmentHeaderBinding.inflate(inflater, container, true)
    }

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup) {
        this.setCloseHoodVisible(false)
        makeHeaderSmall(container, requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.communicator_chat_settings_participant_type_fragment, container, true)
        binding!!.currentType = requireArguments().getSerializable(CURRENT_CHAT_PARTICIPATION_TYPE_ARG) as ChatSettingsParticipationTypeOptions
        binding!!.changingListener = this
    }

    override fun getContentViewId(): Int = R.id.communicator_chat_settings_participant_type_root

    override fun onChatTypeChanged(chatType: ChatSettingsTypeOptions) {
    }

    override fun onChatParticipationTypeChanged(chatType: ChatSettingsParticipationTypeOptions) {
        if (binding!!.currentType != chatType) {
            anyContainerAs<ChatSettingsTypeChangingListener>()?.onChatParticipationTypeChanged(chatType)
            binding!!.currentType = chatType
            binding!!.executePendingBindings()
            didAction(
                CURRENT_PARTICIPATION_CHAT_TYPE_REQUEST_KEY,
                bundleOf(CURRENT_PARTICIPATION_CHAT_TYPE_RESULT to chatType),
            )
        }
        requireArguments().putSerializable(CURRENT_CHAT_PARTICIPATION_TYPE_ARG, chatType)
        anyContainerAs<Container.Closeable>()?.closeContainer()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}

internal const val CURRENT_PARTICIPATION_CHAT_TYPE_REQUEST_KEY = "CURRENT_PARTICIPATION_CHAT_TYPE_REQUEST_KEY"
internal const val CURRENT_PARTICIPATION_CHAT_TYPE_RESULT = "CURRENT_PARTICIPATION_CHAT_TYPE_RESULT"

private const val CURRENT_CHAT_PARTICIPATION_TYPE_ARG = "CURRENT_CHAT_PARTICIPATION_TYPE_ARG"