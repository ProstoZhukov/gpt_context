package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import io.reactivex.Observable
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.uri.UriWrapper
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import java.util.*

/**
 * Презентер настроек чата с возможностью добавления участников чата.
 * Используется при создании чата.
 *
 * @param interactor                интерактор настроек чата
 * @param uriWrapper                класс для работы с URI
 * @param recipientSelectionManager компонент, отвечающий за получение результатов выбора участников/администраторов чата
 * @param isNewChat                 true, если новый чат
 * @param chatUuid                  UUID чата
 * @param draftChat                 true, если драфтовый чат
 * @param filter                    фильтр для CRUD-фасада
 * @param subscriptionManager       менеджер для управления подписками и событиями контроллера
 * @param networkUtils              @SelfDocumented
 *
 * @author vv.chekurda
 */
internal class ChatSettingsParticipantsPresenter(
    interactor: ChatSettingsInteractor,
    uriWrapper: UriWrapper,
    recipientSelectionManager: RecipientSelectionResultManager,
    isNewChat: Boolean,
    chatUuid: UUID?,
    draftChat: Boolean,
    filter: ThemeParticipantsListFilter,
    subscriptionManager: SubscriptionManager,
    networkUtils: NetworkUtils,
) : BaseChatSettingsPresenter<ThemeParticipantsListFilter, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>(
    interactor,
    uriWrapper,
    recipientSelectionManager,
    isNewChat,
    chatUuid,
    draftChat,
    filter,
    subscriptionManager,
    networkUtils,
) {

    init {
        if (chatUuid != null) {
            mFilter.theme = chatUuid
        }
        setDataFromMyProfile()
    }

    /**@SelfDocumented */
    override fun getListObservableCommand(): BaseListObservableCommand<out PagedListResult<ChatSettingsItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback> {
        return interactor.chatSettingsCommandWrapper.listCommand
    }

    /**@SelfDocumented */
    override fun getUpdatingListByLastEntityObservable(
        dataModel: ChatSettingsItem?,
        itemsCount: Int,
        fromPullToRefresh: Boolean,
    ): Observable<out PagedListResult<ChatSettingsItem>> {
        return if (draftChat && isNewChat) {
            val participantsUuids = mutableListOf<UUID>()
            userModel?.let { participantsUuids.add(it.uuid) }
            val participantsFromRecipientSelection = getParticipantsFromRecipientSelection().filterNot { it == userModel?.uuid }
            participantsUuids.addAll(participantsFromRecipientSelection)
            interactor.getThemeParticipantList(participantsUuids)
                .flatMapObservable {
                    Observable.just(
                        PagedListResult(
                            it.map { item -> ChatSettingsContactItem(item) },
                            false,
                        ),
                    )
                }
        } else {
            super.getUpdatingListByLastEntityObservable(dataModel!!, itemsCount, fromPullToRefresh)
        }
    }

    override fun getDataRefreshCallback(): DataRefreshedThemeParticipantsControllerCallback {
        return object : DataRefreshedThemeParticipantsControllerCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                onRefreshCallback(param)
            }
        }
    }

    override fun onAddPersonButtonClicked() {
        recipientSelectionManager.preselect(selectedParticipantUuids)
        super.onAddPersonButtonClicked()
    }
}
