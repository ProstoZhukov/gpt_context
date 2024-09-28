package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.isTablet
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.R
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageActionsListener
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageViewHolder
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.contract.MessageInformationFragmentFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.data.MessageInformationModel
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.di.DaggerMessageInformationComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.di.MessageInformationComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.ReadStatusListView
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusFocusChangeListener
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment
import java.util.*
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Фрагмент информации о сообщении.
 *
 * @author vv.chekurda
 */
internal class MessageInformationFragment : BasePresenterFragment<MessageInformationView, MessageInformationPresenter>(),
    MessageInformationView,
    AdjustResizeHelper.KeyboardEventListener,
    ReadStatusFocusChangeListener {

    companion object : MessageInformationFragmentFactory {

        override fun createMessageInformationFragment(
            dialogUuid: UUID,
            messageUuid: UUID,
            isGroupDialog: Boolean,
            isChannel: Boolean
        ): Fragment = MessageInformationFragment().withArgs {
            putSerializable(MESSAGE_INFORMATION_DIALOG_UUID_ARG, dialogUuid)
            putSerializable(MESSAGE_INFORMATION_MESSAGE_UUID_ARG, messageUuid)
            putBoolean(MESSAGE_INFORMATION_IS_GROUP_DIALOG_ARG, isGroupDialog)
            putBoolean(MESSAGE_INFORMATION_IS_CHANNEL_ARG, isChannel)
        }

        override fun createMessageInformationFragment(args: Bundle): Fragment =
            MessageInformationFragment().apply { arguments = args }
    }

    private val navigationDelegate by lazy { component.communicatorConversationRouter }

    private var readStatusListView: ReadStatusListView? = null

    private val isPhoneLandscape: Boolean by lazy {
        !isTablet
            && DeviceConfigurationUtils.isLandscape(requireContext())
    }

    private lateinit var component: MessageInformationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationDelegate.initCommunicatorRouter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        addToSwipeBackLayout(inflate(inflater, R.layout.communicator_fragment_message_information, container, false))

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readStatusListView = view.findViewById(R.id.communicator_read_status_list_view)
        initToolbar(view.findViewById(R.id.communicator_message_info_toolbar))
        initReadStatusListView(readStatusListView!!)
        view.findViewById<ScrollView>(R.id.communicator_message_holder)!!.setOnTouchListener { _, event ->
            if (event.action == ACTION_DOWN) {
                readStatusListView?.hideKeyboard()
            }
            false
        }
    }

    private fun initToolbar(toolbarView: SbisTopNavigationView) {
        toolbarView.apply {
            content = SbisTopNavigationContent.SmallTitle(
                PlatformSbisString.Res(RCommunicatorDesign.string.communicator_message_information_title)
            )
            setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initReadStatusListView(readStatusListView: ReadStatusListView) {
        readStatusListView.initReadStatusListView(component.readStatusListViewDependency)
    }

    override fun showMessage(message: ConversationMessage) {
        val messageHolderView = requireView().findViewById<ScrollView>(R.id.communicator_message_holder)
        messageHolderView.removeAllViews()
        val view = MessageView(requireContext()).apply {
            setMessageViewPool(
                singletonComponent.conversationViewPoolController.getViewPoolsHolder(requireContext())
                    .messageViewPool
            )
        }
        val viewHolder = MessageViewHolder(
            messageView = view,
            actionsListener = parentFragment?.castTo<MessageActionsListener>() ?: stubMessageActionsListener()
        )
        viewHolder.bind(message)
        messageHolderView.addView(viewHolder.itemView)
        message.message?.receiverCount?.let {
            readStatusListView?.onMessageReceiversCountChanged(it)
        }
    }

    override fun selectFilter(filter: MessageReadStatus) {
        requireView().findViewById<ReadStatusListView>(R.id.communicator_read_status_list_view)
            .selectFilter(filter)
    }

    override fun onFocusChanged(hasFocus: Boolean) {
        if (!hasFocus || needChangeSectionsRatio()) {
            if (!isPhoneLandscape) animateGuideLine()
            changeSectionsRatio(hasFocus)
        }
    }

    /**
     * Логика определения необходимости менять соотношение размеров секций сообщения и списка прочитавших,
     * чтобы видеть часть списка при поиске с поднятой клавиатурой, когда сообщение длинное и занимает значительную
     * часть контента экрана.
     */
    private fun needChangeSectionsRatio(): Boolean =
        view?.run {
            val readStatusSectionHeight = findViewById<View>(R.id.communicator_read_status_list_view).measuredHeight
            val messageSectionHeight = findViewById<View>(R.id.communicator_message_holder).measuredHeight
            // Суммарная высота контентной области
            val sumContentHeight = readStatusSectionHeight + messageSectionHeight
            // Допустимая высота контента сообщения, при которой соотношение можно не изменять
            val nonRatioChangesHeight = MIN_MESSAGE_SECTION_RATIO_PERCENT * sumContentHeight
            return messageSectionHeight > nonRatioChangesHeight || isPhoneLandscape
        } ?: false

    private fun animateGuideLine() {
        val ts = TransitionSet()
            .addTransition(ChangeBounds())
            .excludeChildren(R.id.communicator_read_status_sbis_list, true)
            .excludeChildren(R.id.communicator_read_status_search_input, true)
        TransitionManager.beginDelayedTransition(
            requireView().findViewById(R.id.communicator_message_info_read_status_container),
            ts
        )
    }

    /**
     * Изменить соотношение размеров секций сообщения и списка прочитавших
     * @param toMinRatio true, если изменить соотношение к минимальному допустимому размеру сообщения
     */
    private fun changeSectionsRatio(toMinRatio: Boolean) {
        view?.findViewById<Guideline>(R.id.communicator_message_info_guideline)?.setGuidelinePercent(
            if (toMinRatio) getMinMessageSectionRatioPercent() else MAX_MESSAGE_SECTION_RATIO_PERCENT
        )
    }

    private fun getMinMessageSectionRatioPercent(): Float =
        if (isPhoneLandscape) {
            MIN_PHONE_LANDSCAPE_MESSAGE_SECTION_RATIO_PERCENT
        } else {
            MIN_MESSAGE_SECTION_RATIO_PERCENT
        }

    override fun onDestroy() {
        super.onDestroy()
        readStatusListView = null
        navigationDelegate.detachCommunicatorRouter()
    }

    override fun onBackPressed(): Boolean {
        parentFragmentManager.popBackStack()
        return true
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean =
        readStatusListView?.onKeyboardOpenMeasure(keyboardHeight) ?: false

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean =
        readStatusListView?.onKeyboardCloseMeasure(keyboardHeight) ?: false

    override fun swipeBackEnabled() = true

    override fun getPresenterView() = this

    override fun createPresenter(): MessageInformationPresenter =
        component.messageInformationPresenter

    @Suppress("DEPRECATION")
    override fun inject() {
        val dialogUuid = requireArguments().getSerializable(MESSAGE_INFORMATION_DIALOG_UUID_ARG) as UUID
        val messageUuid = requireArguments().getSerializable(MESSAGE_INFORMATION_MESSAGE_UUID_ARG) as UUID
        val isGroupDialog = requireArguments().getBoolean(MESSAGE_INFORMATION_IS_GROUP_DIALOG_ARG, false)
        val isChannel = requireArguments().getBoolean(MESSAGE_INFORMATION_IS_CHANNEL_ARG, false)
        val messageInformation = MessageInformationModel(dialogUuid, messageUuid, isGroupDialog, isChannel)
        component = DaggerMessageInformationComponent.factory()
            .create(
                fragment = this,
                focusChangeListener = this,
                messageInfo = messageInformation,
                component = singletonComponent
            )
    }

    /** Заглушка MessageActionsListener */
    private fun stubMessageActionsListener() = object : MessageActionsListener {
        override fun onAcceptSigningButtonClicked(data: ConversationMessage) = Unit
        override fun onRejectSigningButtonClicked(data: ConversationMessage) = Unit
        override fun onPhotoClicked(senderUuid: UUID) = Unit
        override fun onSenderNameClicked(senderUuid: UUID) = Unit
        override fun onGrantAccessButtonClicked(data: ConversationMessage, sender: View) = Unit
        override fun onDenyAccessButtonClicked(data: ConversationMessage) = Unit

        override fun onMessageSelected(conversationMessage: ConversationMessage) = Unit
        override fun onMessageErrorStatusClicked(conversationMessage: ConversationMessage) = Unit
        override fun onMessageAttachmentClicked(message: Message, attachment: AttachmentViewModel) = Unit
        override fun onLinkClicked() = Unit
        override fun onQuoteClicked(quotedMessageUuid: UUID) = Unit
        override fun onServiceMessageClicked(position: Int) = Unit
        override fun onMediaMessageExpandClicked(data: ConversationMessage, expanded: Boolean) = false
        override fun onMediaPlaybackError(error: Throwable) = Unit
        override fun onPhoneNumberClicked(phoneNumber: String) = Unit
        override fun onPhoneNumberLongClicked(phoneNumber: String, messageUUID: UUID?) = Unit
        override fun onThreadMessageClicked(data: ConversationMessage) = Unit
        override fun onThreadCreationServiceClicked(data: ConversationMessage) = Unit
    }
}

internal const val MESSAGE_INFORMATION_DIALOG_UUID_ARG = "dialog_uuid"
internal const val MESSAGE_INFORMATION_MESSAGE_UUID_ARG = "message_uuid"
internal const val MESSAGE_INFORMATION_IS_GROUP_DIALOG_ARG = "is_group_dialog"
internal const val MESSAGE_INFORMATION_IS_CHANNEL_ARG = "is_channel"
private const val MAX_MESSAGE_SECTION_RATIO_PERCENT = 0.5f
private const val MIN_MESSAGE_SECTION_RATIO_PERCENT = 0.35f
private const val MIN_PHONE_LANDSCAPE_MESSAGE_SECTION_RATIO_PERCENT = 0f