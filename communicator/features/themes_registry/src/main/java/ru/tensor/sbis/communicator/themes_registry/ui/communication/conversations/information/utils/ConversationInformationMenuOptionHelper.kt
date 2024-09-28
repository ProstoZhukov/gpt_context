package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils

import android.content.Context
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.fab_options.ConversationInformationFabOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationOption
import ru.tensor.sbis.design.context_menu.MenuItem

/**
 * Вспомогательный класс для создания элементов меню.
 *
 * @author da.zhukov
 */
internal class ConversationInformationMenuOptionHelper(
    private val conversationInformationData: ConversationInformationData
) {

    /** @SelfDocumented */
    fun getMoreBtnOptions(
        context: Context,
        onOptionSelected: (option: ConversationInformationOption) -> Unit
    ): List<MenuItem> = buildList {
        if (conversationInformationData.canAddParticipants) {
            add(ConversationInformationOption.ADD_MEMBER.getItem(context, onOptionSelected))
        }
        add(ConversationInformationOption.COPY_LINK.getItem(context, onOptionSelected))
        add(ConversationInformationOption.DELETE.getItem(context, onOptionSelected))
    }

    /** @SelfDocumented */
    fun getFabBtnOptions(
        context: Context,
        optionAction: (option: ConversationInformationFabOption) -> Unit
    ): List<MenuItem> = buildList {
        add(ConversationInformationFabOption.ADD_FILE.getItem(context, optionAction))
        add(ConversationInformationFabOption.CREATE_FOLDER.getItem(context, optionAction))
    }

    private fun ConversationInformationFabOption.getItem(
        context: Context,
        optionAction: (option: ConversationInformationFabOption) -> Unit
    ) = MenuItem(
        title = context.getString(textRes)
    ) {
        optionAction(this)
    }

    private fun ConversationInformationOption.getItem(
        context: Context,
        onOptionSelected: (option: ConversationInformationOption) -> Unit
    ) = MenuItem(
        title = context.getString(textRes),
        image = iconRes,
        destructive = destructive
    ) {
        onOptionSelected(this)
    }
}