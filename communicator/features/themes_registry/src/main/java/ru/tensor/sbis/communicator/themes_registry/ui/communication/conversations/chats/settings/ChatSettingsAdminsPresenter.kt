package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.uri.UriWrapper
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsListFilter
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import java.util.*

/**
 * Презентер настроек чата с возможностью добавления администраторов.
 * Используется, когда чат уже создан.
 *
 * @param interactor                интерактор настроек чата.
 * @param uriWrapper                класс для работы с URI.
 * @param recipientSelectionManager компонент, отвечающий за получение результатов выбора участников/администраторов чата.
 * @param isNewChat                 true, если новый чат.
 * @param chatUuid                  UUID чата.
 * @param draftChat                 true, если драфтовый чат.
 * @param filter                    фильтр для CRUD-фасада.
 * @param subscriptionManager       менеджер для управления подписками и событиями контроллера.
 * @param networkUtils              @SelfDocumented.
 *
 * @author vv.chekurda
 */
internal class ChatSettingsAdminsPresenter(
    interactor: ChatSettingsInteractor,
    uriWrapper: UriWrapper,
    recipientSelectionManager: RecipientSelectionResultManager,
    isNewChat: Boolean,
    chatUuid: UUID,
    draftChat: Boolean,
    filter: ChatAdministratorsListFilter,
    subscriptionManager: SubscriptionManager,
    networkUtils: NetworkUtils
) : BaseChatSettingsPresenter<ChatAdministratorsListFilter, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback>(
    interactor, uriWrapper, recipientSelectionManager, isNewChat,
        chatUuid, draftChat, filter, subscriptionManager, networkUtils) {

    init {
        mFilter.theme = chatUuid
        setDataFromMyProfile()
    }

    /**@SelfDocumented */
    override fun getListObservableCommand(): BaseListObservableCommand<out PagedListResult<ChatSettingsItem>, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback> =
        interactor.chatAdministratorsCommandWrapper.listCommand

    override fun onAddPersonButtonClicked() {
        recipientSelectionManager.preselect(dataList.filterIsInstance<ChatSettingsContactItem>().map { it.participant.employeeProfile.uuid })
        super.onAddPersonButtonClicked()
    }

    override fun getDataRefreshCallback(): DataRefreshedChatAdministratorsControllerCallback {
        return object: DataRefreshedChatAdministratorsControllerCallback() {
            override fun onEvent() {
                onRefreshCallback(HashMap())
            }
        }
    }
}
