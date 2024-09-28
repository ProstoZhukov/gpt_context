package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.base.ChatSettingsItemViewHolder
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.sbis_switch.SbisSwitchView

/**
 * Холдер нижней части экрана настроек чата.
 * Настройка уведомлений канала, кнопка закрытия, кнопка сворачивания/разворачивания списка контактов.
 *
 * @author dv.baranov
 */
internal class ChatSettingsFooterItemViewHolder(
    parentView: ViewGroup,
) : ChatSettingsItemViewHolder<ChatSettingsFooterItem>(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.communicator_chat_settings_footer_item, parentView, false),
) {

    private val collapseButton: SbisTextView = itemView.findViewById(R.id.communicator_chat_settings_footer_collapse_list_contact)
    private val chatTypeView: SbisTextView = itemView.findViewById(R.id.communicator_chat_settings_footer_change_chat_type)
    private val participationTypeView: SbisTextView = itemView.findViewById(R.id.communicator_chat_settings_footer_change_participation_type)
    private val customizeNotificationSwitch: SbisSwitchView = itemView.findViewById(R.id.communicator_chat_settings_footer_switch_notifications)
    private val checkboxAll: SbisCheckboxView = itemView.findViewById(R.id.communicator_chat_settings_footer_checkbox_all)
    private val checkboxPersonal: SbisCheckboxView = itemView.findViewById(R.id.communicator_chat_settings_footer_checkbox_personal)
    private val checkboxAdministrator: SbisCheckboxView = itemView.findViewById(R.id.communicator_chat_settings_footer_checkbox_administrator)
    private val closeChatButton: SbisTextView = itemView.findViewById(R.id.communicator_chat_settings_footer_close_chat)

    override fun bind() {
        initCollapseButton()
        initTypeButtons()
        initCheckboxAndSwitchListeners()
        initCheckbox()
        updateNotificationSwitch(item.skipSwitchAnimation)
        initCloseChatButton()
    }

    private fun initCollapseButton() {
        collapseButton.setOnClickListener { item.onCollapseButtonClick() }
        collapseButton.setText(item.collapseButtonTextResId)
        collapseButton.isVisible = item.isCollapseButtonVisible
    }

    private fun initTypeButtons() {
        chatTypeView.setText(item.chatType.titleRes)
        participationTypeView.setText(item.participationType.titleRes)
        chatTypeView.setOnClickListener {
            handleClickWithCheckPermission(item.onChangeChatTypeButtonClick)
        }
        participationTypeView.setOnClickListener {
            handleClickWithCheckPermission(item.onChangeParticipationTypeButtonClick)
        }
    }

    private fun handleClickWithCheckPermission(onClick: () -> Unit) {
        if (item.isCloseChannelButtonVisible || item.isNewChat) {
            onClick()
        }
    }

    private fun initCheckboxAndSwitchListeners() {
        customizeNotificationSwitch.setOnClickListener { onNotificationSwitchClicked() }
        checkboxAll.setOnClickListener { switchCheckboxAll() }
        checkboxPersonal.setOnClickListener { switchCurrentCheckbox() }
        checkboxAdministrator.setOnClickListener { switchCurrentCheckbox() }
    }

    private fun onNotificationSwitchClicked() {
        if (customizeNotificationSwitch.isChecked) {
            checkboxAll.isCheckBoxChecked = true
            switchCheckboxAll()
        } else {
            item.changeActionDoneButtonVisibility(true)
            checkboxAll.isCheckBoxChecked = false
            checkboxPersonal.isCheckBoxChecked = false
            checkboxAdministrator.isCheckBoxChecked = false
            checkboxPersonal.isEnabled = true
            checkboxAdministrator.isEnabled = true
            checkboxPersonal.presetValue = null
            checkboxAdministrator.presetValue = null
        }
        resultNotificationOptions()
    }

    private fun switchCheckboxAll() {
        item.changeActionDoneButtonVisibility(true)
        if (checkboxAll.isChecked()) {
            checkboxPersonal.isCheckBoxChecked = true
            checkboxPersonal.isEnabled = false
            checkboxAdministrator.isCheckBoxChecked = true
            checkboxAdministrator.isEnabled = false
        } else {
            checkboxAll.isCheckBoxChecked = false
            checkboxPersonal.isEnabled = true
            checkboxAdministrator.isEnabled = true
            checkboxPersonal.presetValue = SbisCheckboxValue.CHECKED
            checkboxAdministrator.presetValue = SbisCheckboxValue.CHECKED
        }
        resultNotificationOptions()
    }

    private fun switchCurrentCheckbox() {
        item.changeActionDoneButtonVisibility(true)
        resultNotificationOptions()
    }

    private fun resultNotificationOptions() {
        val all = !checkboxAll.isChecked() && !checkboxPersonal.isChecked() && !checkboxAdministrator.isChecked()
        val personal = if (checkboxAll.isChecked()) false else checkboxPersonal.isChecked()
        val administrator = if (checkboxAll.isChecked()) false else checkboxAdministrator.isChecked()
        updateNotificationSwitch()
        item.changeNotificationOptions(ChatNotificationOptions(all, personal, administrator))
    }

    private fun updateNotificationSwitch(skipAnimation: Boolean = false) {
        customizeNotificationSwitch.isChecked = checkboxAll.isChecked() ||
            checkboxPersonal.isChecked() || checkboxAdministrator.isChecked()
        if (skipAnimation) {
            customizeNotificationSwitch.jumpDrawablesToCurrentState()
        }
    }

    private fun initCheckbox() {
        val options = item.chatNotificationOptions
        checkboxAll.isCheckBoxChecked = !options.notificationsTurnedOff && !options.notificationsPrivateEvents && !options.notificationsAdminEvents
        checkboxPersonal.isCheckBoxChecked = if (checkboxAll.isChecked()) true else options.notificationsPrivateEvents
        checkboxAdministrator.isCheckBoxChecked = if (checkboxAll.isChecked()) true else options.notificationsAdminEvents
        checkboxPersonal.isEnabled = !checkboxAll.isChecked()
        checkboxAdministrator.isEnabled = !checkboxAll.isChecked()
    }

    private fun initCloseChatButton() {
        closeChatButton.setOnClickListener { item.onCloseChannelButtonClick() }
        closeChatButton.isVisible = item.isCloseChannelButtonVisible
    }

    private fun SbisCheckboxView.isChecked(): Boolean = this.value == SbisCheckboxValue.CHECKED

    override fun recycle() {
        item.onRecycleFooterItem()
        super.recycle()
    }
}
