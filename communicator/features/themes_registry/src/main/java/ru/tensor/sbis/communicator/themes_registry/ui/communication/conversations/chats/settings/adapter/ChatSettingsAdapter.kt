package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsFooterItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsFooterItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsHeaderItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsHeaderItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.base.ChatSettingsItemViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.factory.ChatSettingsItemViewHolderFactory
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.factory.ChatSettingsItemViewHolderFactoryImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsParticipationTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import timber.log.Timber
import java.util.UUID

/**
 * Адаптер настроек чата.
 *
 * @param headerItem верхняя часть экрана.
 * @param footerItem нижняя часть экрана.
 * @param onItemClick действие по нажатию на item.
 * @param onRemoveAdminClick действие по удалению администратора чата.
 * @param isSwipeEnabled true, если свайп-меню должны быть доступны.
 *
 * @author dv.baranov
 */
internal class ChatSettingsAdapter(
    private var headerItem: ChatSettingsHeaderItem,
    private var footerItem: ChatSettingsFooterItem,
    private val onItemClick: (profileUuid: UUID) -> Unit,
    private val onRemoveAdminClick: (admin: ThemeParticipant) -> Unit,
    private var isSwipeEnabled: Boolean,
) : BaseTwoWayPaginationAdapter<ChatSettingsItem>() {

    private lateinit var itemFactory: ChatSettingsItemViewHolderFactory
    private var items: List<ChatSettingsItem>? = null

    init {
        mWithBottomEmptyHolder = false
        mContent.addAll(listOf(headerItem, footerItem))
    }

    /**
     * Обновить аватар.
     */
    fun updateAvatar(dataString: String?) {
        if (headerItem.avatarUrl != dataString) {
            headerItem = headerItem.copy(
                avatarUrl = dataString,
            )
            updateHeader()
        }
    }

    /**
     * Изменить значение поля ввода названия чата.
     */
    fun changeEditChatNameValue(newName: String, needUpdate: Boolean = true) {
        if (headerItem.chatSettingsEditChatNameData.value != newName) {
            headerItem = headerItem.copy(
                chatSettingsEditChatNameData = headerItem.chatSettingsEditChatNameData.copy(
                    value = newName,
                ),
            )
            if (needUpdate) {
                updateHeader()
            } else {
                mContent[0] = headerItem
            }
        }
    }

    /**
     * Изменить доступность редактирования названия чата.
     */
    fun changeEditChatNameIsEnabledValue(newValue: Boolean) {
        if (headerItem.chatSettingsEditChatNameData.isEnabled != newValue) {
            headerItem = headerItem.copy(
                chatSettingsEditChatNameData = headerItem.chatSettingsEditChatNameData.copy(
                    isEnabled = newValue,
                ),
            )
            updateHeader()
        }
    }

    /**
     * Изменить статус валидации поля ввода названия чата.
     */
    fun changeEditChatNameValidationStatus(newStatus: ValidationStatus) {
        if (headerItem.chatSettingsEditChatNameData.validationErrorStatus != newStatus) {
            headerItem = headerItem.copy(
                chatSettingsEditChatNameData = headerItem.chatSettingsEditChatNameData.copy(
                    validationErrorStatus = if (newStatus is ValidationStatus.Error) newStatus else null
                ),
            )
            updateHeader()
        }
    }

    /**
     * Обновить текст заголовока перед списком участников/администраторов.
     */
    fun updatePersonListTitleViewText(newTextRes: Int) {
        if (headerItem.personListTitleViewTextRes != newTextRes) {
            headerItem = headerItem.copy(
                personListTitleViewTextRes = newTextRes,
            )
            updateHeader()
        }
    }

    /**
     * Изменить видимость кнопки добавления администраторов.
     */
    fun changeAddButtonVisibility(isVisible: Boolean) {
        if (headerItem.isAddButtonVisible != isVisible) {
            headerItem = headerItem.copy(
                isAddButtonVisible = isVisible,
            )
            updateHeader()
        }
    }

    /**
     * Обновить состояние чекбоксов и тумблера.
     */
    fun updateCheckboxesAndSwitch(
        options: ChatNotificationOptions,
        skipSwitchAnimation: Boolean,
        needUpdate: Boolean = true,
    ) {
        footerItem = footerItem.copy(
            chatNotificationOptions = options,
            skipSwitchAnimation = skipSwitchAnimation,
        )
        if (needUpdate) {
            updateFooter()
        } else {
            mContent[mContent.size - 1] = footerItem
        }
    }

    /**
     * Изменить видимость кнопки закрытия чата.
     */
    fun changeCloseChatButtonVisibility(isVisible: Boolean) {
        if (footerItem.isCloseChannelButtonVisible != isVisible) {
            footerItem = footerItem.copy(isCloseChannelButtonVisible = isVisible)
            updateFooter()
        }
    }

    /**
     * Обработать нажатие кнопки свернуть/развернуть.
     */
    fun onCollapseButtonClick() {
        val resId = if (footerItem.collapseButtonTextResId == R.string.communicator_chat_show_all) {
            R.string.communicator_chat_collapse_list
        } else {
            R.string.communicator_chat_show_all
        }
        footerItem = footerItem.copy(
            collapseButtonTextResId = resId,
        )
        items?.let {
            setData(it.toMutableList(), 0)
        }
    }

    /**
     * Обновить состояние настроек обоих типов чата.
     */
    fun updateChatTypes(
        newChatType: ChatSettingsTypeOptions?,
        newChatParticipationType: ChatSettingsParticipationTypeOptions?,
    ) {
        footerItem = when {
            newChatType != null && newChatParticipationType != null -> {
                footerItem.copy(
                    chatType = newChatType,
                    participationType = newChatParticipationType,
                )
            }
            newChatType != null -> {
                footerItem.copy(
                    chatType = newChatType,
                )
            }
            newChatParticipationType != null -> {
                footerItem.copy(
                    participationType = newChatParticipationType,
                )
            }
            else -> {
                Timber.e("newChatType and newChatParticipationType is null")
                return
            }
        }
        updateFooter()
    }

    private fun updateHeader() {
        mContent[0] = headerItem
        notifyItemChanged(0)
    }

    private fun updateFooter() {
        mContent[mContent.size - 1] = footerItem
        notifyItemChanged(mContent.size - 1)
    }

    override fun setData(dataList: MutableList<ChatSettingsItem>?, offset: Int) {
        items = dataList?.toList()
        val resultList: MutableList<ChatSettingsItem>? = if (!dataList.isNullOrEmpty() && dataList.size > 2) {
            footerItem = footerItem.copy(
                isCollapseButtonVisible = true,
            )
            if (footerItem.collapseButtonTextResId == R.string.communicator_chat_show_all) {
                dataList.take(2).toMutableList()
            } else {
                dataList
            }
        } else {
            footerItem = footerItem.copy(
                isCollapseButtonVisible = false,
            )
            dataList
        }
        resultList?.add(0, headerItem)
        resultList?.add(resultList.size, footerItem)
        super.setData(resultList, offset)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChatSettingsItemViewHolder<*> {
        mContent.first { it.viewTypeId == viewType }.let {
            return itemFactory.createViewHolder(it::class.java)
        }
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<ChatSettingsItem>, position: Int) {
        val item = mContent[position]
        when (holder) {
            is ChatSettingsHeaderItemViewHolder -> holder.setData(item as ChatSettingsHeaderItem)
            is ChatSettingsContactItemViewHolder -> {
                val contactItem = ChatSettingsContactItem(
                    item.castTo<ChatSettingsContactItem>()!!.participant,
                    onItemClick,
                    onRemoveAdminClick,
                    isSwipeEnabled,
                )
                holder.setData(contactItem)
                if (!isSwipeEnabled) {
                    holder.swipeableLayout.isDragLocked = true
                }
            }
            is ChatSettingsFooterItemViewHolder -> holder.setData(item as ChatSettingsFooterItem)
            else -> super.onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = mContent.size

    override fun getItemViewType(position: Int): Int = mContent[position].viewTypeId

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        itemFactory = ChatSettingsItemViewHolderFactoryImpl(recyclerView)
    }

    override fun getItemType(dataModel: ChatSettingsItem?): Int {
        return dataModel?.viewTypeId ?: 5
    }

    override fun onSavedInstanceState(outState: Bundle) {
        outState.putInt(COLLAPSE_BUTTON_TEXT_RES_ID, footerItem.collapseButtonTextResId)
        outState.putString(
            VALIDATION_STATUS_ERROR,
            headerItem.chatSettingsEditChatNameData.validationErrorStatus?.message
        )
        super.onSavedInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val collapseButtonTextResId = savedInstanceState.getInt(COLLAPSE_BUTTON_TEXT_RES_ID)
        val validationError = savedInstanceState.getString(VALIDATION_STATUS_ERROR)
        footerItem = footerItem.copy(
            collapseButtonTextResId = collapseButtonTextResId,
        )
        headerItem = headerItem.copy(
            chatSettingsEditChatNameData = headerItem.chatSettingsEditChatNameData.copy(
                validationErrorStatus = validationError?.let { ValidationStatus.Error(it) }
            )
        )
        super.onRestoreInstanceState(savedInstanceState)
    }

    /**
     * Сделать свайп-меню доступным для item'а.
     *
     * @param isEnable true, если нужно сделать свайп-меню доступным.
     */
    fun setSwipeEnabled(isEnable: Boolean) {
        if (isEnable) {
            val changed = isEnable != isSwipeEnabled
            if (changed) {
                isSwipeEnabled = isEnable
                notifyDataSetChanged()
            }
        }
    }
}

private const val COLLAPSE_BUTTON_TEXT_RES_ID = "COLLAPSE_BUTTON_TEXT_RES_ID"
private const val VALIDATION_STATUS_ERROR = "VALIDATION_STATUS_ERROR"
