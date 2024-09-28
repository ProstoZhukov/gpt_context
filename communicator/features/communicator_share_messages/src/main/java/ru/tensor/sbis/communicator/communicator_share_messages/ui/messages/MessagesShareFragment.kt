package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.di.CommonSingletonComponentProvider
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.share.ThemeShareSelectionResultListener
import ru.tensor.sbis.communicator.communicator_share_messages.R
import ru.tensor.sbis.communicator.communicator_share_messages.ShareMessagesPlugin
import ru.tensor.sbis.communicator.communicator_share_messages.databinding.CommunicatorShareMessagesFragmentBinding
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.di.DaggerMessagesShareComponent
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui.MessagesShareController
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui.MessagesShareView
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui.MessagesShareViewImpl
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType.ThemeRegistryType
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuContent
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate
import java.lang.IllegalArgumentException

/**
 * Экран шаринга в сообщения: диалоги или каналы.
 *
 * @author dv.baranov
 */
internal class MessagesShareFragment :
    BaseFragment(),
    ShareMenuContent,
    KeyboardEventListener,
    ThemeShareSelectionResultListener {

    companion object {

        /**
         * Создать новый инстанс экран шаринга в сообщения: диалоги или каналы.
         *
         * @param registryType тип реестра - диалоги/каналы.
         * @param shareData данные, которыми делится пользователь.
         * @param quickShareKey ключ для быстрого шаринга.
         */
        fun newInstance(
            registryType: ThemeRegistryType,
            shareData: ShareData,
            quickShareKey: String? = null
        ): Fragment =
            MessagesShareFragment().withArgs {
                putSerializable(MESSAGES_SHARE_REGISTRY_TYPE_KEY, registryType)
                putParcelable(MESSAGES_SHARE_DATA_KEY, shareData)
                putString(MESSAGES_QUICK_SHARE_KEY, quickShareKey)
            }
    }

    private var view: MessagesShareView? = null
    private lateinit var controller: MessagesShareController

    private val fragmentLayout: Int
        get() = R.layout.communicator_share_messages_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && arguments?.getString(MESSAGES_QUICK_SHARE_KEY) == null) {
            val registryType = requireArguments()
                .getSerializableUniversally<ThemeRegistryType>(MESSAGES_SHARE_REGISTRY_TYPE_KEY)!!
            showSelectionFragment(registryType)
        }
        DaggerMessagesShareComponent.factory().create(
            CommonSingletonComponentProvider.get(requireContext()),
            {
                MessagesShareViewImpl(CommunicatorShareMessagesFragmentBinding.bind(requireView()), this)
                    .also {
                        this.view = it
                    }
            },
            ShareMessagesPlugin.sendMessageManagerProvider.get().getSendMessageManager(),
            ShareMessagesPlugin.sendMessageUseCaseProvider.get(),
            arguments?.getParcelable(MESSAGES_SHARE_DATA_KEY)
                ?: throw IllegalArgumentException(EXCEPTION_SHARE_DATA_MESSAGE),
            arguments?.getString(MESSAGES_QUICK_SHARE_KEY)
        ).also {
            controller = it.injector().inject(this)
        }
    }

    private fun showSelectionFragment(registryType: ThemeRegistryType) {
        val fragment = createSelectionFragment(registryType)
        val containerId = R.id.communicator_share_messages_selection_container
        childFragmentManager.beginTransaction()
            .add(containerId, fragment)
            .commitNow()
    }

    private fun createSelectionFragment(registryType: ThemeRegistryType): Fragment =
        ShareMessagesPlugin.themesRegistryFragmentFactoryProvider.get()
            .createShareThemeFragment(type = registryType)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, fragmentLayout, container, false)

    override fun onBackPressed(): Boolean =
        controller.onBackPressed()

    override fun setShareMenuDelegate(delegate: ShareMenuDelegate) {
        controller.initMenuController(delegate)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        childFragmentManager.fragments.forEach {
            it.castTo<KeyboardEventListener>()?.onKeyboardOpenMeasure(keyboardHeight)
        }
        return view?.onKeyboardMeasure(keyboardHeight) ?: false
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        childFragmentManager.fragments.forEach {
            it.castTo<KeyboardEventListener>()?.onKeyboardCloseMeasure(keyboardHeight)
        }
        return view?.onKeyboardMeasure(0) ?: false
    }

    override fun onConversationSelected(model: ConversationModel) {
        controller.onConversationSelected(model)
    }
}

private const val MESSAGES_SHARE_REGISTRY_TYPE_KEY = "MESSAGES_SHARE_REGISTRY_TYPE_KEY"
private const val MESSAGES_SHARE_DATA_KEY = "MESSAGES_SHARE_DATA_KEY"
private const val MESSAGES_QUICK_SHARE_KEY = "MESSAGES_QUICK_SHARE_KEY"
private const val EXCEPTION_SHARE_DATA_MESSAGE = "SHARE DATA is null or not defined"
