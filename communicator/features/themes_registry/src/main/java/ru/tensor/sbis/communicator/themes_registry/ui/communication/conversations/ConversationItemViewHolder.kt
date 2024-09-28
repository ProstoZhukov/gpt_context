package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.tracing.trace
import androidx.recyclerview.widget.RecyclerView.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.attachments.ui.view.register.contract.AttachmentsActionListener
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.util.DeviceConfigurationUtils.isTablet
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.themes_registry.DialogListActionsListener
import ru.tensor.sbis.communicator.common.util.header_date.DateViewHolder
import ru.tensor.sbis.communicator.core.contract.AttachmentClickListener
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsDispatcher
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType
import ru.tensor.sbis.communicator.generated.DocumentType
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.holders.ChatListActionsListener
import ru.tensor.sbis.communicator.core.views.conversation_views.ConversationItemView
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.ConversationItemsViewPool
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.profile_decl.person.PersonCollageClickListener
import ru.tensor.sbis.swipeablelayout.SwipeableHolderInterface
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.DismissedWithoutMessage
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType
import ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemFactory
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool
import java.util.LinkedList
import java.util.UUID
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.swipeable_layout.R as RSwipe

/**
 * Холдер view ячейки списка реестра диалогов/каналов для отображения переписки.
 * http://axure.tensor.ru/CommunicatorMobile/%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D1%8F.html#OnLoadVariable=%D0%BE%D1%87%D0%B8%D1%81%D1%82%D0%B8%D1%82%D1%8C%20%D0%BF%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5&Dialog=%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D1%8F&CSUM=1
 *
 * Имеется частичная копия реализации для компонента селектора при шаринге
 * [ru.tensor.sbis.design.selection.ui.list.items.multi.share.dialog.DialogMultiSelectorItemViewHolderKt].
 * Общие визуальные изменения и логику отображения views в холдере диалога необходимо поддерживать в обеих реализациях.
 *
 * @param parent родительский [ViewGroup], испольщующийся для создания [itemView] ячейки.
 * @property itemClickHandler обработчик кликов по ячейке.
 * @property dialogActionsListener слушатель действий над ячейками диалогов.
 * @property chatActionsListener слушатель действий над ячейками чатов.
 * @property attachmentClickListener слушатель действий над вложениями диалогов.
 * @param swipeMenuViewPool пул кнопок свайп-меню [SwipeableLayout].
 *
 * @author vv.chekurda
 */
