package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import io.reactivex.functions.Function3
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.communication_decl.selection.SelectionHeaderMode
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionItemsMode
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuConfig
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModelInfo
import java.util.UUID

/**
 * Фабрика для производства конфигурации выбора получателей [RecipientSelectionConfig]
 * 1. [Int] - максимальное количество
 * 2. [RxContainer] - [UUID] документа
 * 3. [RxContainer] - [UUID] диалога
 */
internal typealias RecipientSelectionConfigFactory =
    Function3<Int, RxContainer<UUID?>, RxContainer<UUID?>, RecipientSelectionConfig>

/**
 * Фабрика для производства конфигурации меню выбора получателей [RecipientSelectionMenuConfig]
 * 1. [Int] - максимальное количество
 * 2. [RxContainer] - [UUID] документа
 * 3. [RxContainer] - [UUID] диалога
 */
internal typealias RecipientSelectionMenuConfigFactory =
    Function3<Int, RxContainer<UUID?>, RxContainer<UUID?>, RecipientSelectionMenuConfig>

/**
 * @author vv.chekurda
 */
internal class DefaultSelectionConfigFactory(
    private val viewModelInfo: MessagePanelViewModelInfo
) : RecipientSelectionConfigFactory {

    override fun apply(count: Int, doc: RxContainer<UUID?>, conversation: RxContainer<UUID?>) =
        RecipientSelectionConfig(useCase = getRecipientSelectionUseCase())

    /**
     * Получить стандартный [RecipientSelectionUseCase] для текущей переписки.
     */
    private fun getRecipientSelectionUseCase() = with(viewModelInfo.conversationInfo) {
        when {
            recipientSelectionUseCase != null -> recipientSelectionUseCase
            conversationUuid != null && !isNewConversation -> RecipientSelectionUseCase.Dialog(conversationUuid)
            else -> RecipientSelectionUseCase.NewDialog
        }
    }
}

/**
 * @author vv.chekurda
 */
internal class DefaultSelectionMenuConfigFactory(
    private val selectionConfigFactory: RecipientSelectionConfigFactory
) : RecipientSelectionMenuConfigFactory {

    override fun apply(
        count: Int,
        doc: RxContainer<UUID?>,
        conversation: RxContainer<UUID?>
    ): RecipientSelectionMenuConfig {
        val config = selectionConfigFactory.apply(count, doc, conversation)
        val selectionConfig = config.copy(
            headerMode = SelectionHeaderMode.GONE,
            itemsMode = RecipientSelectionItemsMode.SINGLE_LINE,
            selectionMode = SelectionMode.SINGLE_WITH_APPEND,
            isDepartmentsSelectable = false,
            requestKey = MESSAGE_PANEL_SELECTION_MENU_REQUEST_KEY,
            closeOnComplete = false
        )
        return RecipientSelectionMenuConfig(selectionConfig)
    }
}

internal const val MESSAGE_PANEL_SELECTION_MENU_REQUEST_KEY = "MESSAGE_PANEL_SELECTION_MENU_REQUEST_KEY"