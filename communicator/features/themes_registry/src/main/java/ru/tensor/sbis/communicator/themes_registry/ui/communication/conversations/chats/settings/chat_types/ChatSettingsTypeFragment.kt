package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import ru.tensor.sbis.base_components.fragment.selection.SelectionWindowContent
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorChatSettingsChatTypeFragmentBinding
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorChatSettingsTypeFragmentHeaderBinding
import ru.tensor.sbis.design.utils.extentions.applyHeight
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreator
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.anyContainerAs
import ru.tensor.sbis.design_dialogs.dialogs.content.utils.didAction

/**
 * Фрагмент изменения типа канала.
 *
 * @author dv.baranov
 */
internal class ChatSettingsTypeFragment :
    SelectionWindowContent(),
    ChatSettingsTypeChangingListener {

    /**
     * Создатель экземпляра контента.
     * Используется контейнером для создания экземпляра фрагмента, содержащего отображаемый контент.
     */
    internal class Creator(private val currentType: ChatSettingsTypeOptions) : ContentCreator {

        override fun createFragment(): androidx.fragment.app.Fragment =
            ChatSettingsTypeFragment().withArgs {
                putSerializable(CURRENT_CHAT_TYPE_ARG, currentType)
            }
    }

    private var binding: CommunicatorChatSettingsChatTypeFragmentBinding? = null

    override fun hasHeaderDivider(): Boolean = false

    override fun inflateHeaderView(inflater: LayoutInflater, container: ViewGroup) {
        CommunicatorChatSettingsTypeFragmentHeaderBinding.inflate(inflater, container, true)
    }

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup) {
        this.setCloseHoodVisible(false)
        makeHeaderSmall(container, requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.communicator_chat_settings_chat_type_fragment, container, true)
        binding!!.currentType = requireArguments().getSerializable(CURRENT_CHAT_TYPE_ARG) as ChatSettingsTypeOptions
        binding!!.changingListener = this
    }

    override fun getContentViewId(): Int = R.id.communicator_chat_settings_chat_type_root

    override fun onChatTypeChanged(chatType: ChatSettingsTypeOptions) {
        if (binding!!.currentType != chatType) {
            anyContainerAs<ChatSettingsTypeChangingListener>()?.onChatTypeChanged(chatType)
            binding!!.currentType = chatType
            binding!!.executePendingBindings()
            didAction(
                CURRENT_CHAT_TYPE_REQUEST_KEY,
                bundleOf(CURRENT_CHAT_TYPE_RESULT to chatType),
            )
        }
        requireArguments().putSerializable(CURRENT_CHAT_TYPE_ARG, chatType)
        anyContainerAs<Container.Closeable>()?.closeContainer()
    }

    override fun onChatParticipationTypeChanged(chatType: ChatSettingsParticipationTypeOptions) {}

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}

/**
 * Сделать шапку фрагмента изменения типа канала маленькой, чтобы соответствовать макету.
 */
internal fun makeHeaderSmall(container: ViewGroup, context: Context) {
    val header = container.rootView.findViewById<FrameLayout>(ru.tensor.sbis.base_components.R.id.base_components_header_container)
    header.applyHeight(context.getDimenPx(ru.tensor.sbis.design.R.attr.inlineHeight_7xs))
}

internal const val CURRENT_CHAT_TYPE_REQUEST_KEY = "CURRENT_CHAT_TYPE_REQUEST_KEY"
internal const val CURRENT_CHAT_TYPE_RESULT = "CURRENT_CHAT_TYPE_RESULT"
private const val CURRENT_CHAT_TYPE_ARG = "CURRENT_CHAT_TYPE_ARG"