@Suppress("SpellCheckingInspection", "KDocUnresolvedReference")
internal class ConversationItemViewHolder @JvmOverloads constructor(
    parent: ViewGroup,
    private val itemClickHandler: ConversationItemClickHandler,
    private val dialogActionsListener: DialogListActionsListener,
    private val chatActionsListener: ChatListActionsListener,
    private val attachmentClickListener: AttachmentClickListener,
    itemsViewPool: ConversationItemsViewPool,
    swipeMenuViewPool: SwipeMenuViewPool,
    private val isSharingMode: Boolean,
    private val conversationItemView: ConversationItemView = itemsViewPool.conversationItemView
) : AbstractViewHolder<ConversationModel>(itemsViewPool.swipeableLayout),
    SwipeableHolderInterface,
    DateViewHolder {

    /**
     * Данные холдера ячейки диалогов/каналов.
     */
    private var data: ConversationModel? = null

    init {
        conversationItemView.also {
            it.dialogActionsListener = dialogActionsListener
            it.isSharingMode = isSharingMode
        }
        swipeableLayout.also {
            it.addView(conversationItemView, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            it.setMenuItemViewPool(swipeMenuViewPool)
        }
        setClickListeners()
    }

    override fun bind(data: ConversationModel) = trace("ConversationItemViewHolder#bind") {
        this.data = data
        super.bind(data)

        conversationItemView.bind(data)
        swipeableLayout.isDragLocked = isSharingMode || conversationItemView.isCheckModeEnabled
        if (!isSharingMode) bindSwipeButtons(data)
        conversationItemView.attachmentsView?.setAttachmentsClickListener(data)

        // у каналов не должно быть статусов активности
        if (data.isChatForView) {
            conversationItemView.collageLayout.setHasActivityStatus(false)
        } else {
            conversationItemView.collageLayout.setHasActivityStatus(true)
        }
    }

    /**
     * Установить слушателей кликов на view ячейки.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListeners() {
        conversationItemView.setOnClickListener {
            if (data?.documentType != DocumentType.NEWS) {
                MetricsDispatcher.startTrace(MetricsType.FIREBASE_OPEN_DIALOG)
            }
            itemClickHandler.onConversationItemClicked(data!!)
        }
        conversationItemView.setOnLongClickListener {
            itemClickHandler.onConversationItemLongClicked(data!!)
            true
        }
        conversationItemView.setOnTouchListener { _, event ->
            itemClickHandler.onConversationItemTouch(data!!, event)
            false
        }
        conversationItemView.collageLayout.setClickListener(
            object : PersonCollageClickListener {
                override fun onCollageClick() {
                    if (!isSharingMode && data?.isNotice != true) dialogActionsListener.onCollageViewClick(data!!)
                    else itemClickHandler.onConversationItemClicked(data!!)
                }

                override fun onPersonClick(uuid: UUID?) {
                    onCollageClick()
                }
            }
        )
    }

    /**
     * Установить слушателя кликов на view списка вложений.
     */
    private fun AttachmentsView.setAttachmentsClickListener(data: ConversationModel) {
        actionListener = object : AttachmentsActionListener {
            override fun onDeleteAttachmentClick(position: Int) {
                throw UnsupportedOperationException("Illegal method call for attachment view in registry mode")
            }

            override fun onAttachmentClick(position: Int) {
                itemClickHandler.onConversationItemClicked(data)
            }

            override fun onMoreAttachmentsClick(position: Int) {
                itemClickHandler.onConversationItemClicked(data)
            }
        }
    }

    /**
     * Привязать кнопки свайп-меню, соответсвующие текущей модели данных.
     */
    private fun bindSwipeButtons(data: ConversationModel) = with(swipeableLayout) {
        itemUuid = data.uuid.toString()
        addSwipeEventListener<DismissedWithoutMessage> {
            dialogActionsListener.onDismissedWithoutMessage(it.uuid)
        }
        val items: List<SwipeMenuItem> = if (data.isChatForView) {
            itemDismissType = SwipeItemDismissType.DISMISS_IMMEDIATE
            addSwipeEventListener<Dismissed> {
                chatActionsListener.onSwipeHideClicked(data, true)
            }
            setDismissMessage(resources.getString(RCommunicatorDesign.string.communicator_chat_swipe_delete))
            getChatSwipeButtons(data)
        } else {
            itemDismissType = SwipeItemDismissType.CANCELLABLE
            addSwipeEventListener<Dismissed> {
                dialogActionsListener.onSwipeDismissed(UUID.fromString(it.uuid))
            }
            getDialogSwipeButtons(data)
        }

        setMenu(items)
        invalidate()
    }

    /**
     * Получить список необходимых кнопок для свайп-меню ячейки реестра каналов.
     */
    private fun getChatSwipeButtons(chat: ConversationModel): List<SwipeMenuItem> {
        return LinkedList<SwipeMenuItem>().also {
            if (chat.isPinned) {
                it.add(IconWithLabelItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_SwipeUnpin),
                    RCommunicatorDesign.string.communicator_unpin_chat,
                    SwipeItemStyle.BLUE
                ) { chatActionsListener.onUnpinChatClicked(chat) })
            } else if (!chat.isConversationHiddenOrArchived) {
                it.add(IconWithLabelItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_SwipePin),
                    RCommunicatorDesign.string.communicator_pin_chat,
                    SwipeItemStyle.BLUE
                ) { chatActionsListener.onPinChatClicked(chat) })
            }

            if (chat.canBeUnhide) {
                it.add(IconWithLabelItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_SwipeRecover),
                    RCommunicatorDesign.string.communicator_restore_chat,
                    SwipeItemStyle.GREY
                ) { chatActionsListener.onRestoreChatClicked(chat) })
            }

            if (chat.canBeDeleted) {
                it.add(SwipeMenuItemFactory.createDeleteItem(RSwipe.string.design_swipe_menu_label_remove) {
                    chatActionsListener.onSwipeHideClicked(chat, false)
                })
            }
        }
    }

    /**
     * Получить список необходимых кнопок для свайп-меню ячейки реестра диалогов.
     */
    private fun getDialogSwipeButtons(conversation: ConversationModel): List<SwipeMenuItem> {
        val isChat: Boolean = conversation.isChatForOperations
        val isMarkActionGone = !conversation.canBeMarkedRead && !conversation.canBeMarkedUnread || isChat

        return LinkedList<SwipeMenuItem>().also {
            if (!isMarkActionGone) {
                if (conversation.canBeMarkedUnread) {
                    it.add(IconWithLabelItem(
                        SwipeIcon(SbisMobileIcon.Icon.smi_SwipeUnRead),
                        RSwipe.string.design_swipe_menu_label_mark_unread,
                        SwipeItemStyle.BLUE
                    ) { dialogActionsListener.onSwipeMarkClicked(conversation, false) })
                } else {
                    it.add(IconWithLabelItem(
                        SwipeIcon(SbisMobileIcon.Icon.smi_SwipeRead),
                        RSwipe.string.design_swipe_menu_label_mark_read,
                        SwipeItemStyle.BLUE
                    ) { dialogActionsListener.onSwipeMarkClicked(conversation, true) })
                }
            }

            val deletedRegistryOnTablet = conversation.canBeUndeleted && isTablet(swipeableLayout.context)
            // На планшете в реесте удаленных не хватает места для всех иконок - убираем иконку перемещения в папку.
            if (!deletedRegistryOnTablet) {
                it.add(IconWithLabelItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_SwipeFolder),
                    RSwipe.string.design_swipe_menu_label_move,
                    SwipeItemStyle.ORANGE
                ) { dialogActionsListener.onSwipeMoveToFolderClicked(conversation) })
            }

            if (conversation.canBeUndeleted) {
                it.add(IconWithLabelItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_SwipeRecover),
                    RCommunicatorDesign.string.communicator_dialog_restore,
                    SwipeItemStyle.GREY
                ) { dialogActionsListener.onSwipeRestoreClicked(conversation) })
            }

            if (conversation.canBeDeleted) {
                it.add(SwipeMenuItemFactory.createDeleteItem(RSwipe.string.design_swipe_menu_label_remove) {
                    dialogActionsListener.onSwipeRemoveClicked(conversation)
                })
            }
        }
    }

    override fun getSwipeableLayout(): SwipeableLayout =
        itemView as SwipeableLayout

    override fun updateCheckState(checked: Boolean, animate: Boolean) {
        conversationItemView.updateCheckState(checked)
    }

    override fun setFormattedDateTime(formattedDateTime: FormattedDateTime) {
        conversationItemView.updateDateTime(formattedDateTime)
    }

    /**
     * Установить значение включенности мода отображения ячейки для множественного выбора.
     * При активации доступность свайп-меню блокируется.
     *
     * @param isEnabled true, если необходимо активировать мод отображения ячейки для множественного выбора.
     */
    fun setCheckModeEnabled(isEnabled: Boolean) {
        if (!isSharingMode) swipeableLayout.isDragLocked = isEnabled
        conversationItemView.isCheckModeEnabled = isEnabled
        conversationItemView.collageLayout.isClickable = !isEnabled
    }

    /**
     * Установить значение включенности мода отображения ячейки для поиска по контакту.
     * При активации мода скрывается аватарка и изменяется выравнивание заголовка и контента
     * по правой границе аватарки выбранной персоны, находящейся в строке поиска.
     *
     * @param isEnabled true, если необходимо активировать мод отображения ячейки для поиска по контакту.
     */
    fun setSearchByContactModeEnabled(isEnabled: Boolean) {
        conversationItemView.setSearchByContactModeEnabled(isEnabled)
    }

    override fun updateSelectionState(selected: Boolean) {
        itemView.isSelected = selected
    }
}